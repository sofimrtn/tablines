package es.ctic.tabels

import es.ctic.tabels.Dimension._
import es.ctic.tabels.TupleType._
import scala.util.matching.Regex

case class S (prefixes : Seq[(String,Resource)] = List(), patternList: Seq[Pattern] = List(), templateList : Seq[Template] = List()) extends Evaluable

case class Pattern ( concretePattern : Either[BindingExpression,LetWhereExpression] ) extends Evaluable{
 
	def accept(vis : Visitor) = {
	    
	    vis.visit(this)
	  }
}
case class LetWhereExpression(filter: Option[Expression] = None, position : Option[Position] = None , 
		 tupleOrVariable: Either[Tuple,Variable], childPatterns: Seq[Pattern] = Seq(), expression: Option[Expression]= None) extends Evaluable{
  
	def accept(vis : Visitor) = {
	    
	    vis.visit(this)
	  }
}

case class BindingExpression(dimension : Dimension, filter: Option[Expression] = None, 
		pos : Option[Position] = None, stopCond : Option[Expression] = None, variable: Variable = Variable("?_BLANK"),
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


	


case class Assignment(variable : Variable, expression : Expression) extends Evaluable



