case class Pattern (letE : LetWhereExpression,var lBindE : List[BindingExpresion] = List() , 
					lPatternM : List[PatternMatch], whereE : LetWhereExpression) {
  
}

case class LetWhereExpression(sentList : List[Assignment]) {
  
    
}

case class BindingExpresion {

}

case class PatternMatch {

}

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

case class Tupple{

}

case class Expression {

}

case class Assignment(v : Var, e : Expression){
  
    
}