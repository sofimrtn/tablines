package es.ctic.tabels

import es.ctic.tabels.Dimension._
import es.ctic.tabels.TupleType._
import scala.util.matching.Regex

abstract class ASTNode {

	def accept(vis : Visitor)

}

case class S (directives : Seq[Directive] = Seq(), prefixes : Seq[(String,NamedResource)] = List(), statementList: Seq[TabelsStatement] = List(), templateList : Seq[Template] = List()) extends ASTNode {

    val prefixesAsMap : Map[String, NamedResource] = Map() ++ prefixes
    
    override def accept(vis : Visitor) = vis.visit(this)
    
}

class Directive

case class FetchDirective(regex : Regex) extends Directive {
    override def toString() = "@FETCH(\"" + regex.toString() + "\")"
}

case class JenaRuleDirective(jenaRule : String) extends Directive {
	 if(!(jenaRule.toLowerCase.contains("http://idi.fundacionctic.org/scovoxl/scovoxl")|jenaRule.toLowerCase.contains("/idi.fundacionctic.org/tabels/project"))&(jenaRule.toLowerCase.contains("192.168.")|jenaRule.toLowerCase.contains("fundacionctic")))
		throw new ServerReferedURIException(jenaRule)
	 override def toString() = "@JENARULE(\"" + jenaRule + "\")"
}

case class SparqlDirective(sparqlQuery : String) extends Directive {
	 if(!(sparqlQuery.toLowerCase.contains("http://idi.fundacionctic.org/scovoxl/scovoxl")|sparqlQuery.toLowerCase.contains("/idi.fundacionctic.org/tabels/project"))&(sparqlQuery.toLowerCase.contains("192.168.")|sparqlQuery.toLowerCase.contains("fundacionctic")))
		throw new ServerReferedURIException(sparqlQuery)
	 override def toString() = "@SPARQL(\"" + sparqlQuery + "\")"
}

case class LoadDirective(url : String) extends Directive {
	 if(!(url.toLowerCase.contains("http://idi.fundacionctic.org/scovoxl/scovoxl")|url.toLowerCase.contains("/idi.fundacionctic.org/tabels/project"))&(url.toLowerCase.contains("192.168.")|url.toLowerCase.contains("fundacionctic")))
		throw new ServerReferedURIException(url)
	 override def toString() = "@LOAD(\"" + url + "\")"
}

abstract class TabelsStatement extends ASTNode

case class BlockStatement(childStatements : Seq[TabelsStatement]) extends TabelsStatement {

	override def accept(vis : Visitor) =  vis.visit(this)
    
}

/*
 * 
 * group of Classes designed to assign values to variables from different sources or methods
 * 
 */
abstract class VariableAssignationStatement extends TabelsStatement

case class LetStatement(variable: Variable, 
                        expression: Expression,
                        nestedStatement: Option[TabelsStatement] = None) extends VariableAssignationStatement{
  
	override def accept(vis : Visitor) =  vis.visit(this)
	  
}

case class MatchStatement(tuple: Tuple, filter: Option[Expression] = None, position : Option[Position] = None , 
		 nestedStatement: Option[TabelsStatement] = None) extends VariableAssignationStatement{
  
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
}

case class IteratorStatement(override val dimension : Dimension, filter: Option[Expression] = None, 
		pos : Option[Position] = None, startCond : Option[Either[Expression,Position]] = None, stopCond : Option[Expression] = None, override val variable: Option[Variable] = None,
		nestedStatement: Option[TabelsStatement] = None, windowed:Boolean = true) extends DimensionStatement {
  
  override def accept(vis : Visitor) = vis.visit(this)
}

case class SetInDimensionStatement(override val dimension : Dimension, fixedDimension: String, override val variable: Option[Variable] = None,
		nestedStatement: Option[TabelsStatement] = None ) extends DimensionStatement {
  
  override def accept(vis : Visitor) = vis.visit(this)
}
/*
 * 
 * group of classes designed for conditional purpouses
 * 
 */
abstract class ConditionalStatement extends TabelsStatement{
	val condition : Option[Either[Expression,Position]]
}

case class WhenConditionalStatement(override val condition : Option[Either[Expression,Position]] = None,
		nestedStatement: Option[TabelsStatement] = None) extends ConditionalStatement {
  
  override def accept(vis : Visitor) = vis.visit(this)
}

case class Variable (name : String) {

  override def toString() = name	
 
}

case class Tuple(variables : Seq[Variable] = Seq(), tupleType : TupleType = TupleType.horizontal) {
    
    override def toString() = "[" + (variables map (_.toString) mkString ",") + "] IN " + tupleType
    
}


