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

