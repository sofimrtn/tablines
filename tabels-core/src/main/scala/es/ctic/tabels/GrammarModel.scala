package es.ctic.tabels

import es.ctic.tabels.Dimension._
import es.ctic.tabels.TupleType._
import scala.util.matching.Regex

case class S (prefixes : Seq[(String,Resource)] = List(), patternList: Seq[Pattern] = List(), templateList : Seq[Template] = List()) extends Evaluable

case class Pattern ( concretePattern : Either[DimensionExpression,VariableAssignationExpression] ) extends Evaluable{
 
	def accept(vis : Visitor) = {
	    
	    vis.visit(this)
	  }
}
/*
 * 
 * group of Classes designed to assign values to variables from different sources or methods
 * 
 */
abstract class VariableAssignationExpression extends Evaluable {
	
  def accept(vis : Visitor)
}
case class LetWhereExpression(filter: Option[Expression] = None, position : Option[Position] = None , 
		 variable: Variable, childPatterns: Seq[Pattern] = Seq(), expression: Option[Expression]= None) extends VariableAssignationExpression{
  
	override def accept(vis : Visitor) =  vis.visit(this)
	  
}

case class MatchExpression(filter: Option[Expression] = None, position : Option[Position] = None , 
		 tupleOrVariable: Either[Tuple,Variable], childPatterns: Seq[Pattern] = Seq(), expression: Option[Expression]= None) extends VariableAssignationExpression{
  
	override def accept(vis : Visitor) = vis.visit(this)
}

/*
 * 
 *
 * 
 */
abstract class DimensionExpression(dim : Dimension, v : Variable) extends Evaluable {
  val dimension = dim	
  val variable = v
  def accept(vis : Visitor)
}

case class BindingExpression(override val dimension : Dimension, filter: Option[Expression] = None, 
		pos : Option[Position] = None, stopCond : Option[Expression] = None, override val variable: Variable = Variable("?_BLANK"),
		childPatterns: Seq[Pattern] = Seq() ) extends DimensionExpression(dimension,variable) {
  
  override def accept(vis : Visitor) = vis.visit(this)
}

case class SetInDimensionExpression(override val dimension : Dimension, fixedDimension: String, override val variable: Variable = Variable("?_BLANK"),
		childPatterns: Seq[Pattern] = Seq() ) extends DimensionExpression (dimension, variable){
  
  override def accept(vis : Visitor) = vis.visit(this)
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

case class StopCondition (cond: String) extends Evaluable

case class Variable (name : String) extends Evaluable{
	
  override def toString() : String = name	
	
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



