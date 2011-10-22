package es.ctic.tabels

import es.ctic.tabels.Dimension._
import scala.collection.mutable.ListBuffer
import grizzled.slf4j.Logging

abstract class Visitor {
  def visit(s : S)
  def visit(stmt : BlockStatement)
  def visit(lwexp : LetStatement)
  def visit(iteratorStatement : IteratorStatement)
  def visit(dimensionExp :SetInDimensionStatement)
  def visit(pattMatch : MatchStatement)

}

class AbstractVisitor extends Visitor with Logging {
  override def visit(s : S) = {}
  override def visit(stmt : BlockStatement) = {}
  override def visit(lwexp : LetStatement) = {}
  override def visit(iteratorStatement : IteratorStatement) = {}
  override def visit(dimensionExp :SetInDimensionStatement)= {}
  override def visit(pattMatch : MatchStatement) = {}
}


case class VisitorEvaluate(dataSource : DataSource,events :ListBuffer[Event],evaluationContext: EvaluationContext) extends AbstractVisitor{
 
 
  val requiredDimensionMap = Map(Dimension.files -> null, Dimension.sheets -> Dimension.files, Dimension.rows -> Dimension.sheets, Dimension.cols -> Dimension.sheets )
  
  def calculateNewEvaluationContext(dimensionStatement: DimensionStatement, dimensionIterator: String) : EvaluationContext = {
	 val newEvaluationContext = evaluationContext.addDimension(dimensionStatement.dimension, dimensionIterator)
     val cursor : Point = newEvaluationContext.cursor
     val value : Literal = dimensionStatement.dimension match{
		    case Dimension.rows =>	dataSource.getValue(cursor).getContent
		    case Dimension.cols =>	dataSource.getValue(cursor).getContent
			case Dimension.sheets =>Literal(dimensionIterator)
			case Dimension.files =>	Literal(dimensionIterator)
	}
	dimensionStatement.variable match{
	  case Some(v) => newEvaluationContext.addBinding(dimensionStatement.variable.get, value, cursor).addBinding(v, value, cursor)
	  case None => newEvaluationContext
	}
  }  
  
  override def visit(s : S) {
	logger.debug("Visiting root node")
	s.statementList.foreach(p => p.accept(this))
  }
  
    override def visit(stmt : BlockStatement) = {
        // evaluates its children in independent evaluation contexts
        stmt.childStatements.foreach(p => p.accept(VisitorEvaluate(dataSource,events,evaluationContext)))      
    }
  
  override def visit(iteratorStatement : IteratorStatement) = {
   
    logger.debug("Visting Iterator statement " + iteratorStatement.dimension)
    
    val requiredDimension = requiredDimensionMap(iteratorStatement.dimension)
    
    if( requiredDimension!=null && !evaluationContext.dimensions.contains(requiredDimension)){
	  IteratorStatement(variable = Some(Variable("?_" + requiredDimension)), dimension = requiredDimension, nestedStatement = Some(iteratorStatement)).accept(this)
	} 
    else {
      val dimensionValues = dataSource.getDimensionRange(iteratorStatement.dimension, evaluationContext)
      val evaluationContexts = dimensionValues map (calculateNewEvaluationContext(iteratorStatement, _))
      val pairsMap = dimensionValues zip evaluationContexts
      val filteredPairs = pairsMap takeWhile(pair => iteratorStatement.stopCond.isEmpty ||iteratorStatement.stopCond.get.evaluate(pair._2).asBoolean.truthValue) filter
      					(pair => iteratorStatement.filter.isEmpty ||iteratorStatement.filter.get.evaluate(pair._2).asBoolean.truthValue)
      
      for ((dimensionIterator, newEvaluationContext) <- filteredPairs){
    	logger.debug("Iteration through " + iteratorStatement.dimension+" in position "+dimensionIterator )
    	        		
	    	iteratorStatement.variable match{
	    	case Some(v) =>val event = new Event(newEvaluationContext.bindings, Set(v))
	    				   events += event
	    	case None =>			   
    		}
			iteratorStatement.nestedStatement.map(p => p.accept(VisitorEvaluate(dataSource,events, newEvaluationContext)))
	 }
	}
	  
	
  }
  
  override def visit(setDimensionStatement : SetInDimensionStatement) = {
   
    logger.debug("Visting setInDimension statement " + setDimensionStatement.dimension)
    
    val requiredDimension = requiredDimensionMap(setDimensionStatement.dimension)
    
    if( requiredDimension!=null && !evaluationContext.dimensions.contains(requiredDimension)){
	  IteratorStatement(variable = Some(Variable("?_" + requiredDimension)), dimension = requiredDimension, nestedStatement = Some(setDimensionStatement)).accept(this)
	} 
    else {
     
      val newEvaluationContext = calculateNewEvaluationContext(setDimensionStatement, setDimensionStatement.fixedDimension )    
      setDimensionStatement.variable match{
        case Some(v) =>val event = new Event(newEvaluationContext.bindings, Set(v))
      				   events += event
      	case None =>	
      }
      	
	  setDimensionStatement.nestedStatement.map(p => p.accept(VisitorEvaluate(dataSource,events, newEvaluationContext)))
    }
	  
	
  }
  
    override def visit(letStatement : LetStatement){
        logger.debug("Visting let statement" + letStatement)
        logger.debug("Matching with file " + dataSource.filenames + " and tab "+ evaluationContext.getValue(Dimension.sheets))
	    var cursor : Point = evaluationContext.cursor // FIXME: may fail for LET stmts at file or sheet level
	    val value = letStatement.expression.evaluate(evaluationContext)
	    val newEvaluationContext = evaluationContext.addBinding(letStatement.variable, value, cursor)
	    val event = Event(newEvaluationContext.bindings, Set(letStatement.variable))
        events += event
        letStatement.nestedStatement.map(p => p.accept(VisitorEvaluate(dataSource,events, newEvaluationContext)))
    }
  
  override def visit(matchStatement : MatchStatement){
	  	
	  	if( !evaluationContext.dimensions.contains(Dimension.sheets)){
	    		IteratorStatement(variable = Some(Variable("?_SHEET")), dimension = Dimension.sheets, nestedStatement = Some(matchStatement)).accept(this)
	    }else{ 
	  	
		  	logger.debug("Visting match statement" + matchStatement)
	  		logger.debug("Matching with file " + dataSource.filenames + " and tab "+ evaluationContext.getValue(Dimension.sheets))
	  		
			var position : Point = matchStatement.position match{
		  	  case Some(p) =>	p.calculatePoint(evaluationContext)								
		  	  case None =>		evaluationContext.cursor
								
		  	}
	  		
		  	var newEvaluationContext: EvaluationContext = evaluationContext
		  	 matchStatement.tuple.variables.foreach(v =>{
		  		  					val value = dataSource.getValue(position).getContent
		  		  					newEvaluationContext = newEvaluationContext.addBinding(v, value, position)
		  		  					matchStatement.tuple.tupleType match{
		  		  						case TupleType.horizontal => position = position.RightPoint
		  		  						case TupleType.vertical => position = position.BottomPoint
		  		  					}
		  		  					
		  	  					})
		  	val event = Event(newEvaluationContext.bindings, Set(matchStatement.tuple.variables:_*))
		  		  					
		  	 
			if (matchStatement.filter map (_.evaluate(newEvaluationContext).asBoolean.truthValue) getOrElse true) {
		  	  events += event
		  	  matchStatement.nestedStatement.map(p => p.accept(VisitorEvaluate(dataSource,events, newEvaluationContext)))
		  	}
	  }
  }
 
 }

 
