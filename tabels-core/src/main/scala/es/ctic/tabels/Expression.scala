package es.ctic.tabels

import scala.util.matching.Regex
import java.net.URLEncoder

abstract class Expression {
  
  def evaluate(evaluationContext : EvaluationContext) : RDFNode
  
  def prettyPrint() : String
  
  override def toString = prettyPrint

}

/*
 * TABELS Expressions
 */

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

case class ResourceExpression(expression:Expression, uri : Resource) extends Expression {
  
  override def evaluate(evaluationContext : EvaluationContext) = uri + URLEncoder.encode(expression.evaluate(evaluationContext).asString.value.toString,"UTF-8")
  override def prettyPrint = "resource(" + expression.toString + "," + uri.toString + ")"

}

case class LiteralExpression(literal : Literal) extends Expression{
    
  override def evaluate(evaluationContext : EvaluationContext) = literal
  override def prettyPrint =  literal.toString
}



case class RegexExpression(expression : Expression , re : Regex) extends Expression{
  
	override def evaluate(evaluationContext : EvaluationContext) =
	 expression.evaluate(evaluationContext).asString.value.toString.matches(re.toString()) match{
	    case true =>  LITERAL_TRUE
	    case false => LITERAL_FALSE
	  }
    override def prettyPrint = "matches(" + expression.toString + ",\"" + re.toString + "\")"

}

case class DBPediaDisambiguation(expression:Expression) extends Expression{
  
  override def evaluate(evaluationContext:EvaluationContext) ={ 
    
	 val query = new DBPediaQuery
	 query.queryResource(expression.evaluate(evaluationContext))
  }
    override def prettyPrint = "DBPedia-disambiguation(" + expression.toString + ")"

}

/*
 * BOOLEAN Expressions 
 */
case class NotExpression(expression:Expression) extends Expression{
  
  override def evaluate(evaluationContext:EvaluationContext) = 
    
	 if (expression.evaluate(evaluationContext).asBoolean.truthValue)
	    LITERAL_FALSE
	 else
		LITERAL_TRUE

    override def prettyPrint = "not(" + expression.toString + ")"

}

case class TernaryOperationExpression(condition: Expression, trueExpression: Expression, falseExpression : Expression) extends Expression{
  
  override def evaluate(evaluationContext: EvaluationContext)= 
    if(condition.evaluate(evaluationContext).asBoolean.truthValue)trueExpression.evaluate(evaluationContext) else falseExpression.evaluate(evaluationContext)
  override def prettyPrint = "if" +  condition.toString  +" then "+ trueExpression.toString +" else "+falseExpression.toString 
}

/* *Type change expressions  * */
case class BooleanExpression(expression: Expression) extends Expression{
  
  override def evaluate(evaluationContext : EvaluationContext) =  Literal(expression.evaluate(evaluationContext).asBoolean.value, XSD_BOOLEAN)
  override def prettyPrint = "boolean(" + expression.toString + ")"
  
}







