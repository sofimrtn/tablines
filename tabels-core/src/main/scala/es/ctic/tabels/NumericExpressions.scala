package es.ctic.tabels

/*
 * NUMERIC Expressions
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
		  
	override def prettyPrint = "numeric mod(" + expression   +", " + expression2   + ")"
}

case class BiggerThanExpression(expression: Expression, expression2: Expression) extends Expression
{
	override def evaluate(evaluationContext : EvaluationContext) : RDFNode =
	  //FIX ME: Check if expressions are numbers. check if they are int or float
	  if(expression.evaluate(evaluationContext).asString.value.toString.toFloat > expression2.evaluate(evaluationContext).asString.value.toString.toFloat)
	    LITERAL_TRUE
	  else
	  	LITERAL_FALSE
		  
	override def prettyPrint = "bigger-than(" + expression   +", " + expression2   +")"
}

case class SmallerThanExpression(expression: Expression, expression2: Expression) extends Expression
{
	override def evaluate(evaluationContext : EvaluationContext) : RDFNode =
	  //FIX ME: Check if expressions are numbers. check if they are int or float
	  if(expression.evaluate(evaluationContext).asString.value.toString.toFloat < expression2.evaluate(evaluationContext).asString.value.toString.toFloat)
	    LITERAL_TRUE
	  else
	  	LITERAL_FALSE
		  
	override def prettyPrint = "smaller-than(" + expression   +", " + expression2   +")"
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
