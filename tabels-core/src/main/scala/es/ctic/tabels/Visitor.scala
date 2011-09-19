package es.ctic.tabels

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


class VisitorEvaluate(dS : DataSource) extends AbstractVisitor{
 
  val dataSource : DataSource = dS
  def events :List[Event] = buffEventList.toList
  private val buffEventList = new ListBuffer[Event]

  val evaluationContext : EvaluationContext = new EvaluationContext()
  
  override def visit(s : S) {
	logger.debug("Visiting root node")
	s.patternList.foreach(p => p.accept(this))
  }
  
  override def visit(pattern : Pattern){
	logger.debug("Visting pattern")
	//FIX ME
	pattern.lBindE.foreach(p => p.accept(this))
	
  }
  
  override def visit(bindExp : BindingExpresion) = {
    logger.debug("Visting binding expression")
    // FIXME: this code does not manage context
	for (file <- dataSource.filenames ; tab <- dataSource.getTabs(file)) {
	  bindExp.dimension match{
	    case Dimension.rows => for (row <- 0 until dataSource.getRows(file,tab) ){
	    					val point = new Point(file, tab, row, 0)// FIXME: this code does not manage context
	    					var bindings = new Bindings
					    	bindings.addBinding(bindExp.variable, dataSource.getValue(point).getContent, point)
					    	val event = new Event(bindings, Set(bindExp.variable))
					    	println(bindExp)
					    	buffEventList += event
					    	bindExp.lBindE.foreach(p => p.accept(this))
					    	bindExp.lPatternM.foreach(p => p.accept(this))
	    			}
	    case Dimension.cols => for (col <- 0 until dataSource.getCols(file,tab) ){
	    					val point = new Point(file, tab, 0, col)// FIXME: this code does not manage context
	    					var bindings = new Bindings
					    	bindings.addBinding(bindExp.variable, dataSource.getValue(point).getContent, point)
					    	val event = new Event(bindings, Set(bindExp.variable))
					    	println(bindExp)
					    	buffEventList += event
					    	bindExp.lBindE.foreach(p => p.accept(this))
					    	bindExp.lPatternM.foreach(p => p.accept(this))
	    			}
	  }
	}
  }
  
  override def visit(patternMatch : PatternMatch){
  	logger.debug("Visting pattern match")
	// FIXME: this code does not manage context
	for (file <- dataSource.filenames ; tab <- dataSource.getTabs(file)) {
		logger.debug("Matching with file " + file + " and tab "+ tab)
    	val point = new Point(file, tab, patternMatch.position.row, patternMatch.position.col)
    	var bindings = new Bindings
    	bindings.addBinding(patternMatch.variable, dataSource.getValue(point).getContent, point)
    	val event = new Event(bindings, Set(patternMatch.variable))
    	println(patternMatch)
    	buffEventList += event
	}
  }
  
  /*override def visit(v : Var){
    print(v.name)
  }
  
  override def visit(pos : Position){
    print(pos.pos)
  }
*/
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


