package es.ctic.tabels

import es.ctic.tabels.Dimension._
import scala.collection.mutable.ListBuffer
import grizzled.slf4j.Logging

abstract class Visitor {
  def visit(s : S)
  def visit(lwexp : LetStatement)
  def visit(iteratorStatement : IteratorStatement)
  def visit(dimensionExp :SetInDimensionStatement)
  def visit(pattMatch : MatchStatement)

}

class AbstractVisitor extends Visitor with Logging {
  override def visit(s : S) = {}
  override def visit(lwexp : LetStatement) = {}
  override def visit(iteratorStatement : IteratorStatement) = {}
  override def visit(dimensionExp :SetInDimensionStatement)= {}
  override def visit(pattMatch : MatchStatement) = {}
}


case class VisitorEvaluate(dataSource : DataSource,events :ListBuffer[Event],evaluationContext: EvaluationContext) extends AbstractVisitor{
 
 
  override def visit(s : S) {
	logger.debug("Visiting root node")
	s.statementList.foreach(p => p.accept(this))
  }
  
  val requiredDimensionMap = Map(Dimension.files -> null, Dimension.sheets -> Dimension.files, Dimension.rows -> Dimension.sheets, Dimension.cols -> Dimension.sheets )
  
  def calculateNewEvaluationContext(dimensionStatement: DimensionStatement, dimensionIterator: String) : EvaluationContext = {
     
    
	 val newEvaluationContext = evaluationContext.addDimension(dimensionStatement.dimension, dimensionIterator)
     val point = new Point(newEvaluationContext.getValue(Dimension.files), newEvaluationContext.getValue(Dimension.sheets), newEvaluationContext.getValue(Dimension.cols).toInt, newEvaluationContext.getValue(Dimension.rows).toInt)
     val value : Literal = dimensionStatement.dimension match{
		    case Dimension.rows =>	dataSource.getValue(point).getContent
		    case Dimension.cols =>	dataSource.getValue(point).getContent
			case Dimension.sheets =>Literal(dimensionIterator)
			case Dimension.files =>	Literal(dimensionIterator)
	}
	dimensionStatement.variable match{
	  case Some(v) => newEvaluationContext.addBinding(dimensionStatement.variable.get, value, point).addBinding(v, value, point)
	  case None => newEvaluationContext
	}
    
  }
  
  
  override def visit(iteratorStatement : IteratorStatement) = {
   
    logger.debug("Visting Iterator statement " + iteratorStatement.dimension)
    
    val requiredDimension = requiredDimensionMap(iteratorStatement.dimension)
    
    if( requiredDimension!=null && !evaluationContext.dimensiones.contains(requiredDimension)){
	  IteratorStatement(variable = Some(Variable("?_" + requiredDimension)), dimension = requiredDimension, childPatterns = Seq(iteratorStatement)).accept(this)
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
			iteratorStatement.childPatterns.foreach(p => p.accept(VisitorEvaluate(dataSource,events, newEvaluationContext)))
	 }
	}
	  
	
  }
  
  override def visit(setDimensionStatement : SetInDimensionStatement) = {
   
    logger.debug("Visting setInDimension statement " + setDimensionStatement.dimension)
    
    val requiredDimension = requiredDimensionMap(setDimensionStatement.dimension)
    
    if( requiredDimension!=null && !evaluationContext.dimensiones.contains(requiredDimension)){
	  IteratorStatement(variable = Some(Variable("?_" + requiredDimension)), dimension = requiredDimension, childPatterns = Seq(setDimensionStatement)).accept(this)
	} 
    else {
     
      val newEvaluationContext = calculateNewEvaluationContext(setDimensionStatement, setDimensionStatement.fixedDimension )    
      setDimensionStatement.variable match{
        case Some(v) =>val event = new Event(newEvaluationContext.bindings, Set(v))
      				   events += event
      	case None =>	
      }
      	
	  setDimensionStatement.childPatterns.foreach(p => p.accept(VisitorEvaluate(dataSource,events, newEvaluationContext)))
    }
	  
	
  }
  
  override def visit(letStatement : LetStatement){
	  	
	  	if( !evaluationContext.dimensiones.contains(Dimension.sheets)){
	    		IteratorStatement(variable = Some(Variable("?_SHEET")), dimension = Dimension.sheets, childPatterns = Seq(letStatement)).accept(this)
	    }else{ 
	  	
		  	logger.debug("Visting let statement" + letStatement)
		  	var newEvaluationContext: EvaluationContext = evaluationContext
	  		 
	  		  		
	  		logger.debug("Matching with file " + dataSource.filenames + " and tab "+ evaluationContext.getValue(Dimension.sheets))
	  		
			var position : Point = letStatement.position match{
		  	  case Some(p) =>	p.calculatePoint(evaluationContext)
								
		  	  case None =>		Point(evaluationContext.getValue(Dimension.files),evaluationContext.getValue(Dimension.sheets),evaluationContext.getValue(Dimension.cols).toInt, evaluationContext.getValue(Dimension.rows).toInt)
								
		  	}
	  		
	  		var event : Event = null		  	
		  	letStatement.expression match{
			  	    case Some(expr) => newEvaluationContext = newEvaluationContext.addBinding(letStatement.variable, expr.evaluate(newEvaluationContext), position)
			  	    case None => newEvaluationContext = newEvaluationContext.addBinding(letStatement.variable, dataSource.getValue(position).getContent, position)
		  	  	}
		  	    					
			event = Event(newEvaluationContext.bindings, Set(letStatement.variable))
			  	    					
		  	
		  	if(letStatement.filter.isEmpty || letStatement.filter.get.evaluate(newEvaluationContext).asBoolean.truthValue){
		  	  events += event
		  	  letStatement.childPatterns.foreach(p => p.accept(VisitorEvaluate(dataSource,events, newEvaluationContext)))
		  	}
	  }
  }
  override def visit(matchStatement : MatchStatement){
	  	
	  	if( !evaluationContext.dimensiones.contains(Dimension.sheets)){
	    		IteratorStatement(variable = Some(Variable("?_SHEET")), dimension = Dimension.sheets, childPatterns = Seq(matchStatement)).accept(this)
	    }else{ 
	  	
		  	logger.debug("Visting match statement" + matchStatement)
		  	var newEvaluationContext: EvaluationContext = evaluationContext
	  		 
	  		  		
	  		logger.debug("Matching with file " + dataSource.filenames + " and tab "+ evaluationContext.getValue(Dimension.sheets))
	  		
			var position : Point = matchStatement.position match{
		  	  case Some(p) =>	p.calculatePoint(evaluationContext)
								
		  	  case None =>		Point(evaluationContext.getValue(Dimension.files),evaluationContext.getValue(Dimension.sheets),evaluationContext.getValue(Dimension.cols).toInt, evaluationContext.getValue(Dimension.rows).toInt)
								
		  	}
	  		
	  		var event : Event = null		  	
		  	
		  	 matchStatement.tuple.variables.foreach(v =>{
		  		  					val node : RDFNode = dataSource.getValue(position).getContent
		  		  					newEvaluationContext = 	newEvaluationContext.addBinding(v, node, position)
		  		  					matchStatement.tuple.tupleType match{
		  		  						case TupleType.horizontal => position = position.RightPoint
		  		  						case TupleType.vertical => position = position.BottomPoint
		  		  					}
		  		  					
		  	  					})
		  	event = Event(newEvaluationContext.bindings, Set(matchStatement.tuple.variables:_*))
		  		  					
		  	 
			if(matchStatement.filter.isEmpty || matchStatement.filter.get.evaluate(newEvaluationContext).asBoolean.truthValue){
		  	  events += event
		  	  matchStatement.childPatterns.foreach(p => p.accept(VisitorEvaluate(dataSource,events, newEvaluationContext)))
		  	}
	  }
  }
 
 }

 
