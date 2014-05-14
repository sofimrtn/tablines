package es.ctic.tabels

import java.net.{URL, URI, URLEncoder}
import grizzled.slf4j.Logging

/*
 * TABELS Expressions
 */

object MiscellaneaFunctions extends FunctionCollection with Logging {
	 lazy val lucene = new Lucene
   val DBPediaDisambiguation3 = "DBPedia-Disambiguation" isDefinedBy {(ec: EvaluationContext,query: String, workMode: String) => lucene.query(ec,query, workMode) }
	 val DBPediaDisambiguation1 = "DBPedia-Disambiguation" isDefinedBy {(ec: EvaluationContext,query: String) => lucene.query(ec,query) }
	 val setLangTag = "setLangTag" isDefinedBy {(lit: String, lang: String) => Literal(value = lit, rdfType = XSD_STRING, langTag = lang)}
	 val boolean = "boolean" isDefinedBy { (x : Boolean) => x  }
   val resource1 = "resource" isDefinedBy {(uri:String) => new NamedResource(uri)}

   val resource2 = "resource" isDefinedBy {(expression:String, uri:String) =>  try
                                                                               {
                                                                                 val url = new URL((uri + expression) )
                                                                                 val protocol = url.getProtocol
                                                                                 val user = if (url.getUserInfo != null) url.getUserInfo  else null
                                                                                 val port = if (url.getPort != null)  url.getPort else -1
                                                                                 val host = url.getHost
                                                                                 val prePath = url.getPath
                                                                                 val path = if (prePath.length > 0) prePath else null
                                                                                 val query = if (url.getQuery != null) URLEncoder.encode(url.getQuery,"UTF-8") else null
                                                                                 val fragment = if (url.getRef != null) URLEncoder.encode(url.getRef,"UTF-8") else null

                                                                                 NamedResource(new URI(protocol,user,host,port,path,query,fragment).toString )
                                                                               }
                                                                               catch
                                                                                 { case e => throw e}}

   val isResource = "is-resource" isDefinedBy {(uri:String) => if (uri.equalsIgnoreCase(""))
                                                                   throw new NotValidUriException(uri)
                                                                else new NamedResource(uri)}
   val canBeResource = "can-be-resource" isDefinedBy {(uri:String) => try{ if (uri.equalsIgnoreCase(""))
                                                                              throw new NotValidUriException(uri)
                                                                           else new NamedResource(uri)
                                                                           true
                                                                      }catch{ case _=> false}
   }

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

case class ResourceExpression(expression:Expression, uri : NamedResource) extends Expression with Logging {
  
  override def evaluate(evaluationContext : EvaluationContext) =
    try
      {
      val url = new URL((uri.uri + expression.evaluateAsStringValue(evaluationContext)) )
      val protocol = url.getProtocol
      val user = if (url.getUserInfo != null) url.getUserInfo  else null
      val port = if (url.getPort != null)  url.getPort else -1
      val host =url.getHost
      val prePath = url.getPath
      val path = if (prePath.length > 0) prePath else null
      val query = if (url.getQuery != null) URLEncoder.encode(url.getQuery,"UTF-8") else null
      val fragment = if (url.getRef != null) URLEncoder.encode(url.getRef,"UTF-8") else null

      NamedResource(new URI(protocol,user,host,port,path,query,fragment).toString )
    }
    catch
    { case e => throw e}

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

    override def prettyPrint = "not " + expression.toString + " "

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
