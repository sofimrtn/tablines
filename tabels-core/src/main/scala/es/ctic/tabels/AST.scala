package es.ctic.tabels

import es.ctic.tabels.Dimension._
import es.ctic.tabels.TupleType._
import scala.util.matching.Regex

case class S (prefixes : Seq[(String,Resource)] = List(), statementList: Seq[TabelsStatement] = List(), templateList : Seq[Template] = List()) extends Evaluable

abstract class TabelsStatement  extends Evaluable{
 
	def accept(vis : Visitor) =  vis.visit(this)
}
/*
 * 
 * group of Classes designed to assign values to variables from different sources or methods
 * 
 */
abstract class VariableAssignationStatement extends TabelsStatement {
	
  override def accept(vis : Visitor)
}
case class LetStatement(filter: Option[Expression] = None, position : Option[Position] = None , 
		 variable: Variable, childPatterns: Seq[TabelsStatement] = Seq(), expression: Option[Expression]= None) extends VariableAssignationStatement{
  
	override def accept(vis : Visitor) =  vis.visit(this)
	  
}

case class MatchStatement(filter: Option[Expression] = None, position : Option[Position] = None , 
		 tuple: Tuple, childPatterns: Seq[TabelsStatement] = Seq(), expression: Option[Expression]= None) extends VariableAssignationStatement{
  
	override def accept(vis : Visitor) = vis.visit(this)
}

/*
 * 
 *group of Classes designed to move through dimension
 * 
 */
abstract class DimensionStatement extends TabelsStatement {
  val dimension :Dimension = null	
  val variable : Option[Variable] = None
  override def accept(vis : Visitor)
}

case class IteratorStatement(override val dimension : Dimension, filter: Option[Expression] = None, 
		pos : Option[Position] = None, stopCond : Option[Expression] = None, override val variable: Option[Variable] = None,
		childPatterns: Seq[TabelsStatement] = Seq() ) extends DimensionStatement {
  
  override def accept(vis : Visitor) = vis.visit(this)
}

case class SetInDimensionStatement(override val dimension : Dimension, fixedDimension: String, override val variable: Option[Variable] = None,
		childPatterns: Seq[TabelsStatement] = Seq() ) extends DimensionStatement {
  
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
	
  def accept(vis : Visitor) = vis.visit(this)
 
}



case class Tuple(variables : Seq[Variable] = Seq(), tupleType : TupleType = TupleType.horizontal) extends Evaluable {
	
	def accept(vis : Visitor) = vis.visit(this)
  }


	


case class Assignment(variable : Variable, expression : Expression) extends Evaluable



