package es.ctic.tabels
import scala.util.matching.Regex

abstract class Expression {
  
  def evaluate(evaluationContext : EvaluationContext) : RDFNode
  
  def prettyPrint() : String
  
  override def toString = prettyPrint

}

case class VariableReference(variable:Variable) extends Expression{
  
  override def evaluate(evaluationContext : EvaluationContext) = evaluationContext.bindings.getValue(variable)
  override def prettyPrint = variable.toString

}

case class AddVariableExpression(variable : Variable, expression: Expression) extends Expression{
  
  override def evaluate(evaluationContext : EvaluationContext) = evaluationContext.bindings.getValue(variable) + expression.evaluate(evaluationContext).asString.value.toString
  override def prettyPrint = variable.toString + "+" + expression.toString

}

case class ResourceExpression(expression:Expression, uri : Resource) extends Expression {
  
  override def evaluate(evaluationContext : EvaluationContext) = uri + expression.evaluate(evaluationContext).asString.value.toString
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
	
    return Literal(expressions.map(_.evaluate(evaluationContext).asString.value.toString).mkString(separator.evaluate(evaluationContext).asString.value.toString), XSD_STRING). asInstanceOf[RDFNode]
  
   override def prettyPrint = "string join(" + expressions.map(_ toString).mkString(",")  + " , "+ separator + ")"
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



