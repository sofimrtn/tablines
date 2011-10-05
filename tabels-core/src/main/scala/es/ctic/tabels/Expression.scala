package es.ctic.tabels

abstract class Expression {
  
  def evaluate(evaluationContext : EvaluationContext) : RDFNode

}

case class VariableReference(variable:Variable) extends Expression{
	
	override def evaluate(evaluationContext : EvaluationContext) = evaluationContext.bindings.getValue(variable)

}

case class ResourceExpression(variable:Variable, uri : Resource) extends Expression {
	
	override def evaluate(evaluationContext : EvaluationContext) = uri + evaluationContext.bindings.getValue(variable).getValue
}