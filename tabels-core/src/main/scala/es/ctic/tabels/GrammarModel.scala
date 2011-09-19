package es.ctic.tabels

case class S (patternList: Seq[Pattern] = List(), templateList : Seq[Template] = List()) extends Evaluable

case class Pattern (lBindE : Seq[BindingExpresion]  , letE : LetWhereExpression = LetWhereExpression(),  
					whereE : LetWhereExpression = LetWhereExpression(), patternList: Seq[Pattern] = List(),lPatternM : Seq[PatternMatch] = List() ) extends Evaluable{
  
	def accept(vis : Visitor) = {
	    
	    vis.visit(this)
	  }
}
case class LetWhereExpression(sentList : Seq[Assignment] = List()) extends Evaluable

case class BindingExpresion(pattern:Pattern = null,dim : Dimension, filterCondList: Seq[FilterCondition] = List(), 
		pos : Position = null, stopCond : StopCondition = null, variable: Variable = null,lPatternM : Seq[PatternMatch] = List(),lBindE : Seq[BindingExpresion]  ) extends Evaluable {
  
  def accept(vis : Visitor) = {
	    
	    vis.visit(this)
	  }
}

case class PatternMatch(filterCondList: Seq[FilterCondition] = List(), position : Position = null, 
		stopCond : StopCondition = null, variable: Variable = null, tupple : Tuple = null) extends Evaluable{
  
	def accept(vis : Visitor) = {
	    
	    vis.visit(this)
	  }
}

case class Dimension (dim : String) extends Evaluable

case class FilterCondition (cond : String) extends Evaluable

case class Position (row : Int, col: Int) extends Evaluable {
	
	override def toString() : String = columnConverter.intToAlpha(col) + (row+1)
	
	def accept(vis : Visitor) = {
	    
	    vis.visit(this)
	  }
}

case class StopCondition (cond: String) extends Evaluable

case class Variable (name : String) extends Evaluable{
	
  def accept(vis : Visitor) = {
    
    vis.visit(this)
  }
}



case class Tuple(variables : Seq[Variable]) extends Evaluable

case class Expression (exp: String) extends Evaluable

case class Assignment(variable : Variable, expression : Expression) extends Evaluable



