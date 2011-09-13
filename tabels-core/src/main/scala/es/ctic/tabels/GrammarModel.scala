package es.ctic.tabels

case class S (patternList: Seq[Pattern] = List(), templateList : Seq[Template] = List()) extends Evaluable

case class Pattern (lPatternM : Seq[PatternMatch], letE : LetWhereExpression = LetWhereExpression(), lBindE : List[BindingExpresion] = List() , 
					whereE : LetWhereExpression = LetWhereExpression() ) extends Evaluable{
  
	def accept(vis : Visitor) = {
	    
	    vis.visit(this)
	  }
}
case class LetWhereExpression(sentList : Seq[Assignment] = List()) extends Evaluable

case class BindingExpresion(dim : Dimension, filterCondList: Seq[FilterCondition] = List(), 
		pos : Position = null, stopCond : StopCondition = null, variable: Variable = null) extends Evaluable

case class PatternMatch(filterCondList: Seq[FilterCondition] = List(), position : Position = null, 
		stopCond : StopCondition = null, variable: Variable = null, tupple : Tuple = null) extends Evaluable{
  
	def accept(vis : Visitor) = {
	    
	    vis.visit(this)
	  }
}

case class Dimension (dim : String) extends Evaluable

case class FilterCondition (cond : String) extends Evaluable

case class Position (row : Int, col: Int) extends Evaluable {
	
	override def toString() : String = intToAlpha(col) + (row+1)
	
	def intToAlpha(i : Int) : String = {
		
		
		var value:Double = (i / 26)-1
		var resto :Double = i % 26  
		var alphaCol : String = ""
		  
		while(value >= 0)
		{
			
			alphaCol += (resto + 'A').toChar.toString
			resto = value % 26
			value= (value / 26)-1
		}
		alphaCol += (resto + 'A').toChar.toString
		
		return alphaCol.reverse
	}
  
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



