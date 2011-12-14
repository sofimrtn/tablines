package es.ctic.tabels

import scala.util.matching.Regex
import java.net.URLEncoder

abstract class Expression {
  
  def evaluate(evaluationContext : EvaluationContext) : RDFNode
  
  def prettyPrint() : String
  
  override def toString = prettyPrint

}

case class FunctionName(name : String) {
  
   def isDefinedBy[TYPE1,TYPE_RESULT]
        (f : (EvaluationContext, TYPE1) => TYPE_RESULT)
        (implicit type1Converter : CanFromRDFNode[TYPE1], resultConverter : CanToRDFNode[TYPE_RESULT])
         : UnaryFunction[TYPE1, TYPE_RESULT] =
        UnaryFunction[TYPE1,TYPE_RESULT](name, f)
        
    def isDefinedBy[TYPE1,TYPE_RESULT]
        (f : (TYPE1) => TYPE_RESULT)
        (implicit type1Converter : CanFromRDFNode[TYPE1], resultConverter : CanToRDFNode[TYPE_RESULT])
         : UnaryFunction[TYPE1, TYPE_RESULT] =
        UnaryFunction[TYPE1,TYPE_RESULT](name, { (ev : EvaluationContext, p1 : TYPE1) => f(p1) })

    def isDefinedBy[TYPE1,TYPE2,TYPE_RESULT]
        (f : (EvaluationContext, TYPE1, TYPE2) => TYPE_RESULT)
        (implicit type1Converter : CanFromRDFNode[TYPE1], type2Converter : CanFromRDFNode[TYPE2], resultConverter : CanToRDFNode[TYPE_RESULT])
         : BinaryFunction[TYPE1, TYPE2, TYPE_RESULT] =
        BinaryFunction[TYPE1,TYPE2,TYPE_RESULT](name, f)
        
    def isDefinedBy[TYPE1,TYPE2,TYPE_RESULT]
        (f : (TYPE1, TYPE2) => TYPE_RESULT)
        (implicit type1Converter : CanFromRDFNode[TYPE1], type2Converter : CanFromRDFNode[TYPE2], resultConverter : CanToRDFNode[TYPE_RESULT])
         : BinaryFunction[TYPE1, TYPE2, TYPE_RESULT] =
        BinaryFunction[TYPE1,TYPE2,TYPE_RESULT](name, { (ev : EvaluationContext, p1 : TYPE1, p2 : TYPE2) => f(p1,p2) })
        
    def isDefinedBy[TYPE1,TYPE2, TYPE3, TYPE_RESULT]
        (f : (EvaluationContext, TYPE1, TYPE2, TYPE3) => TYPE_RESULT)
        (implicit type1Converter : CanFromRDFNode[TYPE1], type2Converter : CanFromRDFNode[TYPE2], type3Converter : CanFromRDFNode[TYPE3], resultConverter : CanToRDFNode[TYPE_RESULT])
         : TernaryFunction[TYPE1, TYPE2, TYPE3, TYPE_RESULT] =
        TernaryFunction[TYPE1,TYPE2, TYPE3,TYPE_RESULT](name, f)
        
    def isDefinedBy[TYPE1,TYPE2, TYPE3,TYPE_RESULT]
        (f : (TYPE1, TYPE2, TYPE3) => TYPE_RESULT)
        (implicit type1Converter : CanFromRDFNode[TYPE1], type2Converter : CanFromRDFNode[TYPE2], type3Converter : CanFromRDFNode[TYPE3], resultConverter : CanToRDFNode[TYPE_RESULT])
         : TernaryFunction[TYPE1, TYPE2, TYPE3, TYPE_RESULT] =
        TernaryFunction[TYPE1,TYPE2, TYPE3, TYPE_RESULT](name, { (ev : EvaluationContext, p1 : TYPE1, p2 : TYPE2, p3 : TYPE3) => f(p1,p2,p3) })

}

case class UnaryFunction[TYPE1, TYPE_RESULT](name : String, f : (EvaluationContext, TYPE1) => TYPE_RESULT)
    (implicit type1Converter : CanFromRDFNode[TYPE1],  resultConverter : CanToRDFNode[TYPE_RESULT]) {

    def createExpression(arg1 : Expression) : UnaryExpression[TYPE1, TYPE_RESULT] =
        new UnaryExpression(this)(arg1)

}

case class UnaryExpression[TYPE1, TYPE_RESULT](func : UnaryFunction[TYPE1, TYPE_RESULT])
    (arg1 : Expression)
    (implicit type1Converter : CanFromRDFNode[TYPE1], resultConverter : CanToRDFNode[TYPE_RESULT])
    extends Expression {

    def typeWrapper(ec : EvaluationContext, param1 : RDFNode) : RDFNode =
        resultConverter.toRDFNode(func.f(ec, type1Converter.fromRDFNode(param1)))

    override def evaluate(evaluationContext : EvaluationContext) : RDFNode =
        typeWrapper(evaluationContext, arg1.evaluate(evaluationContext))
        
    override def prettyPrint = func.name + "(" + arg1 + ")"

}

case class BinaryFunction[TYPE1, TYPE2, TYPE_RESULT](name : String, f : (EvaluationContext, TYPE1, TYPE2) => TYPE_RESULT)
    (implicit type1Converter : CanFromRDFNode[TYPE1], type2Converter : CanFromRDFNode[TYPE2], resultConverter : CanToRDFNode[TYPE_RESULT]) {

    def createExpression(arg1 : Expression, arg2: Expression) : BinaryExpression[TYPE1, TYPE2, TYPE_RESULT] =
        new BinaryExpression(this)(arg1, arg2)

}

case class BinaryExpression[TYPE1, TYPE2, TYPE_RESULT](func : BinaryFunction[TYPE1, TYPE2, TYPE_RESULT])
    (arg1 : Expression, arg2 : Expression)
    (implicit type1Converter : CanFromRDFNode[TYPE1], type2Converter : CanFromRDFNode[TYPE2], resultConverter : CanToRDFNode[TYPE_RESULT])
    extends Expression {

    def typeWrapper(ec : EvaluationContext, param1 : RDFNode, param2 : RDFNode) : RDFNode =
        resultConverter.toRDFNode(func.f(ec, type1Converter.fromRDFNode(param1),
                                             type2Converter.fromRDFNode(param2)))

    override def evaluate(evaluationContext : EvaluationContext) : RDFNode =
        typeWrapper(evaluationContext, arg1.evaluate(evaluationContext), arg2.evaluate(evaluationContext))
        
    override def prettyPrint = func.name + "(" + arg1 + "," + arg2 + ")"

}

case class TernaryFunction[TYPE1, TYPE2, TYPE3, TYPE_RESULT](name : String, f : (EvaluationContext, TYPE1, TYPE2, TYPE3) => TYPE_RESULT)
    (implicit type1Converter : CanFromRDFNode[TYPE1], type2Converter : CanFromRDFNode[TYPE2], type3Converter : CanFromRDFNode[TYPE3], resultConverter : CanToRDFNode[TYPE_RESULT]) {

    def createExpression(arg1 : Expression, arg2: Expression, arg3: Expression) : TernaryExpression[TYPE1, TYPE2, TYPE3, TYPE_RESULT] =
        new TernaryExpression(this)(arg1, arg2, arg3)

}

case class TernaryExpression[TYPE1, TYPE2, TYPE3, TYPE_RESULT](func : TernaryFunction[TYPE1, TYPE2, TYPE3, TYPE_RESULT])
    (arg1 : Expression, arg2 : Expression, arg3 : Expression)
    (implicit type1Converter : CanFromRDFNode[TYPE1], type2Converter : CanFromRDFNode[TYPE2], type3Converter : CanFromRDFNode[TYPE3], resultConverter : CanToRDFNode[TYPE_RESULT])
    extends Expression {

    def typeWrapper(ec : EvaluationContext, param1 : RDFNode, param2 : RDFNode, param3 : RDFNode) : RDFNode =
        resultConverter.toRDFNode(func.f(ec, type1Converter.fromRDFNode(param1),
                                             type2Converter.fromRDFNode(param2),
                                             type3Converter.fromRDFNode(param3)))

    override def evaluate(evaluationContext : EvaluationContext) : RDFNode =
        typeWrapper(evaluationContext, arg1.evaluate(evaluationContext), arg2.evaluate(evaluationContext), arg3.evaluate(evaluationContext))
        
    override def prettyPrint = func.name + "(" + arg1 + "," + arg2 + "," + arg3 +")"

}

trait FunctionCollection {

    implicit def string2functionName(name: String) : FunctionName = FunctionName(name)

}

/*
 * TABELS Expressions
 */

object MiscellaneaFunctions extends FunctionCollection{
	 val lucene = new Lucene
     val DBPediaDisambiguation3 = "DBPedia-Disambiguation" isDefinedBy {(query: String, workMode: String, index:Int) => lucene.query(query, workMode,index) getOrElse Seq(Resource("http://example.org/ResourceNotDisambiguated")) }
	 val DBPediaDisambiguation1 = "DBPedia-Disambiguation" isDefinedBy {(query: String) => lucene.query(query) getOrElse Seq(Resource("http://example.org/ResourceNotDisambiguated")) }

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
    
	
    //val query = new DBPediaQuery
	 //query.queryResource(expression.evaluate(evaluationContext))
    val lucene = new Lucene
    
    
    Literal(lucene.query(""))
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







