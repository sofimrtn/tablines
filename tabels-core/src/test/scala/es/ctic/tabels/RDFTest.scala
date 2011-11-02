package es.ctic.tabels

import org.scalatest.junit.JUnitSuite
import org.junit.Test
import org.junit.Assert._

class LiteralTest extends JUnitSuite {
    
    @Test def testToString {
        assertEquals("\"Hello\"", Literal("Hello").toString())
        assertEquals("\"Hola\"@es", Literal("Hola", langTag = "es").toString())
        assertEquals("\"3.0\"^^<http://www.w3.org/2001/XMLSchema#float>", Literal("3.0", XSD_FLOAT).toString())
    }
  
  @Test def truthValue {
      assertTrue(LITERAL_TRUE.truthValue)
      assertFalse(LITERAL_FALSE.truthValue)
      assertTrue(Literal("true", rdfType = XSD_BOOLEAN).truthValue)
      assertTrue(Literal("1", rdfType = XSD_BOOLEAN).truthValue)    
      assertFalse(Literal("false", rdfType = XSD_BOOLEAN).truthValue)
      assertFalse(Literal("0", rdfType = XSD_BOOLEAN).truthValue)    
  }
  
  @Test def asBoolean {
      // see http://www.w3.org/TR/rdf-sparql-query/#ebv for details
      assertEquals(LITERAL_TRUE, LITERAL_TRUE.asBoolean)
      assertEquals(LITERAL_FALSE, LITERAL_FALSE.asBoolean)
      assertEquals(LITERAL_TRUE, Literal("hello").asBoolean)
      assertEquals(LITERAL_FALSE, Literal("").asBoolean)
      assertEquals(LITERAL_TRUE, Literal("5", XSD_INT).asBoolean)
      assertEquals(LITERAL_TRUE, Literal("5.5", XSD_DOUBLE).asBoolean)
      assertEquals(LITERAL_TRUE, Literal("5.5", XSD_FLOAT).asBoolean)
      assertEquals(LITERAL_TRUE, Literal("5.5", XSD_DECIMAL).asBoolean)
      assertEquals(LITERAL_FALSE, Literal("0", XSD_INT).asBoolean)
      assertEquals(LITERAL_FALSE, Literal("0.0", XSD_DOUBLE).asBoolean)
      assertEquals(LITERAL_FALSE, Literal("0.0", XSD_FLOAT).asBoolean)
      assertEquals(LITERAL_FALSE, Literal("0.0", XSD_DECIMAL).asBoolean)
  }

}

