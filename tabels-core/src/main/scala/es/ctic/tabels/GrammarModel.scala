package es.ctic.tabels

import es.ctic.tabels.Dimension._
import es.ctic.tabels.TupleType._
import scala.util.matching.Regex

case class S (patternList: Seq[Pattern] = List(), templateList : Seq[Template] = List()) extends Evaluable

case class Pattern ( concretePattern : Either[BindingExpression,LetWhereExpression] ) extends Evaluable{
 
	def accept(vis : Visitor) = {
	    
	    vis.visit(this)
	  }
}
case class LetWhereExpression(filterCondList: Seq[FilterCondition] = List(), position : Option[Position] = None , 
		 tupleOrVariable: Either[Tuple,Variable], childPatterns: Seq[Pattern] = Seq(), expression: Option[Expression]= None) extends Evaluable{
  
	def accept(vis : Visitor) = {
	    
	    vis.visit(this)
	  }
}

case class BindingExpression(dimension : Dimension, filterCondList: Seq[FilterCondition] = List(), 
		pos : Position = null, stopCond : StopCondition = null, variable: Variable = Variable("?_BLANK"),
		childPatterns: Seq[Pattern] = Seq() ) extends Evaluable {
  
  def accept(vis : Visitor) = {
	    
	    vis.visit(this)
	  }
}
/*Forget about PatternMatch, from now on LetWhereExpresion will be the binding leaf node
case class PatternMatch(filterCondList: Seq[FilterCondition] = List(), position : Position = null, 
		stopCond : StopCondition = null, variable: Variable = null, tuple : Tuple = null) extends Evaluable{
  
	def accept(vis : Visitor) = {
	    
	    vis.visit(this)
	  }
}
*/
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



case class Tuple(variables : Seq[Variable] = Seq(), tupleType : TupleType) extends Evaluable {
	
	def accept(vis : Visitor) = {
    
	vis.visit(this)
  }
}

case class Expression (variable : Variable, param : String) extends Evaluable {

  def getResource(value : String) : RDFNode = Resource (param.concat(value))

}
	


case class Assignment(variable : Variable, expression : Expression) extends Evaluable



