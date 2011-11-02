package es.ctic.tabels

import es.ctic.tabels.Dimension._
import org.scalatest.junit.JUnitSuite
import org.junit.Test
import org.junit.Assert._

class RegexExpressionTest extends JUnitSuite {
  
  @Test def evaluate{
    val evaluationContext: EvaluationContext = null
    assertEquals(LITERAL_TRUE , RegexExpression(LiteralExpression(Literal("hola")), """hola""".r).evaluate(evaluationContext))
    assertEquals(LITERAL_FALSE , RegexExpression(LiteralExpression(Literal("hola")), """adios""".r).evaluate(evaluationContext))
    
  }

}

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

class substringAfterTest extends JUnitSuite{
  @Test def evaluate{
    val evaluationContext: EvaluationContext = null
    assertEquals(Literal("efg",XSD_STRING) , SubstringAfterExpression(LiteralExpression(Literal("abcdefg")), LiteralExpression(Literal("d"))).evaluate(evaluationContext))
    assertEquals(Literal("g",XSD_STRING) , SubstringAfterExpression(LiteralExpression(Literal("abcdefg")), LiteralExpression(Literal("f"))).evaluate(evaluationContext))
    assertEquals(Literal("",XSD_STRING) , SubstringAfterExpression(LiteralExpression(Literal("abcdefg")), LiteralExpression(Literal("g"))).evaluate(evaluationContext))
    assertEquals(Literal("",XSD_STRING) , SubstringAfterExpression(LiteralExpression(Literal("abcdefg")), LiteralExpression(Literal("r"))).evaluate(evaluationContext))
    assertEquals(Literal("efg",XSD_STRING) , SubstringAfterExpression(LiteralExpression(Literal("abcdefg")), LiteralExpression(Literal("cd"))).evaluate(evaluationContext))
    assertEquals(Literal("defg",XSD_STRING) , SubstringAfterExpression(LiteralExpression(Literal("abcdefg")), LiteralExpression(Literal("abc"))).evaluate(evaluationContext))
  }
}

class substringBeforeTest extends JUnitSuite{
  @Test def evaluate{
    val evaluationContext: EvaluationContext = null
    assertEquals(Literal("abc",XSD_STRING) , SubstringBeforeExpression(LiteralExpression(Literal("abcdefg")), LiteralExpression(Literal("d"))).evaluate(evaluationContext))
    assertEquals(Literal("abcde",XSD_STRING) , SubstringBeforeExpression(LiteralExpression(Literal("abcdefg")), LiteralExpression(Literal("f"))).evaluate(evaluationContext))
    assertEquals(Literal("",XSD_STRING) , SubstringBeforeExpression(LiteralExpression(Literal("abcdefg")), LiteralExpression(Literal("a"))).evaluate(evaluationContext))
    assertEquals(Literal("",XSD_STRING) , SubstringBeforeExpression(LiteralExpression(Literal("abcdefg")), LiteralExpression(Literal("r"))).evaluate(evaluationContext))
    assertEquals(Literal("ab",XSD_STRING) , SubstringBeforeExpression(LiteralExpression(Literal("abcdefg")), LiteralExpression(Literal("cd"))).evaluate(evaluationContext))
    assertEquals(Literal("abcd",XSD_STRING) , SubstringBeforeExpression(LiteralExpression(Literal("abcdefg")), LiteralExpression(Literal("efg"))).evaluate(evaluationContext))
  }
}