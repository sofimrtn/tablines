package es.ctic.tabels

import org.scalatest.junit.JUnitSuite
import org.junit.Test
import org.junit.Assert._

class JenaDataOutputTest extends JUnitSuite {

    val prefixes = Map("ex" -> NamedResource("http://example.org/"))
    val jenaDataOutput = new JenaDataOutput(prefixes)
    
    @Test def executeJenaRule() {
        def statement1 = jenaDataOutput.model.createStatement(jenaDataOutput.model.createResource("http://example.org/a"),jenaDataOutput.model.createProperty("http://example.org/b"),jenaDataOutput.model.createResource("http://example.org/c") : com.hp.hpl.jena.rdf.model.Resource)
        jenaDataOutput.model.add(statement1)
        assertEquals(1, jenaDataOutput.model.size())
        jenaDataOutput.executeJenaRule("[rule1: (?x ex:b ?z) -> (?z ex:d ?x)]")
        assertEquals(2, jenaDataOutput.model.size())
    }
    
    
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

  @Test def createFloatLiteralObject {
      val input = Literal("3.1415", XSD_FLOAT)
      val output = jenaDataOutput.createObject(input)
      assertTrue(output.isLiteral)
      assertEquals("3.1415", output.asLiteral.getString)
      assertEquals("", output.asLiteral.getLanguage)
      assertEquals(XSD_FLOAT.uri, output.asLiteral.getDatatypeURI)
  }

  @Test def createIntLiteralObject {
      val input = Literal("3", XSD_INT)
      val output = jenaDataOutput.createObject(input)
      assertTrue(output.isLiteral)
      assertEquals("3", output.asLiteral.getString)
      assertEquals("", output.asLiteral.getLanguage)
      assertEquals(XSD_INT.uri, output.asLiteral.getDatatypeURI)
  }

  @Test def createDecimalLiteralObject {
      val input = Literal("3", XSD_DECIMAL)
      val output = jenaDataOutput.createObject(input)
      assertTrue(output.isLiteral)
      assertEquals("3", output.asLiteral.getString)
      assertEquals("", output.asLiteral.getLanguage)
      assertEquals(XSD_DECIMAL.uri, output.asLiteral.getDatatypeURI)
  }

  @Test def createBooleanLiteralObject {
      val input = LITERAL_TRUE
      val output = jenaDataOutput.createObject(input)
      assertTrue(output.isLiteral)
      assertEquals("true", output.asLiteral.getString)
      assertEquals("", output.asLiteral.getLanguage)
      assertEquals(XSD_BOOLEAN.uri, output.asLiteral.getDatatypeURI)
  }

  @Test def createDateLiteralObject {
      val input = Literal("2011-10-25", XSD_DATE)
      val output = jenaDataOutput.createObject(input)
      assertTrue(output.isLiteral)
      assertEquals("2011-10-25", output.asLiteral.getString)
      assertEquals("", output.asLiteral.getLanguage)
      assertEquals(XSD_DATE.uri, output.asLiteral.getDatatypeURI)
  }

}

