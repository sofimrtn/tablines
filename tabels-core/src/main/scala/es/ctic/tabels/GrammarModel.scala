package es.ctic.tabels

case class S (patternList: List[Pattern], templateList : List[Template])

case class Pattern (letE : LetWhereExpression,var lBindE : List[BindingExpresion] = List() , 
					lPatternM : List[PatternMatch], whereE : LetWhereExpression)

case class LetWhereExpression(sentList : List[Assignment]) 

case class BindingExpresion(dim : Dimension, filterCondList: List[FilterCondition] = List(), pos : Position = null, stopCond : StopCondition = null, variable: Var = null)

case class PatternMatch(filterCondList: List[FilterCondition] = List(), pos : Position = null, stopCond : StopCondition = null, variable: Var = null, tupple : Tupple = null)

case class Dimension {

}

case class FilterCondition {

}

case class Position {

}

case class StopCondition {

}

case class Var {

}

case class Tupple( varList: List[Var]){

}

case class Expression {

}

case class Assignment(v : Var, e : Expression){
  
    
}