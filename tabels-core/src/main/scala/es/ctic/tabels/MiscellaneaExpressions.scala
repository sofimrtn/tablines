package es.ctic.tabels

import java.net.{URL, URLEncoder}

/*
 * TABELS Expressions
 */

object MiscellaneaFunctions extends FunctionCollection{
	 lazy val lucene = new Lucene
     val DBPediaDisambiguation3 = "DBPedia-Disambiguation" isDefinedBy {(ec: EvaluationContext,query: String, workMode: String) => lucene.query(ec,query, workMode) }
	 val DBPediaDisambiguation1 = "DBPedia-Disambiguation" isDefinedBy {(ec: EvaluationContext,query: String) => lucene.query(ec,query) }
	 val setLangTag = "setLangTag" isDefinedBy {(lit: String, lang: String) => Literal(value = lit, rdfType = XSD_STRING, langTag = lang)}
	 val boolean = "boolean" isDefinedBy { (x : Boolean) => x  }
   val isResource = "is-resource" isDefinedBy {(ec: EvaluationContext, uri:String) => new NamedResource(uri)}
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
 * RDF Expressions
 */

case class ResourceExpression(expression:Expression, uri : NamedResource) extends Expression {
  
  override def evaluate(evaluationContext : EvaluationContext) =try{ uri + expression.evaluateAsStringValue(evaluationContext)}
  																catch{ case e =>  uri + ((URLEncoder.encode(expression.evaluateAsStringValue(evaluationContext),"cp1252")).replaceAll("%[0-9a-fA-F][0-9a-fA-F]|\\+", "_"))}
  override def prettyPrint = "resource(" + expression.toString + "," + uri.toString + ")"

}

/*case class IsResourceExpression(expression:Expression) extends Expression {
  
  override def evaluate(evaluationContext : EvaluationContext) =
  	new NamedResource(expression.evaluateAsStringValue(evaluationContext))

  override def prettyPrint = "resource(" + expression.toString + ")"

}   */

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

case class AndExpression(expression1:Expression, expression2:Expression) extends Expression{
  
  override def evaluate(evaluationContext:EvaluationContext) = 
    
	 expression1.evaluateAsTruthValue(evaluationContext)&& expression2.evaluateAsTruthValue(evaluationContext) match{
	    
	 	case true => LITERAL_TRUE
	    case false => LITERAL_FALSE
	  }
	    
	

    override def prettyPrint = "AND("+ expression1.toString + " , "+ expression2.toString+")"

}

case class OrExpression(expression1:Expression, expression2:Expression) extends Expression{
  
  override def evaluate(evaluationContext:EvaluationContext) = 
    
	 expression1.evaluateAsTruthValue(evaluationContext)|| expression2.evaluateAsTruthValue(evaluationContext) match{
	    
	 	case true => LITERAL_TRUE
	    case false => LITERAL_FALSE
	  }
	    
	

    override def prettyPrint = "OR(" + expression1.toString + " , "+ expression2.toString+")"

}

case class TernaryOperationExpression(condition: Expression, trueExpression: Expression, falseExpression : Expression) extends Expression{
  
  override def evaluate(evaluationContext: EvaluationContext)= 
    if(condition.evaluateAsTruthValue(evaluationContext)) trueExpression.evaluate(evaluationContext) else falseExpression.evaluate(evaluationContext)
  override def prettyPrint = "if " +  condition.toString  +" then "+ trueExpression.toString +" else "+falseExpression.toString 
}
