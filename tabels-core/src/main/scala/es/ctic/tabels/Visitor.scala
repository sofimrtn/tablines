package es.ctic.tabels

import es.ctic.tabels.Dimension._
import scala.collection.mutable.ListBuffer
import grizzled.slf4j.Logging

abstract class Visitor {
  def visit(s : S)
  def visit(patt : Pattern)
  def visit(lwexp : LetWhereExpression)
  def visit(bindExp : BindingExpresion)
  def visit(pattMatch : PatternMatch)
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
  override def visit(bindExp : BindingExpresion) = {}
  override def visit(pattMatch : PatternMatch) = {}
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
	pattern.lBindE.foreach(p => p.accept(this))
	pattern.lPatternM.foreach(p => p.accept(this))
	
  }
  
  override def visit(bindExp : BindingExpresion) = {
    var newEvaluationContext: EvaluationContext = evaluationContext
    logger.debug("Visting binding expression")
    
    bindExp.dimension match{
	    case Dimension.rows =>if( !evaluationContext.dimensiones.contains(Dimension.sheets)){
	    							BindingExpresion(dimension = Dimension.sheets, lBindE= Seq(bindExp)).accept(this)
	    						} else {
	      	      					for (row <- 0 until dataSource.getRows(evaluationContext.getValue(Dimension.files),evaluationContext.getValue(Dimension.sheets))){
	      	      						val point = new Point(evaluationContext.getValue(Dimension.files), evaluationContext.getValue(Dimension.sheets), row, evaluationContext.getValue(Dimension.cols).toInt)
				    				   	newEvaluationContext = evaluationContext.addDimension(Dimension.rows, row.toString()).addBinding(bindExp.variable, dataSource.getValue(point).getContent, point)
				    					val event = new Event(newEvaluationContext.bindings, Set(bindExp.variable))
								    	events += event
								    	bindExp.lBindE.foreach(p => p.accept(VisitorEvaluate(dataSource,events, newEvaluationContext)))
								    	bindExp.lPatternM.foreach(p => p.accept(VisitorEvaluate(dataSource,events, newEvaluationContext)))
	
	      	      					}
	    						}
	    case Dimension.cols =>if( !evaluationContext.dimensiones.contains(Dimension.sheets)){
	    							BindingExpresion(dimension = Dimension.sheets, lBindE= Seq(bindExp)).accept(this)
	    						} else {
	      	      					for (col <- 0 until dataSource.getCols(evaluationContext.getValue(Dimension.files),evaluationContext.getValue(Dimension.sheets))){
	      	      						val point = new Point(evaluationContext.getValue(Dimension.files), evaluationContext.getValue(Dimension.sheets), evaluationContext.getValue(Dimension.rows).toInt, col)
				    					newEvaluationContext = evaluationContext.addDimension(Dimension.cols, col.toString()).addBinding(bindExp.variable, dataSource.getValue(point).getContent, point)
				    					val event = new Event(newEvaluationContext.bindings, Set(bindExp.variable))
								    	events += event
								    	bindExp.lBindE.foreach(p => p.accept(VisitorEvaluate(dataSource,events, newEvaluationContext)))
								    	bindExp.lPatternM.foreach(p => p.accept(VisitorEvaluate(dataSource,events, newEvaluationContext)))
	      	      					}
	    						}
	    case Dimension.sheets =>if( !evaluationContext.dimensiones.contains(Dimension.files)){
	    							BindingExpresion(dimension = Dimension.files, lBindE= Seq(bindExp)).accept(this)
	    						} else {
	      	      					for (sheet <- dataSource.getTabs(evaluationContext.getValue(Dimension.files)) ){
	      	      						val point = new Point(evaluationContext.getValue(Dimension.files), sheet, evaluationContext.getValue(Dimension.rows).toInt, evaluationContext.getValue(Dimension.cols).toInt)
				    				   	newEvaluationContext = evaluationContext.addDimension(Dimension.sheets, sheet.toString()).addBinding(bindExp.variable, sheet, point)
				    					val event = new Event(newEvaluationContext.bindings, Set(bindExp.variable))
								    	events += event
								    	bindExp.lBindE.foreach(p => p.accept(VisitorEvaluate(dataSource,events, newEvaluationContext)))
								    	bindExp.lPatternM.foreach(p => p.accept(VisitorEvaluate(dataSource,events, newEvaluationContext)))
	      	      					}
	    						}
	    
	    case Dimension.files => for (file <- dataSource.filenames ){
	    					 			val point = new Point(file, evaluationContext.getValue(Dimension.sheets), evaluationContext.getValue(Dimension.rows).toInt, evaluationContext.getValue(Dimension.cols).toInt)
				    					newEvaluationContext = evaluationContext.addDimension(Dimension.files, file).addBinding(bindExp.variable, file, point)
				    					val event = new Event(newEvaluationContext.bindings, Set(bindExp.variable))
								    	events += event
								    	bindExp.lBindE.foreach(p => p.accept(VisitorEvaluate(dataSource,events, newEvaluationContext)))
								    	bindExp.lPatternM.foreach(p => p.accept(VisitorEvaluate(dataSource,events, newEvaluationContext)))
	    							}
	    						
	  }
	  
	
  }
  
  override def visit(patternMatch : PatternMatch){
	  	logger.debug("Visting pattern match")
	
  		var row: Int = 0
  		var col:Int = 0
  		logger.debug("Matching with file " + dataSource.filenames + " and tab "+ evaluationContext.getValue(Dimension.sheets))
  		
		if(patternMatch.position.row == -1 | patternMatch.position.col== -1){
			row = evaluationContext.getValue(Dimension.rows).toInt
			col = evaluationContext.getValue(Dimension.cols).toInt
		}
		else{
			row = patternMatch.position.row
			col = patternMatch.position.col
		}
	  	
	  	val point = Point(evaluationContext.getValue(Dimension.files), evaluationContext.getValue(Dimension.sheets), row, col)
	  	patternMatch.filterCondList.foreach(filter => 	if(!filter.filterValue(dataSource.getValue(point).getContent)){return})
	  	val newEvaluationContext = 	evaluationContext.addBinding(patternMatch.variable, dataSource.getValue(point).getContent, point)
    	val event = new Event(newEvaluationContext.bindings, Set(patternMatch.variable))
    	events += event
    	
  }
 }
  
 
class VisitorToString extends AbstractVisitor{
  
  override def visit(pattMatch : PatternMatch){
  
    pattMatch.variable.accept(this)
    print("  in CELL ")
    pattMatch.position.accept(this)
  }
  
  override def visit(v : Variable){
    print(v.name)
  }
  
  override def visit(position : Position){
    print(position)
  }
  
}


