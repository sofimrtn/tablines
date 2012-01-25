package es.ctic.tabels

import java.net.URLEncoder

/*
 * TABELS Expressions
 */

object MiscellaneaFunctions extends FunctionCollection{
	 lazy val lucene = new Lucene
     val DBPediaDisambiguation3 = "DBPedia-Disambiguation" isDefinedBy {(ec: EvaluationContext,query: String, workMode: String) => lucene.query(ec,query, workMode) }
	 val DBPediaDisambiguation1 = "DBPedia-Disambiguation" isDefinedBy {(ec: EvaluationContext,query: String) => lucene.query(ec,query) }
	 val setLangTag = "setLangTag" isDefinedBy {(lit: String, lang: String) => Literal(value = lit, rdfType = XSD_STRING, langTag = lang)}
	 val boolean = "boolean" isDefinedBy { (x : Boolean) => x  }
}

case class VariableReference(variable:Variable) extends Expression{
  
  override def evaluate(evaluationContext : EvaluationContext) = evaluationContext.bindings.getValue(variable)
  override def prettyPrint = variable.toString

}

case class GetRowExpression(variable:Variable) extends Expression {
    
    override def evaluate(evaluationContext:EvaluationContext) = Literal(evaluationContext.bindings.getPoint(variable).row, XSD_INT)
    override def prettyPrint = "get-row(" + variable.toString + ")"
    
}

case class GetColExpression(variable:Variable) extends Expression {
    
    override def evaluate(evaluationContext:EvaluationContext) = Literal(evaluationContext.bindings.getPoint(variable).col, XSD_INT)
    override def prettyPrint = "get-col(" + variable.toString + ")"
    
}

/*
 * RDF Expresion
 */

case class ResourceExpression(expression:Expression, uri : NamedResource) extends Expression {
  
  override def evaluate(evaluationContext : EvaluationContext) = uri + URLEncoder.encode(expression.evaluateAsStringValue(evaluationContext),"UTF-8")
  override def prettyPrint = "resource(" + expression.toString + "," + uri.toString + ")"

}

case class LiteralExpression(literal : Literal) extends Expression{
    
  override def evaluate(evaluationContext : EvaluationContext) = literal
  override def prettyPrint =  literal.toString
}





/*
 * BOOLEAN Expressions 
 */
case class NotExpression(expression:Expression) extends Expression{
  
  override def evaluate(evaluationContext:EvaluationContext) = 
    
	 if (expression.evaluateAsTruthValue(evaluationContext))
	    LITERAL_FALSE
	 else
		LITERAL_TRUE

    override def prettyPrint = "not(" + expression.toString + ")"

}

case class TernaryOperationExpression(condition: Expression, trueExpression: Expression, falseExpression : Expression) extends Expression{
  
  override def evaluate(evaluationContext: EvaluationContext)= 
    if(condition.evaluateAsTruthValue(evaluationContext)) trueExpression.evaluate(evaluationContext) else falseExpression.evaluate(evaluationContext)
  override def prettyPrint = "if" +  condition.toString  +" then "+ trueExpression.toString +" else "+falseExpression.toString 
}