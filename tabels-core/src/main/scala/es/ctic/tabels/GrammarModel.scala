package es.ctic.tabels

case class S (var patternList: List[Pattern], var templateList : List[Template]) extends Evaluable

case class Pattern (letE : LetWhereExpression,var lBindE : List[BindingExpresion] = List() , 
					lPatternM : List[PatternMatch], whereE : LetWhereExpression) extends Evaluable

case class LetWhereExpression(sentList : List[Assingment]) extends Evaluable

case class BindingExpresion(dim : Dimension, filterCondList: List[FilterCondition] = List(), 
		pos : Position = null, stopCond : StopCondition = null, variable: Var = null) extends Evaluable

case class PatternMatch(filterCondList: List[FilterCondition] = List(), pos : Position = null, 
		stopCond : StopCondition = null, variable: Var = null, tupple : Tupple = null) extends Evaluable

case class Dimension (dim : String) extends Evaluable

case class FilterCondition (cond : String) extends Evaluable

case class Position (pos : String) extends Evaluable{
  
	def accept(vis : Visitor) = {
	    
	    vis.visit(this)
	  }
}

case class StopCondition (cond: String) extends Evaluable

case class Var (name : String) extends Evaluable{
	
  def accept(vis : Visitor) = {
    
    vis.visit(this)
  }
}



case class Tupple( varList: List[Var]) extends Evaluable

case class Expression (exp: String) extends Evaluable

case class Assingment(v : Var, e : Expression) extends Evaluable



