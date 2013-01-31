package es.ctic.tabels

import org.scalatest.junit.JUnitSuite
import org.junit.Test
import org.junit.Assert._
import com.hp.hpl.jena.query.QueryParseException

class JenaDataOutputErrorTest extends JUnitSuite {

    val prefixes = Map("ex" -> NamedResource("http://example.org/"))
    val jenaDataOutput = new JenaDataOutput(prefixes)
    val statement1 = jenaDataOutput.model.createStatement(jenaDataOutput.model.createResource("http://example.org/a"),jenaDataOutput.model.createProperty("http://example.org/b"),jenaDataOutput.model.createResource("http://example.org/c") : com.hp.hpl.jena.rdf.model.Resource)
    
    @Test def executeJenaRule() {
        jenaDataOutput.model.add(statement1)
        assertEquals(1, jenaDataOutput.model.size())
        jenaDataOutput.executeJenaRule("[rule1: (?x ex:b ?z) -> (?z ex:d ?x)]")
        assertEquals(2, jenaDataOutput.model.size())
    }
    
    /*
     * @Test(expected = classOf[com.hp.hpl.jena.reasoner.rulesys.Rule$ParserException]) def executeInvalidJenaRule() {
        jenaDataOutput.executeJenaRule("CRASH")
    }*/
    
    @Test def executeSparql() {
        jenaDataOutput.model.add(statement1)
        assertEquals(1, jenaDataOutput.model.size())
        jenaDataOutput.executeSparql("DROP ALL")
        assertEquals(0, jenaDataOutput.model.size())
        jenaDataOutput.executeSparql("INSERT DATA { <http://example.org/foo> <http://example.org/bar> <http://example.org/baz> } ")
        assertEquals(1, jenaDataOutput.model.size())
        jenaDataOutput.executeSparql("INSERT { ?x ex:bar ?z } WHERE { ?z ex:bar ?x } ")
        assertEquals(2, jenaDataOutput.model.size())
    }
    
    @Test(expected = classOf[QueryParseException]) def executeInvalidSparql() {
        jenaDataOutput.executeSparql("CRASH")
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

