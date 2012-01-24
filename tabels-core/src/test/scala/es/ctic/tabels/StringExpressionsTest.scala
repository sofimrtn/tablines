package es.ctic.tabels

import es.ctic.tabels.Dimension._
import org.scalatest.junit.JUnitSuite
import org.junit.Test
import org.junit.Assert._

class StringFunctionsTest extends JUnitSuite {
    
    implicit val evaluationContext = EvaluationContext()
    
    @Test def startsWith() {
        assertTrue(StringFunctions.startsWith("caracola", "cara"))
        assertFalse(StringFunctions.startsWith("caracola", "cola"))
    }
    
    @Test def compare {
        assertEquals(0, StringFunctions.compare("foo", "foo"))
        assertEquals(-1, StringFunctions.compare("bar", "foo"))
        assertEquals(1, StringFunctions.compare("foo", "bar"))
    }
    
    @Test def translate {
        assertEquals("original", StringFunctions.translate("original", "", ""))
        assertEquals("original", StringFunctions.translate("original", "xyz", "XYZ"))
        assertEquals("0r1g1n4l", StringFunctions.translate("original", "oia", "014"))
        assertEquals("rgnl", StringFunctions.translate("original", "oia", ""))
    }
    
    @Test def substringAfter {
        assertEquals("efg", StringFunctions.substringAfter("abcdefg", "d"))
        assertEquals("g", StringFunctions.substringAfter("abcdefg", "f"))
        assertEquals("", StringFunctions.substringAfter("abcdefg", "g"))
        assertEquals("", StringFunctions.substringAfter("abcdefg", "r"))
        assertEquals("efg", StringFunctions.substringAfter("abcdefg", "cd"))
        assertEquals("defg", StringFunctions.substringAfter("abcdefg", "abc"))
    }
    
    @Test def substringBefore {
        assertEquals("abc", StringFunctions.substringBefore("abcdefg", "d"))
        assertEquals("abcde", StringFunctions.substringBefore("abcdefg", "f"))
        assertEquals("", StringFunctions.substringBefore("abcdefg", "a"))
        assertEquals("", StringFunctions.substringBefore("abcdefg", "r"))
        assertEquals("ab", StringFunctions.substringBefore("abcdefg", "cd"))
        assertEquals("abcd", StringFunctions.substringBefore("abcdefg", "efg"))
    }
}

/*
class RegexExpressionTest extends JUnitSuite {
  
  @Test def evaluate{
    val evaluationContext: EvaluationContext = null
    assertEquals(LITERAL_TRUE , RegexExpression(LiteralExpression(Literal("hola")), """hola""".r).evaluate(evaluationContext))
    assertEquals(LITERAL_FALSE , RegexExpression(LiteralExpression(Literal("hola")), """adios""".r).evaluate(evaluationContext))
    
  }

}
*/
