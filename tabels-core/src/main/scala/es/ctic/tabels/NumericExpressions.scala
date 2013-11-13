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
    val numericDivide = "numeric-divide" isDefinedBy { (x : Double, y : Double) =>if(y!=0) x / y else throw new ZeroDivisionException }
    val numericIntegerDivide = "numeric-integer-divide" isDefinedBy { (x : Double, y : Double) => if(y!=0) (x / y).round else throw new ZeroDivisionException }
    val numericMod = "numeric-mod" isDefinedBy { (x : Double, y : Double) => if(y!=0) x % y else throw new ZeroDivisionException }
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
    val intOrElse = "int" isDefinedBy { (ec : EvaluationContext, x : Any, default : Int) => try {
            x.toString.toFloat.toInt
        } catch {
            case e : Exception => default
        }
    }
    val float = "float" isDefinedBy { (x : Float) => x  }
    val double = "double" isDefinedBy { (x : Double) => x  }
    val doubleOrElse = "double" isDefinedBy { (ec : EvaluationContext, x : Any, default : Double) => try {
            x.toString.toDouble
        } catch {
            case e : Exception => default
        }
    }
    
    // extra functions
    val intAdd = "int-add" isDefinedBy { (x : Int, y : Int) => x + y }
    val intSubstract = "int-substract" isDefinedBy { (x : Int, y : Int) => x - y }
    val intMultiply = "int-multiply" isDefinedBy { (x : Int, y : Int) => x * y }
    val intDivide = "int-divide" isDefinedBy { (x : Int, y : Int) =>if(y!=0) x / y else throw new ZeroDivisionException }
    val isDouble = "is-double" isDefinedBy{(x:Any)=> x.isInstanceOf[Double]}
    val canBeDouble = "can-be-double" isDefinedBy{(x:Any)=> if(x.isInstanceOf[Double])
    															true
    														else{ 
    														  try{if(x.toString.toDouble== 0) true
    															  else true
    														  }
    														  catch{
    														    case e: Exception => false
    														  }  
    														}
    												}
    val isInt = "is-int" isDefinedBy{(x:Any)=> x.isInstanceOf[Int]}
    val canBeInt = "can-be-int" isDefinedBy{(x:Any)=> if(x.isInstanceOf[Int])
    															true
    														else{ 
    														  try{if(x.toString.toDouble.toInt== 0) true
    															  else true
    														  }
    														  catch{
    														    case e: Exception => false
    														  }  
    														}
    												}
}
    														  


case class DecimalExpression(expression : Expression, separator :Option[String] = None) extends Expression{
 //FIXME: Decimal is a subgroup of float it is not supposed to support numbers in a simplified syntax.
   override def evaluate(evaluationContext:EvaluationContext) = {
    val evaluatedExpression =  expression.evaluateAsStringValue(evaluationContext)
    separator match{
      case Some(sep) => try  Literal(java.lang.Float.valueOf(evaluatedExpression.replaceAllLiterally(sep, "")), XSD_DECIMAL)
      case None =>  try  Literal(java.lang.Float.valueOf(evaluatedExpression), XSD_DECIMAL)
    }
  }   
  override def prettyPrint = "decimal(" + expression.toString + ")"
	
}
