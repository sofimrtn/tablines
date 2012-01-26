package es.ctic.tabels

/*
 * NUMERIC Expressions
 *  
 */

object NumericFunctions extends FunctionCollection {

   
    // ==========================================    
    // XPath 2.0 operators on numeric values (6.2)
    // ==========================================    
    
    val numericAdd = "numeric-add" isDefinedBy { (x : Double, y : Double) => x + y }
    val numericSubstract = "numeric-substract" isDefinedBy { (x : Double, y : Double) => x - y }
    val numericMultiply = "numeric-multiply" isDefinedBy { (x : Double, y : Double) => x * y }
    val numericDivide = "numeric-divide" isDefinedBy { (x : Double, y : Double) =>if(y!=0) x / y else throw new ZeroDivision }
    val numericIntegerDivide = "numeric-integer-divide" isDefinedBy { (x : Double, y : Double) => if(y!=0) (x / y).round else throw new ZeroDivision }
    val numericMod = "numeric-mod" isDefinedBy { (x : Double, y : Double) => if(y!=0) x % y else throw new ZeroDivision }
    // FIXME: numeric-unary-plus is missing
    // FIXME: numeric-unary-minus is missing
    
    // ==========================================
    // XPath 2.0 comparison operators on numeric values (6.3)
    // ==========================================    
    
    val equal = "numeric-equal" isDefinedBy { (x : Double, y : Double) => x == y }
    val greaterThan = "numeric-greater-than" isDefinedBy { (x : Double, y : Double) => x > y }
    val lessThan = "numeric-less-than" isDefinedBy { (x : Double, y : Double) => x < y }

    // ==========================================    
    // XPath 2.0 functions on numeric values (6.4)
    // ==========================================

    val abs = "abs" isDefinedBy { (x : Double) => scala.math.abs(x)  }
    val ceiling = "ceiling" isDefinedBy { (x : Double) => scala.math.ceil(x)  }
    val floor = "floor" isDefinedBy { (x : Double) => scala.math.floor(x)  }
    val round = "round" isDefinedBy { (x : Double) => scala.math.round(x)  }
    // FIXME: round-half-to-even is missing
    val int = "int" isDefinedBy { (x : Int) => x  }
    val float = "float" isDefinedBy { (x : Float) => x  }
     
}


//FIX ME: Type control
/*case class IntExpression(expression : Expression, separator :Option[String] = None) extends Expression{
  
  override def evaluate(evaluationContext:EvaluationContext) = {
    val evaluatedExpression =  expression.evaluateAsStringValue(evaluationContext)
    separator match{
      case Some(sep) =>
        try  Literal(Integer.parseInt(evaluatedExpression.replaceAllLiterally(sep, ""), 10), XSD_INT)
      case None =>  try  Literal(Integer.parseInt(evaluatedExpression, 10), XSD_INT)
    }
    
  }   
  override def prettyPrint = "int(" + expression.toString + ")"
	
}*/
/*case class FloatExpression(expression : Expression, separator :Option[String] = None) extends Expression{
  
  override def evaluate(evaluationContext:EvaluationContext) = {
    val evaluatedExpression =  expression.evaluateAsStringValue(evaluationContext)
    separator match{
      case Some(sep) => try  Literal(java.lang.Float.valueOf(evaluatedExpression.replaceAllLiterally(sep, "")), XSD_FLOAT)
      case None =>  try  Literal(java.lang.Float.valueOf(evaluatedExpression), XSD_FLOAT)
    }
  }   
  override def prettyPrint = "float(" + expression.toString + ")"
	
}*/

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
