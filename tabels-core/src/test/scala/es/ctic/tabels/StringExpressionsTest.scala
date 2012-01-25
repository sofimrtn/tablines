package es.ctic.tabels

import es.ctic.tabels.Dimension._
import org.scalatest.junit.JUnitSuite
import org.junit.Test
import org.junit.Assert._

class StringFunctionsTest extends JUnitSuite {
    
    implicit val evaluationContext = EvaluationContext()
    
     // ==========================================    
    // XPath 2.0 functions
    // ==========================================    

    // FIXME: concat is missing
    // FIXME: string-join is missing
	@Test def substring {
        assertEquals("bcde", StringFunctions.substring3("abcdefg", 1,4))
        assertEquals("abcd", StringFunctions.substring3("abcdefg", 0,4))
        assertEquals("defg", StringFunctions.substring3("abcdefg", 3,4))
        assertEquals("b", StringFunctions.substring3("abcdefg", 1,1))
        assertEquals("bcdefg", StringFunctions.substring2("abcdefg", 1))
        assertEquals("g", StringFunctions.substring2("abcdefg", 6))
        assertEquals("abcdefg", StringFunctions.substring2("abcdefg", 0))
    }
	
    @Test def stringLength {
        assertEquals(8, StringFunctions.stringLength("original"))
        assertEquals(0, StringFunctions.stringLength(""))
        assertEquals(1, StringFunctions.stringLength("0"))
        
    }
    // FIXME: normalize-space is missing
    // FIXME: normalize-unicode is missing
    @Test def upperCase {
        assertEquals("ORIGINAL", StringFunctions.upperCase("original"))
        assertEquals("ORIGINAL", StringFunctions.upperCase("ORIGINAL"))
        assertEquals("0R1G1N4L", StringFunctions.upperCase("0r1g1n4l"))
        
    }
    @Test def lowerCase {
        assertEquals("original", StringFunctions.lowerCase("original"))
        assertEquals("original", StringFunctions.lowerCase("ORIGINAL"))
        assertEquals("0r1g1n4l", StringFunctions.lowerCase("0R1G1N4l"))
        
    }
    @Test def translate {
        assertEquals("original", StringFunctions.translate("original", "", ""))
        assertEquals("original", StringFunctions.translate("original", "xyz", "XYZ"))
        assertEquals("0r1g1n4l", StringFunctions.translate("original", "oia", "014"))
        assertEquals("rgnl", StringFunctions.translate("original", "oia", ""))
    }
    // FIXME: encode-for-uri is missing
    // FIXME: iri-to-uri is missing
    // FIXME: escape-html-uri is missing
    
    // ==========================================    
    // XPath 2.0 predicates
    // ==========================================    
    
    @Test def contains {
        assertTrue(StringFunctions.contains("caracola", "cara"))
        assertFalse(StringFunctions.contains("caracola", "hola"))
    }
    @Test def startsWith {
        assertTrue(StringFunctions.startsWith("caracola", "cara"))
        assertFalse(StringFunctions.startsWith("caracola", "cola"))
    }
    @Test def endsWith() {
        assertTrue(StringFunctions.endsWith("caracola", "cola"))
        assertFalse(StringFunctions.endsWith("caracola", "cara"))
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
   	
    @Test def replace {
        assertEquals("abc0efg", StringFunctions.replace("abcdefg", "d".r, "0"))
        assertEquals("abcde", StringFunctions.replace("abcdefg", "fg".r, ""))
        assertEquals("", StringFunctions.replace("abcdefg", "[a-z]".r, ""))
        assertEquals("", StringFunctions.replace("abcdefg", "abcdefg".r, ""))
        assertEquals("", StringFunctions.replace("abcdefg", "ab[a-z]+".r, ""))
        assertEquals("abcde", StringFunctions.replace("abcdefg", "[0-9]*fg".r, ""))
    }
    
    @Test def compare {
        assertEquals(0, StringFunctions.compare("foo", "foo"))
        assertEquals(-1, StringFunctions.compare("bar", "foo"))
        assertEquals(1, StringFunctions.compare("foo", "bar"))
    }
    // FIXME: codepoint-equal is missing

    // ==========================================    
    // Other functions and predicates not in XPath 2.0
    // ==========================================    
     
    // FIXME: levenshteinDistance is missing
    @Test def string {
        assertEquals("foo", StringFunctions.string("foo"))
        assertEquals("bar", StringFunctions.string("bar"))
       
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
