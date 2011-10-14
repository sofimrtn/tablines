package es.ctic.tabels

import org.scalatest.junit.JUnitSuite
import org.junit.Test
import org.junit.Assert._

class JenaDataOutputTest extends JUnitSuite {

    val jenaDataOutput = new JenaDataOutput()
    
    
  @Test def createStringLiteralObject {
      val input = Literal("hello")
      val output = jenaDataOutput.createObject(input)
      assertTrue(output.isLiteral)
      assertEquals("hello", output.asLiteral.getString)
      assertEquals("", output.asLiteral.getLanguage)
      assertNull(output.asLiteral.getDatatypeURI) // this may change in future versions of RDF
  }

  @Test def createTaggedLiteralObject {
      val input = Literal("hello", langTag = "en")
      val output = jenaDataOutput.createObject(input)
      assertTrue(output.isLiteral)
      assertEquals("hello", output.asLiteral.getString)
      assertEquals("en", output.asLiteral.getLanguage)
      assertNull(output.asLiteral.getDatatypeURI) // this may change in future versions of RDF
  }

  @Test def createDoubleLiteralObject {
      val input = Literal("3.1415", XSD_DOUBLE)
      val output = jenaDataOutput.createObject(input)
      assertTrue(output.isLiteral)
      assertEquals("3.1415", output.asLiteral.getString)
      assertEquals("", output.asLiteral.getLanguage)
      assertEquals(XSD_DOUBLE.uri, output.asLiteral.getDatatypeURI)
  }

  @Test def createIntLiteralObject {
      val input = Literal("3", XSD_INT)
      val output = jenaDataOutput.createObject(input)
      assertTrue(output.isLiteral)
      assertEquals("3", output.asLiteral.getString)
      assertEquals("", output.asLiteral.getLanguage)
      assertEquals(XSD_INT.uri, output.asLiteral.getDatatypeURI)
  }

  @Test def createBooleanLiteralObject {
      val input = LITERAL_TRUE
      val output = jenaDataOutput.createObject(input)
      assertTrue(output.isLiteral)
      assertEquals("true", output.asLiteral.getString)
      assertEquals("", output.asLiteral.getLanguage)
      assertEquals(XSD_BOOLEAN.uri, output.asLiteral.getDatatypeURI)
  }

}

