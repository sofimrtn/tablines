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
  
  override def visit(bindExp : BindingExpression) = {
    var newEvaluationContext: EvaluationContext = evaluationContext
    logger.debug("Visting binding expression")
    
    bindExp.dimension match{
	    case Dimension.rows =>if( !evaluationContext.dimensiones.contains(Dimension.sheets)){
	    							BindingExpression(variable = Variable("?_SHEET"), dimension = Dimension.sheets, childPatterns = Seq(Pattern(Left(bindExp)))).accept(this)
	    						} else {
	      	      					for (row <- 0 until dataSource.getRows(evaluationContext.getValue(Dimension.files),evaluationContext.getValue(Dimension.sheets))){
	      	      						val point = new Point(evaluationContext.getValue(Dimension.files), evaluationContext.getValue(Dimension.sheets), evaluationContext.getValue(Dimension.cols).toInt, row)
				    				   	newEvaluationContext = evaluationContext.addDimension(Dimension.rows, row.toString()).addBinding(bindExp.variable, Literal(dataSource.getValue(point).getContent), point)
				    					val event = new Event(newEvaluationContext.bindings, Set(bindExp.variable))
								    	events += event
								    	bindExp.childPatterns.foreach(p => p.accept(VisitorEvaluate(dataSource,events, newEvaluationContext)))
	
	      	      					}
	    						}
	    case Dimension.cols =>if( !evaluationContext.dimensiones.contains(Dimension.sheets)){
	    							BindingExpression(variable = Variable("?_SHEET"), dimension = Dimension.sheets, childPatterns = Seq(Pattern(Left(bindExp)))).accept(this)
	    						} else {
	      	      					for (col <- 0 until dataSource.getCols(evaluationContext.getValue(Dimension.files),evaluationContext.getValue(Dimension.sheets))){
	      	      						val point = new Point(evaluationContext.getValue(Dimension.files), evaluationContext.getValue(Dimension.sheets), col, evaluationContext.getValue(Dimension.rows).toInt)
				    					newEvaluationContext = evaluationContext.addDimension(Dimension.cols, col.toString()).addBinding(bindExp.variable, Literal(dataSource.getValue(point).getContent), point)
				    					val event = new Event(newEvaluationContext.bindings, Set(bindExp.variable))
								    	events += event
								    	bindExp.childPatterns.foreach(p => p.accept(VisitorEvaluate(dataSource,events, newEvaluationContext)))
	      	      					}
	    						}
	    case Dimension.sheets =>if( !evaluationContext.dimensiones.contains(Dimension.files)){
	    							BindingExpression(variable = Variable("?_FILE"),dimension = Dimension.files, childPatterns = Seq(Pattern(Left(bindExp)))).accept(this)
	    						} else {
	      	      					for (sheet <- dataSource.getTabs(evaluationContext.getValue(Dimension.files)) ){
	      	      						val  point = new Point(evaluationContext.getValue(Dimension.files), sheet, evaluationContext.getValue(Dimension.cols).toInt, evaluationContext.getValue(Dimension.rows).toInt)
				    				   	newEvaluationContext = evaluationContext.addDimension(Dimension.sheets, sheet.toString()).addBinding(bindExp.variable, Literal(sheet), point)
				    					val event = new Event(newEvaluationContext.bindings, Set(bindExp.variable))
								    	events += event
								    	bindExp.childPatterns.foreach(p => p.accept(VisitorEvaluate(dataSource,events, newEvaluationContext)))
	      	      					}
	    						}
	    
	    case Dimension.files => for (file <- dataSource.filenames ){
	    					 			val point = new Point(file, evaluationContext.getValue(Dimension.sheets), evaluationContext.getValue(Dimension.cols).toInt, evaluationContext.getValue(Dimension.rows).toInt)
				    					newEvaluationContext = evaluationContext.addDimension(Dimension.files, file).addBinding(bindExp.variable, Literal(file), point)
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
	  		
	  				  	
		  	letWhereExpression.tupleOrVariable match{
		  	  case Left(tuple) =>	tuple.variables.foreach(v =>{
			  	  					//letWhereExpression.filterCondList.foreach(filter => 	if(!filter.filterValue(dataSource.getValue(point).getContent)){return})
		  		  					newEvaluationContext = 	newEvaluationContext.addBinding(v, Literal(dataSource.getValue(position).getContent), position)
		  		  					tuple.tupleType match{
		  		  						case TupleType.horizontal => position = position.RightPoint
		  		  						case TupleType.vertical => position = position.BottomPoint
		  		  					}
		  		  					
		  	  						})
		  	  						val event = new Event(newEvaluationContext.bindings, Set(tuple.variables:_*))
		  		  					events += event
		  	  case Right(variable) =>letWhereExpression.filterCondList.foreach(filter => 	
		  	    					if(!filter.filterValue(dataSource.getValue(position).getContent)){return})
					  	
		  	    					letWhereExpression.expression match{
			  	    					case Some(expr) => newEvaluationContext = newEvaluationContext.addBinding(variable, expr.evaluate(newEvaluationContext), position)
			  	    					case None => newEvaluationContext = newEvaluationContext.addBinding(variable, Literal(dataSource.getValue(position).getContent), position)
		  	  						}
		  	    					val event = new Event(newEvaluationContext.bindings, Set(variable))
		  	    					events += event
		  	}
		  	letWhereExpression.childPatterns.foreach(p => p.accept(VisitorEvaluate(dataSource,events, newEvaluationContext)))
	 	    	
	  }
  }
 }

 
class VisitorToString extends AbstractVisitor{
  
  override def visit(letWhereExpression : LetWhereExpression){
  
   letWhereExpression.tupleOrVariable fold(_.accept(this),_ accept(this))
       
   letWhereExpression.position map( _.accept(this))
      
  }
  
  override def visit(v : Variable){
    print(v.name)
  }
  
  override def visit(position : Position){
    print(position)
  }
  
}


