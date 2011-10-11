package es.ctic.tabels
import scala.util.matching.Regex

abstract class Expression extends EvaluableExpression{
  
  def evaluate(evaluationContext : EvaluationContext) : RDFNode

}

case class VariableReference(variable:Variable) extends Expression{
  
  override def evaluate(evaluationContext : EvaluationContext) = evaluationContext.bindings.getValue(variable)
  }

case class ResourceExpression(expression:Expression, uri : Resource) extends Expression {
  
  override def evaluate(evaluationContext : EvaluationContext) = uri + expression.evaluate(evaluationContext).getValue
  }

case class RegexExpression(expression : Expression , re : Regex) extends Expression{
  
	override def evaluate(evaluationContext : EvaluationContext) ={ 
	   
	  println("evaluamos expresion: " + expression.evaluate(evaluationContext).getValue)
	/*  expression.evaluate(evaluationContext).getValue match{
	  case re(_) => LITERAL_TRUE
	  case _ => LITERAL_FALSE
	}*/
	 expression.evaluate(evaluationContext).getValue.matches(re.toString()) match{
	    case true =>  LITERAL_TRUE
	    case false => LITERAL_FALSE
	  }
	}
	}

case class LiteralExpression( literal : String) extends Expression{
  
  override def evaluate(evaluationContext : EvaluationContext) = Literal(value = literal)
}