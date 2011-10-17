package es.ctic.tabels

import es.ctic.tabels.Dimension._
import org.scalatest.junit.JUnitSuite
import org.junit.Test
import org.junit.Assert._

class RegexExpressionTest extends JUnitSuite{
  
  @Test def evaluate{
    val evaluationContext: EvaluationContext = null
    assertEquals(LITERAL_TRUE , RegexExpression(LiteralExpression("hola"), """hola""".r).evaluate(evaluationContext))
    assertEquals(LITERAL_FALSE , RegexExpression(LiteralExpression("hola"), """adios""".r).evaluate(evaluationContext))
    
  }

}
class IntETest extends JUnitSuite{
  
  @Test def evaluate{
     val evaluationContext: EvaluationContext = null
    assertEquals(Literal("1",XSD_INT) , IntE(LiteralExpression("1")).evaluate(evaluationContext))
    assertEquals(Literal("345.56",XSD_INT) , IntE(LiteralExpression("345.56")).evaluate(evaluationContext))
    assertEquals(Literal("345,56",XSD_INT) , IntE(LiteralExpression("345,56")).evaluate(evaluationContext))
    assertEquals(Literal("23.345,56",XSD_INT) , IntE(LiteralExpression("23.345,56")).evaluate(evaluationContext))
    assertEquals(Literal("23.223.345,56",XSD_INT) , IntE(LiteralExpression("23.223.345,56")).evaluate(evaluationContext))

  }

}