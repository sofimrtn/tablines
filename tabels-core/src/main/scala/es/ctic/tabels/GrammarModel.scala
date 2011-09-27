package es.ctic.tabels

import es.ctic.tabels.Dimension._
import es.ctic.tabels.TupleType._
import scala.util.matching.Regex

case class S (patternList: Seq[Pattern] = List(), templateList : Seq[Template] = List()) extends Evaluable

case class Pattern (lBindE : Seq[BindingExpresion]  , letE : LetWhereExpression = LetWhereExpression(),  
					whereE : LetWhereExpression = LetWhereExpression(), patternList: Seq[Pattern] = List(),lPatternM : Seq[PatternMatch] = List() ) extends Evaluable{
  
	def accept(vis : Visitor) = {
	    
	    vis.visit(this)
	  }
}
case class LetWhereExpression(sentList : Seq[Assignment] = List()) extends Evaluable

case class BindingExpresion(dimension : Dimension, filterCondList: Seq[FilterCondition] = List(), 
		pos : Position = null, stopCond : StopCondition = null, variable: Variable = Variable("?0"),lPatternM : Seq[PatternMatch] = List(),lBindE : Seq[BindingExpresion] = List() ) extends Evaluable {
  
  def accept(vis : Visitor) = {
	    
	    vis.visit(this)
	  }
}

case class PatternMatch(filterCondList: Seq[FilterCondition] = List(), position : Position = null, 
		stopCond : StopCondition = null, variable: Variable = null, tuple : Tuple = null) extends Evaluable{
  
	def accept(vis : Visitor) = {
	    
	    vis.visit(this)
	  }
}

case class FilterCondition (condition : String) extends Evaluable {
  
  def filterValue(value : String): Boolean = {
    val cond = new Regex("""("""+condition+""")""")
    value match {
      case cond(regularExp) => false
      case _ => true
    }
  }
}

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



case class Tuple(variables : Seq[Variable] = Seq(), tupleType : TupleType) extends Evaluable

case class Expression (exp: String) extends Evaluable

case class Assignment(variable : Variable, expression : Expression) extends Evaluable



