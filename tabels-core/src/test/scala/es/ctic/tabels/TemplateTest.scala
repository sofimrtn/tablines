package es.ctic.tabels

import org.scalatest.junit.JUnitSuite
import org.junit.Test
import org.junit.Assert._

class LiteralTest extends JUnitSuite {
  
  @Test def truthValue {
      assertTrue(LITERAL_TRUE.truthValue)
      assertFalse(LITERAL_FALSE.truthValue)
      assertTrue(Literal("true", rdfType = XSD_BOOLEAN).truthValue)
      assertTrue(Literal("1", rdfType = XSD_BOOLEAN).truthValue)    
      assertFalse(Literal("false", rdfType = XSD_BOOLEAN).truthValue)
      assertFalse(Literal("0", rdfType = XSD_BOOLEAN).truthValue)    
  }

}

