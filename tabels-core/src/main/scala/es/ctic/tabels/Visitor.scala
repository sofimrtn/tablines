package es.ctic.tabels

import es.ctic.tabels.Dimension._
import scala.collection.mutable.ListBuffer
import grizzled.slf4j.Logging

abstract class Visitor {
  def visit(s : S)
  def visit(patt : Pattern)
  def visit(lwexp : LetWhereExpression)
  def visit(bindExp : BindingExpression)
 // def visit(pattMatch : PatternMatch)
  def visit(filtCond : FilterCondition)
  def visit(pos : Position)
  def visit(sCond : StopCondition)
  def visit(v : Variable)
  def visit(tupple : Tuple)
  def visit(exp : Expression)
  def visit(assing : Assignment)

}

class AbstractVisitor extends Visitor with Logging {
  override def visit(s : S) = {}
  override def visit(patt : Pattern) = {}
  override def visit(lwexp : LetWhereExpression) = {}
  override def visit(bindExp : BindingExpression) = {}
  //override def visit(pattMatch : PatternMatch) = {}
  override def visit(filtCond : FilterCondition) = {}
  override def visit(pos : Position) = {}
  override def visit(sCond : StopCondition) = {}
  override def visit(v : Variable) = {}
  override def visit(tupple : Tuple) = {}
  override def visit(exp : Expression) = {}
  override def visit(assing : Assignment) = {}
}


case class VisitorEvaluate(dataSource : DataSource,events :ListBuffer[Event],evaluationContext: EvaluationContext) extends AbstractVisitor{
 
 
  override def visit(s : S) {
	logger.debug("Visiting root node")
	s.patternList.foreach(p => p.accept(this))
  }
  
  override def visit(pattern : Pattern){
	logger.debug("Visting pattern")
	//FIX ME
	
	pattern.concretePattern match{
	  case Left(binding) => binding.accept(this)
	  case Right(let) => let.accept(this)
	}
	
	
	
  }
  
  val requiredDimensionMap = Map(Dimension.files -> null, Dimension.sheets -> Dimension.files, Dimension.rows -> Dimension.sheets, Dimension.cols -> Dimension.sheets )
  
  def calculateNewEvaluationContext(bindingExpression: BindingExpression, dimensionIterator: String) : EvaluationContext = {
     
    
	 val newEvaluationContext = evaluationContext.addDimension(bindingExpression.dimension, dimensionIterator)
     val point = new Point(newEvaluationContext.getValue(Dimension.files), newEvaluationContext.getValue(Dimension.sheets), newEvaluationContext.getValue(Dimension.cols).toInt, newEvaluationContext.getValue(Dimension.rows).toInt)
     val value : Literal = bindingExpression.dimension match{
		    case Dimension.rows =>	dataSource.getValue(point).getContent
		    case Dimension.cols =>	dataSource.getValue(point).getContent
			case Dimension.sheets =>Literal(dimensionIterator)
			case Dimension.files =>	Literal(dimensionIterator)
	}
		
    return newEvaluationContext.addBinding(bindingExpression.variable, value, point)
  }
  
  
  override def visit(bindExp : BindingExpression) = {
   
    logger.debug("Visting binding expression " + bindExp.dimension)
    
    val requiredDimension = requiredDimensionMap(bindExp.dimension)
    
    if( requiredDimension!=null && !evaluationContext.dimensiones.contains(requiredDimension)){
	  BindingExpression(variable = Variable("?_" + requiredDimension), dimension = requiredDimension, childPatterns = Seq(Pattern(Left(bindExp)))).accept(this)
	} 
    else {
      val dimensionValues = dataSource.getDimensionRange(bindExp.dimension, evaluationContext)
      val evaluationContexts = dimensionValues map (calculateNewEvaluationContext(bindExp, _))
      val pairsMap = dimensionValues zip evaluationContexts
      val filteredPairs = pairsMap takeWhile(pair => bindExp.stopCond.isEmpty ||bindExp.stopCond.get.evaluate(pair._2).asBoolean.truthValue) filter
      					(pair => bindExp.filter.isEmpty ||bindExp.filter.get.evaluate(pair._2).asBoolean.truthValue)
      
      for ((dimensionIterator, newEvaluationContext) <- filteredPairs){
    	logger.debug("Iteration through " + bindExp.dimension+" in position "+dimensionIterator )
    	        		
	    	val event = new Event(newEvaluationContext.bindings, Set(bindExp.variable))
			events += event
			bindExp.childPatterns.foreach(p => p.accept(VisitorEvaluate(dataSource,events, newEvaluationContext)))
	 }
	}
	  
	
  }
  override def visit(letWhereExpression : LetWhereExpression){
	  	
	  	if( !evaluationContext.dimensiones.contains(Dimension.sheets)){
	    		BindingExpression(variable = Variable("?_SHEET"), dimension = Dimension.sheets, childPatterns = Seq(Pattern(Right(letWhereExpression)))).accept(this)
	    }else{ 
	  	
		  	logger.debug("Visting let/where expression" + letWhereExpression)
		  	var newEvaluationContext: EvaluationContext = evaluationContext
	  		 
	  		  		
	  		logger.debug("Matching with file " + dataSource.filenames + " and tab "+ evaluationContext.getValue(Dimension.sheets))
	  		
			var position : Point = letWhereExpression.position match{
		  	  case Some(p) =>	p.getPosition(evaluationContext)
								
		  	  case None =>		Point(evaluationContext.getValue(Dimension.files),evaluationContext.getValue(Dimension.sheets),evaluationContext.getValue(Dimension.cols).toInt, evaluationContext.getValue(Dimension.rows).toInt)
								
		  	}
	  		
	  		var event : Event = null		  	
		  	letWhereExpression.tupleOrVariable match{
		  	  case Left(tuple) =>	tuple.variables.foreach(v =>{
			  	  					//letWhereExpression.filterCondList.foreach(filter => 	if(!filter.filterValue(dataSource.getValue(point).getContent)){return})
		  		  					newEvaluationContext = 	newEvaluationContext.addBinding(v, dataSource.getValue(position).getContent, position)
		  		  					tuple.tupleType match{
		  		  						case TupleType.horizontal => position = position.RightPoint
		  		  						case TupleType.vertical => position = position.BottomPoint
		  		  					}
		  		  					
		  	  						})
		  	  						event = Event(newEvaluationContext.bindings, Set(tuple.variables:_*))
		  		  					
		  	  case Right(variable) =>letWhereExpression.expression match{
			  	    					case Some(expr) => newEvaluationContext = newEvaluationContext.addBinding(variable, expr.evaluate(newEvaluationContext), position)
			  	    					case None => newEvaluationContext = newEvaluationContext.addBinding(variable, dataSource.getValue(position).getContent, position)
		  	  						}
		  	    					
			  	  					event = Event(newEvaluationContext.bindings, Set(variable))
			  	    					
		  	}
		  	if(letWhereExpression.filter.isEmpty || letWhereExpression.filter.get.evaluate(newEvaluationContext).asBoolean.truthValue){
		  	  events += event
		  	  letWhereExpression.childPatterns.foreach(p => p.accept(VisitorEvaluate(dataSource,events, newEvaluationContext)))
		  	}
	  }
  }
 }

 
class VisitorToString extends AbstractVisitor{
  
  override def visit(letWhereExpression : LetWhereExpression){
  
   letWhereExpression.tupleOrVariable fold(_.accept(this),_ accept(this))
       
   letWhereExpression.position map( _.toString)
      
  }
  
  override def visit(v : Variable){
    print(v.name)
  }
  
  override def visit(position : Position){
    print(position)
  }
  
}


