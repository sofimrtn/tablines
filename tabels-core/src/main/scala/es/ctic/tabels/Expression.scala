package es.ctic.tabels

abstract class Expression {
  
  def evaluate(bindings : Bindings) : RDFNode

}

case class VariableReference(variable:Variable) extends Expression{
	
	override def evaluate(bindings: Bindings) = bindings.getValue(variable)

}

case class ResourceExpression(variable:Variable, uri : String) extends Expression {
	
	override def evaluate(bindings : Bindings) = Resource(uri + bindings.getValue(variable).getValue)
}