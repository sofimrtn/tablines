package es.ctic.tabels

case class S (var patternList: List[Pattern], var templateList : List[Template]) extends Evaluable

case class Pattern (letE : LetWhereExpression,var lBindE : List[BindingExpresion] = List() , 
					lPatternM : List[PatternMatch], whereE : LetWhereExpression) extends Evaluable

case class LetWhereExpression(sentList : List[Assingment]) extends Evaluable

case class BindingExpresion(dim : Dimension, filterCondList: List[FilterCondition] = List(), 
		pos : Position = null, stopCond : StopCondition = null, variable: Variable = null) extends Evaluable

case class PatternMatch(filterCondList: List[FilterCondition] = List(), pos : Position = null, 
		stopCond : StopCondition = null, variable: Variable = null, tupple : Tuple = null) extends Evaluable

case class Dimension (dim : String) extends Evaluable

case class FilterCondition (cond : String) extends Evaluable

case class Position (position : String) extends Evaluable

case class StopCondition (cond: String) extends Evaluable

case class Variable(name : String) extends Evaluable

case class Tuple(variables : List[Variable]) extends Evaluable

case class Expression (exp: String) extends Evaluable

case class Assingment(variable : Variable, expression : Expression) extends Evaluable

