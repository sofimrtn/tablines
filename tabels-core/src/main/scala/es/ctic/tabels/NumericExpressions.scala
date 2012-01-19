package es.ctic.tabels

/*
 * NUMERIC Expressions
 *  
 */

object NumericFunctions extends FunctionCollection {

     val numericAdd = "numeric-add" isDefinedBy { (x : Int, y : Int) => x + y }
     val numericSubstract = "numeric-substract" isDefinedBy { (x : Int, y : Int) => x - y }
     val numericMultiply = "numeric-multiply" isDefinedBy { (x : Int, y : Int) => x * y }
     val numericDivide = "numeric-divide" isDefinedBy { (x : Int, y : Int) => x / y }
     val numericIntegerDivide = "numeric-integer-divide" isDefinedBy { (x : Int, y : Int) => (x / y).round }
     val numericMod = "numeric-mod" isDefinedBy { (x : Int, y : Int) => x % y }
     
 //    val startsWith = BinaryFunction("starts-with", { (ec : EvaluationContext, x : String, y : String) => x.startsWith(y) })

}

/*case class NumericAddExpression(expression: Expression, expression2: Expression) extends Expression
{
	override def evaluate(evaluationContext : EvaluationContext) : RDFNode =
	  //FIX ME: Check if expressions are numbers. check if they are int or float
	  Literal(expression.evaluateAsFloatValue(evaluationContext) + expression2.evaluateAsFloatValue(evaluationContext), XSD_FLOAT)
		  
	override def prettyPrint = "lower case(" + expression   + ")" // FIXME
}
*/


/*case class NumericSubtractExpression(expression: Expression, expression2: Expression) extends Expression
{
	override def evaluate(evaluationContext : EvaluationContext) : RDFNode =
	  //FIX ME: Check if expressions are numbers. check if they are int or float
	  Literal(expression.evaluateAsFloatValue(evaluationContext) - expression2.evaluateAsFloatValue(evaluationContext), XSD_FLOAT)
		  
	override def prettyPrint = "lower case(" + expression   + ")" // FIXME
}*/

case class NumericMultiplyExpression(expression: Expression, expression2: Expression) extends Expression
{
	override def evaluate(evaluationContext : EvaluationContext) : RDFNode =
	  //FIX ME: Check if expressions are numbers. check if they are int or float
	  Literal(expression.evaluateAsFloatValue(evaluationContext) * expression2.evaluateAsFloatValue(evaluationContext), XSD_FLOAT)
		  
	override def prettyPrint = "lower case(" + expression   + ")" // FIXME
}

case class NumericDivideExpression(expression: Expression, expression2: Expression) extends Expression
{
	override def evaluate(evaluationContext : EvaluationContext) : RDFNode =
	  //FIX ME: Check if expressions are numbers. check if they are int or float
	  Literal(expression.evaluateAsFloatValue(evaluationContext) / expression2.evaluateAsFloatValue(evaluationContext), XSD_FLOAT)
		  
	override def prettyPrint = "lower case(" + expression   + ")" // FIXME
}

case class NumericIntegerDivideExpression(expression: Expression, expression2: Expression) extends Expression
{
	override def evaluate(evaluationContext : EvaluationContext) : RDFNode =
	  //FIX ME: Check if expressions are numbers. check if they are int or float
	  Literal((expression.evaluateAsFloatValue(evaluationContext) / expression2.evaluateAsFloatValue(evaluationContext)).round, XSD_FLOAT)
		  
	override def prettyPrint = "lower case(" + expression   + ")" // FIXME
}

case class NumericModExpression(expression: Expression, expression2: Expression) extends Expression
{
	override def evaluate(evaluationContext : EvaluationContext) : RDFNode =
	  //FIX ME: Check if expressions are numbers. check if they are int or float
	  Literal(expression.evaluateAsFloatValue(evaluationContext) % expression2.evaluateAsFloatValue(evaluationContext), XSD_FLOAT)
		  
	override def prettyPrint = "numeric mod(" + expression   +", " + expression2   + ")" // FIXME
}

case class EqualThanExpression(expression: Expression, expression2: Expression) extends Expression
{
	override def evaluate(evaluationContext : EvaluationContext) : RDFNode =
	  //FIX ME: Check if expressions are numbers. check if they are int or float
	  if(expression.evaluateAsFloatValue(evaluationContext) == expression2.evaluateAsFloatValue(evaluationContext))
	    LITERAL_TRUE
	  else
	  	LITERAL_FALSE
		  
	override def prettyPrint = "numeric-equal(" + expression   +", " + expression2   +")"
}

case class BiggerThanExpression(expression: Expression, expression2: Expression) extends Expression
{
	override def evaluate(evaluationContext : EvaluationContext) : RDFNode =
	  //FIX ME: Check if expressions are numbers. check if they are int or float
	  if(expression.evaluateAsFloatValue(evaluationContext) > expression2.evaluateAsFloatValue(evaluationContext))
	    LITERAL_TRUE
	  else
	  	LITERAL_FALSE
		  
	override def prettyPrint = "numeric-greater-than(" + expression   +", " + expression2   +")"
}

case class SmallerThanExpression(expression: Expression, expression2: Expression) extends Expression
{
	override def evaluate(evaluationContext : EvaluationContext) : RDFNode =
	  //FIX ME: Check if expressions are numbers. check if they are int or float
	  if(expression.evaluateAsFloatValue(evaluationContext) < expression2.evaluateAsFloatValue(evaluationContext))
	    LITERAL_TRUE
	  else
	  	LITERAL_FALSE
		  
	override def prettyPrint = "smaller-than(" + expression   +", " + expression2   +")"
}

case class AbsExpression(expression: Expression) extends Expression
{
	override def evaluate(evaluationContext : EvaluationContext) : RDFNode =
	  //FIX ME: Check if expressions are numbers. check if they are int or float
	  Literal(scala.math.abs(expression.evaluateAsFloatValue(evaluationContext)), XSD_INT) 
		  
	override def prettyPrint = "abs(" + expression   +")"
}

case class FloorExpression(expression: Expression) extends Expression
{
	override def evaluate(evaluationContext : EvaluationContext) : RDFNode =
	  //FIX ME: Check if expressions are numbers. check if they are int or float
	  Literal(scala.math.floor(expression.evaluateAsFloatValue(evaluationContext)), XSD_INT) 
		  
	override def prettyPrint = "floor(" + expression   +")"
}

case class RoundExpression(expression: Expression) extends Expression
{
	override def evaluate(evaluationContext : EvaluationContext) : RDFNode =
	  //FIX ME: Check if expressions are numbers. check if they are int or float
	  Literal(scala.math.round(expression.evaluateAsFloatValue(evaluationContext)), XSD_INT) 
		  
	override def prettyPrint = "round(" + expression   +")"
}

case class CeilExpression(expression: Expression) extends Expression
{
	override def evaluate(evaluationContext : EvaluationContext) : RDFNode =
	  //FIX ME: Check if expressions are numbers. check if they are int or float
	  Literal(scala.math.ceil(expression.evaluateAsFloatValue(evaluationContext)), XSD_INT) 
		  
	override def prettyPrint = "ceil(" + expression   +")"
}



//FIX ME: Type control
case class IntExpression(expression : Expression, separator :Option[String] = None) extends Expression{
  
  override def evaluate(evaluationContext:EvaluationContext) = {
    val evaluatedExpression =  expression.evaluateAsStringValue(evaluationContext)
    separator match{
      case Some(sep) =>
        try  Literal(Integer.parseInt(evaluatedExpression.replaceAllLiterally(sep, ""), 10), XSD_INT)
      case None =>  try  Literal(Integer.parseInt(evaluatedExpression, 10), XSD_INT)
    }
    
  }   
  override def prettyPrint = "int(" + expression.toString + ")"
	
}
case class FloatExpression(expression : Expression, separator :Option[String] = None) extends Expression{
  
  override def evaluate(evaluationContext:EvaluationContext) = {
    val evaluatedExpression =  expression.evaluateAsStringValue(evaluationContext)
    separator match{
      case Some(sep) => try  Literal(java.lang.Float.valueOf(evaluatedExpression.replaceAllLiterally(sep, "")), XSD_FLOAT)
      case None =>  try  Literal(java.lang.Float.valueOf(evaluatedExpression), XSD_FLOAT)
    }
  }   
  override def prettyPrint = "float(" + expression.toString + ")"
	
}

case class DecimalExpression(expression : Expression, separator :Option[String] = None) extends Expression{
 //FIX ME: Decimal is a subgroup of float it is not supposed to support numbers in a simplified syntax.
   override def evaluate(evaluationContext:EvaluationContext) = {
    val evaluatedExpression =  expression.evaluateAsStringValue(evaluationContext)
    separator match{
      case Some(sep) => try  Literal(java.lang.Float.valueOf(evaluatedExpression.replaceAllLiterally(sep, "")), XSD_DECIMAL)
      case None =>  try  Literal(java.lang.Float.valueOf(evaluatedExpression), XSD_DECIMAL)
    }
  }   
  override def prettyPrint = "decimal(" + expression.toString + ")"
	
}
