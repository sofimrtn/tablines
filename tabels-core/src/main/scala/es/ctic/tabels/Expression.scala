package es.ctic.tabels

abstract class Expression extends EvaluableExpression{
  
  def evaluate(evaluationContext : EvaluationContext) : RDFNode

}

case class VariableReference(variable:Variable) extends Expression{
	
	override def evaluate(evaluationContext : EvaluationContext) = evaluationContext.bindings.getValue(variable)

}

case class ResourceExpression(expression:Expression, uri : Resource) extends Expression {
	
	override def evaluate(evaluationContext : EvaluationContext) = uri + expression.evaluate(evaluationContext).getValue
}