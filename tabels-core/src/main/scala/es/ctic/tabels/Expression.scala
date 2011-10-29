package es.ctic.tabels

import scala.util.matching.Regex
import java.net.URLEncoder

abstract class Expression {
  
  def evaluate(evaluationContext : EvaluationContext) : RDFNode
  
  def prettyPrint() : String
  
  override def toString = prettyPrint

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

case class AddVariableExpression(variable : Variable, expression: Expression) extends Expression{
  
  override def evaluate(evaluationContext : EvaluationContext) = evaluationContext.bindings.getValue(variable) + expression.evaluate(evaluationContext).asString.value.toString
  override def prettyPrint = variable.toString + "+" + expression.toString

}

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

case class NotExpression(expression:Expression) extends Expression{
  
  override def evaluate(evaluationContext:EvaluationContext) = 
    
	 if (expression.evaluate(evaluationContext).asBoolean.truthValue)
	    LITERAL_FALSE
	 else
		LITERAL_TRUE

    override def prettyPrint = "not(" + expression.toString + ")"

}

/*
 * STRING FUNCTIONS
 * 
 */
case class ConcatExpression(expressions: Seq[Expression]) extends Expression{
  
   override def evaluate(evaluationContext:EvaluationContext): RDFNode ={
	var result : String = ""
    expressions.foreach( exp => result += exp.evaluate(evaluationContext).asString.value.toString)
    return Literal(result, XSD_STRING). asInstanceOf[RDFNode]
  }
   override def prettyPrint = "concat(" + expressions.map(_ toString).mkString(",") + ")"
}

case class StringJoinExpression(expressions: Seq[Expression], separator : Expression) extends Expression{
  
   override def evaluate(evaluationContext:EvaluationContext): RDFNode =
	
    Literal(expressions.map(_.evaluate(evaluationContext).asString.value.toString).mkString(separator.evaluate(evaluationContext).asString.value.toString), XSD_STRING). asInstanceOf[RDFNode]
  
   override def prettyPrint = "string join(" + expressions.map(_ toString).mkString(",")  + " , "+ separator + ")"
}

case class SubStringExpression(expression: Expression, index : Int) extends Expression{
  
   override def evaluate(evaluationContext:EvaluationContext): RDFNode ={
	
	if (expression.evaluate(evaluationContext).asString.value.toString.length >0)
    	Literal(expression.evaluate(evaluationContext).asString.value.toString.substring(index), XSD_STRING). asInstanceOf[RDFNode]
	else Literal("", XSD_STRING). asInstanceOf[RDFNode]
   }
   override def prettyPrint = "substring(" + expression.toString  + " , "+ index + ")"
}

case class StringLengthExpression(expression: Expression) extends Expression
{
	override def evaluate(evaluationContext : EvaluationContext) : RDFNode =
	  Literal (expression.evaluate(evaluationContext).asString.value.toString.length, XSD_INT)
	
	override def prettyPrint = "length(" + expression.toString  + ")"
}

case class StartsWithExpression(container: Expression, start: Expression) extends Expression
{
	override def evaluate(evaluationContext : EvaluationContext) : RDFNode =
	  if(container.evaluate(evaluationContext).asString.value.toString.startsWith(start.evaluate(evaluationContext).asString.value.toString))
		   LITERAL_TRUE
	  else LITERAL_FALSE
	
	override def prettyPrint = "startswith(" + container.toString  +", "+ start.toString  + ")"
}

case class SubstringBeforeExpression(container: Expression, subString: Expression) extends Expression
{
	override def evaluate(evaluationContext : EvaluationContext) : RDFNode =
	  Literal(container.evaluate(evaluationContext).asString.value.toString.stripSuffix(subString.evaluate(evaluationContext).asString.value.toString), XSD_STRING)
		   
	override def prettyPrint = "substringbefore(" + container.toString  +", "+ subString.toString  + ")"
}

case class SubstringAfterExpression(container: Expression, subString: Expression) extends Expression
{
	override def evaluate(evaluationContext : EvaluationContext) : RDFNode =
	  Literal(container.evaluate(evaluationContext).asString.value.toString.stripPrefix(subString.evaluate(evaluationContext).asString.value.toString), XSD_STRING)
		   
	override def prettyPrint = "substringafter(" + container.toString  +", "+ subString.toString  + ")"
}

case class EndsWithExpression(container: Expression, end: Expression) extends Expression
{
	override def evaluate(evaluationContext : EvaluationContext) : RDFNode =
	  if(container.evaluate(evaluationContext).asString.value.toString.endsWith(end.evaluate(evaluationContext).asString.value.toString))
		   LITERAL_TRUE
	  else LITERAL_FALSE
	
	override def prettyPrint = "endswith(" + container.toString  +", "+ end.toString  + ")"
}

case class ContainsExpression(container: Expression, content: Expression) extends Expression
{
	override def evaluate(evaluationContext : EvaluationContext) : RDFNode =
	  if(container.evaluate(evaluationContext).asString.value.toString.contains(content.evaluate(evaluationContext).asString.value.toString))
		   LITERAL_TRUE
	  else LITERAL_FALSE
	
	override def prettyPrint = "contains(" + container.toString  +", "+ content.toString  + ")"
}

case class ReplaceExpression(input: Expression,pattern : Regex, replacement: Expression) extends Expression
{
	override def evaluate(evaluationContext : EvaluationContext) : RDFNode =
	   Literal(input.evaluate(evaluationContext).asString.value.toString.replaceAll(pattern.toString, replacement.evaluate(evaluationContext).asString.value.toString),XSD_STRING)
			
	override def prettyPrint = "replace(" + input.toString  +", "+ pattern.toString +", "+replacement.toString  + ")"
}

case class UpperCaseExpression(expression: Expression) extends Expression
{
	override def evaluate(evaluationContext : EvaluationContext) : RDFNode =
	  Literal(expression.evaluate(evaluationContext).asString.value.toString.toUpperCase)
		  
	override def prettyPrint = "upper case(" + expression   + ")"
}

case class LowerCaseExpression(expression: Expression) extends Expression
{
	override def evaluate(evaluationContext : EvaluationContext) : RDFNode =
	  Literal(expression.evaluate(evaluationContext).asString.value.toString.toLowerCase)
		  
	override def prettyPrint = "lower case(" + expression   + ")"
}

case class TranslateExpression(input: Expression, pattern : Expression,replacement: Expression) extends Expression
{
	override def evaluate(evaluationContext : EvaluationContext) : RDFNode ={
	 var textChain :String =input.evaluate(evaluationContext).asString.value.toString
	 for(char <- pattern.evaluate(evaluationContext).asString.value.toString.toCharArray())
	 {
	   //FIX ME: not checked if the index in pattern is out of bounds in replacement
	   textChain = textChain.replaceAll(char.toString,replacement.evaluate(evaluationContext).asString.value.toString.charAt(pattern.evaluate(evaluationContext).asString.value.toString.indexOf(char)).toString )
	 }
	 
	 Literal(textChain,XSD_STRING)
}
	override def prettyPrint = "translate(" +  input.toString  +", "+ pattern.toString +", "+replacement.toString + ")"
}
/*FIX ME: To be implemented function 
 * escape-html-uri(expression: Expression)
 * */

/*
 * NUMERIC OPERATIONS
 *  
 */
case class NumericAddExpression(expression: Expression, expression2: Expression) extends Expression
{
	override def evaluate(evaluationContext : EvaluationContext) : RDFNode =
	  //FIX ME: Check if expressions are numbers. check if they are int or float
	  Literal(expression.evaluate(evaluationContext).asString.value.toString.toFloat + expression2.evaluate(evaluationContext).asString.value.toString.toFloat, XSD_FLOAT)
		  
	override def prettyPrint = "lower case(" + expression   + ")"
}

case class NumericSubtractExpression(expression: Expression, expression2: Expression) extends Expression
{
	override def evaluate(evaluationContext : EvaluationContext) : RDFNode =
	  //FIX ME: Check if expressions are numbers. check if they are int or float
	  Literal(expression.evaluate(evaluationContext).asString.value.toString.toFloat - expression2.evaluate(evaluationContext).asString.value.toString.toFloat, XSD_FLOAT)
		  
	override def prettyPrint = "lower case(" + expression   + ")"
}

case class NumericMultiplyExpression(expression: Expression, expression2: Expression) extends Expression
{
	override def evaluate(evaluationContext : EvaluationContext) : RDFNode =
	  //FIX ME: Check if expressions are numbers. check if they are int or float
	  Literal(expression.evaluate(evaluationContext).asString.value.toString.toFloat * expression2.evaluate(evaluationContext).asString.value.toString.toFloat, XSD_FLOAT)
		  
	override def prettyPrint = "lower case(" + expression   + ")"
}

case class NumericDivideExpression(expression: Expression, expression2: Expression) extends Expression
{
	override def evaluate(evaluationContext : EvaluationContext) : RDFNode =
	  //FIX ME: Check if expressions are numbers. check if they are int or float
	  Literal(expression.evaluate(evaluationContext).asString.value.toString.toFloat / expression2.evaluate(evaluationContext).asString.value.toString.toFloat, XSD_FLOAT)
		  
	override def prettyPrint = "lower case(" + expression   + ")"
}

case class NumericIntegerDivideExpression(expression: Expression, expression2: Expression) extends Expression
{
	override def evaluate(evaluationContext : EvaluationContext) : RDFNode =
	  //FIX ME: Check if expressions are numbers. check if they are int or float
	  Literal((expression.evaluate(evaluationContext).asString.value.toString.toFloat / expression2.evaluate(evaluationContext).asString.value.toString.toFloat).round, XSD_FLOAT)
		  
	override def prettyPrint = "lower case(" + expression   + ")"
}

case class NumericModExpression(expression: Expression, expression2: Expression) extends Expression
{
	override def evaluate(evaluationContext : EvaluationContext) : RDFNode =
	  //FIX ME: Check if expressions are numbers. check if they are int or float
	  Literal(expression.evaluate(evaluationContext).asString.value.toString.toFloat % expression2.evaluate(evaluationContext).asString.value.toString.toFloat, XSD_FLOAT)
		  
	override def prettyPrint = "lower case(" + expression   + ")"
}


/* *Type change expressions  * */
case class BooleanExpression(expression: Expression) extends Expression{
  
  override def evaluate(evaluationContext : EvaluationContext) =  Literal(expression.evaluate(evaluationContext).asBoolean.value, XSD_BOOLEAN)
  override def prettyPrint = "boolean(" + expression.toString + ")"
  
}

case class StringExpression(expression: Expression) extends Expression{
  
  override def evaluate(evaluationContext : EvaluationContext) = Literal(expression.evaluate(evaluationContext).asString.value, XSD_STRING)
  override def prettyPrint = "string(" + expression.toString + ")"

}


//FIX ME: Type control
case class IntExpression(expression : Expression) extends Expression{
  
  override def evaluate(evaluationContext:EvaluationContext) = {
    val decimalExpression = """(?:[0-9]+(?:\.[0-9]+)?)+(?:(?:\.|\,)[0-9]*)?""".r
    expression.evaluate(evaluationContext).asString.value match{
    	case  decimalExpression()=>  Literal(expression.evaluate(evaluationContext).asString.value, XSD_INT)
    	case _ => throw new InvalidTypeFunctionException("Is not posible to generate Int RDF type with this Literal") 
    }
  }   
  override def prettyPrint = "int(" + expression.toString + ")"
	
}
case class FloatExpression(expression : Expression) extends Expression{
  
  override def evaluate(evaluationContext:EvaluationContext) = {
    val decimalExpression = """(?:[0-9]+(?:\.[0-9]+)?)+(?:(?:\.|\,)[0-9]*)?""".r
    expression.evaluate(evaluationContext).asString.value match{
    	case  decimalExpression()=>  Literal(expression.evaluate(evaluationContext).asString.value, XSD_FLOAT)
    	case _ => throw new InvalidTypeFunctionException("Is not posible to generate Float RDF type with this Literal") 
    }
  }   
  override def prettyPrint = "float(" + expression.toString + ")"
	
}

case class DecimalExpression(expression : Expression) extends Expression{
  
  override def evaluate(evaluationContext:EvaluationContext) = {
    val decimalExpression = """(?:[0-9]+(?:\.[0-9]+)?)+(?:(?:\.|\,)[0-9]*)?""".r
    expression.evaluate(evaluationContext).asString.value match{
    	case  decimalExpression()=>  Literal(expression.evaluate(evaluationContext).asString.value, XSD_DECIMAL)
    	case _ => throw new InvalidTypeFunctionException("Is not posible to generate Decimal RDF type with this Literal") 
    }
  }   
  override def prettyPrint = "decimal(" + expression.toString + ")"
	
}



