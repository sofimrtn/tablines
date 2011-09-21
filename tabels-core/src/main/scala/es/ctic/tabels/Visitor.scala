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


class VisitorEvaluate(dS : DataSource) extends AbstractVisitor{
 
  val dataSource : DataSource = dS
  def events :List[Event] = buffEventList.toList
  private val buffEventList = new ListBuffer[Event]
  
  private var bindings = Bindings(Map())
  private var evaluationContext = EvaluationContext(Map())
  private var varStack : scala.collection.mutable.Stack[Variable] = new scala.collection.mutable.Stack()
  
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
    logger.debug("Visting binding expression")
    // FIXME: this code does not manage context
	for (file <- dataSource.filenames ) {
	  evaluationContext = evaluationContext.addDimension(Dimension.files,file)
	  varStack = varStack.push(bindExp.variable)
	  bindExp.dimension match{
	    case Dimension.rows => for (row <- 0 until dataSource.getRows(file,evaluationContext.getValue(Dimension.sheets))){
	    					val evContext = evaluationContext.addDimension(Dimension.rows, row.toString())
	    					val point = new Point(file, evContext.getValue(Dimension.sheets), evContext.getValue(Dimension.rows).toInt, evContext.getValue(Dimension.cols).toInt)// FIXME: this code does not manage context
	    					bindings = bindings.addBinding(bindExp.variable, dataSource.getValue(point).getContent, point)
					    	val event = new Event(bindings, Set(bindExp.variable))
					    	buffEventList += event
					    	evaluationContext = evContext
					    	bindExp.lBindE.foreach(p => p.accept(this))
					    	bindExp.lPatternM.foreach(p => p.accept(this))
					    	for(index<-0 until varStack.indexOf(bindExp.variable,0))
					   			bindings = bindings.removeBinding(varStack.pop())
					    	evaluationContext = evaluationContext.removeDimension(Dimension.rows)
	    			}
	    case Dimension.cols => for (col <- 0 until dataSource.getCols(file,evaluationContext.getValue(Dimension.sheets))){
	    					val evContext = evaluationContext.addDimension(Dimension.cols, col.toString())
	    					println("columna: \n"+evContext.getValue(Dimension.cols).toInt)
	    					val point = new Point(file, evContext.getValue(Dimension.sheets), evContext.getValue(Dimension.rows).toInt, evContext.getValue(Dimension.cols).toInt)// FIXME: this code does not manage context
	    					bindings = bindings.addBinding(bindExp.variable, dataSource.getValue(point).getContent, point)
					    	val event = new Event(bindings, Set(bindExp.variable))
					    	buffEventList += event
					    	evaluationContext = evContext.addDimension(Dimension.files,file)
					    	bindExp.lBindE.foreach(p => p.accept(this))
					    	bindExp.lPatternM.foreach(p => p.accept(this))
					    	for(index<-0 until varStack.indexOf(bindExp.variable,0))
					   			bindings = bindings.removeBinding(varStack.pop())
					    	evaluationContext = evaluationContext.removeDimension(Dimension.cols)
	    			}
	    case Dimension.sheets => for (sheet <- dataSource.getTabs(file) ){
	    					val evContext = evaluationContext.addDimension(Dimension.sheets, sheet.toString())
	    					val point = new Point(file, evContext.getValue(Dimension.sheets), evContext.getValue(Dimension.rows).toInt, evContext.getValue(Dimension.cols).toInt)// FIXME: this code does not manage context
	    					bindings = bindings.addBinding(bindExp.variable, sheet, point)
					    	val event = new Event(bindings, Set(bindExp.variable))
					    	buffEventList += event
					    	evaluationContext = evContext
					    	bindExp.lBindE.foreach(p => p.accept(this))
					    	bindExp.lPatternM.foreach(p => p.accept(this))
					    	for(index<-0 until varStack.indexOf(bindExp.variable,0)){
					   			bindings = bindings.removeBinding(varStack.pop())}
	    					evaluationContext = evaluationContext.removeDimension(Dimension.sheets)
	      			}
	  }
	}
  }
  
  override def visit(patternMatch : PatternMatch){
	  	logger.debug("Visting pattern match")
	// FIXME: this code does not manage context
  		val evContext = evaluationContext	
  		logger.debug("Matching with file " + dataSource.filenames + " and tab "+ evContext.getValue(Dimension.sheets))
		val point = new Point(evContext.getValue(Dimension.files), evContext.getValue(Dimension.sheets), patternMatch.position.row, patternMatch.position.col)
    	bindings = 	bindings.addBinding(patternMatch.variable, dataSource.getValue(point).getContent, point)
    	val event = new Event(bindings, Set(patternMatch.variable))
    	varStack = varStack.push(patternMatch.variable)
	  	buffEventList += event
    	
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


