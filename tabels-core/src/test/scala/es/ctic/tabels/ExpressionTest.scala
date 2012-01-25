package es.ctic.tabels

import es.ctic.tabels.Dimension._
import org.scalatest.junit.JUnitSuite
import org.junit.Test
import org.junit.Assert._

class IntETest extends JUnitSuite {
  
  @Test def evaluate{
    val evaluationContext: EvaluationContext = null
    assertEquals(Literal("1",XSD_INT) , IntExpression(LiteralExpression(Literal("1",XSD_STRING))).evaluate(evaluationContext))
    assertEquals(Literal("14",XSD_INT) , IntExpression(LiteralExpression(Literal("14"))).evaluate(evaluationContext))
    assertEquals(Literal("345.56",XSD_INT) , IntExpression(LiteralExpression(Literal("345.56"))).evaluate(evaluationContext))
    assertEquals(Literal("345,56",XSD_INT) , IntExpression(LiteralExpression(Literal("345,56"))).evaluate(evaluationContext))
    assertEquals(Literal("23.345,56",XSD_INT) , IntExpression(LiteralExpression(Literal("23.345,56"))).evaluate(evaluationContext))
    assertEquals(Literal("23.223.345,56",XSD_INT) , IntExpression(LiteralExpression(Literal("23.223.345,56"))).evaluate(evaluationContext))
  }

}
