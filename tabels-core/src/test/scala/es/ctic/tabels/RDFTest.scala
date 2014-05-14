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
      assertEquals(LITERAL_TRUE, Literal("5", XSD_INTEGER).asBoolean)
      assertEquals(LITERAL_TRUE, Literal("5.5", XSD_DOUBLE).asBoolean)
      assertEquals(LITERAL_TRUE, Literal("5.5", XSD_FLOAT).asBoolean)
      assertEquals(LITERAL_TRUE, Literal("5.5", XSD_DECIMAL).asBoolean)
      assertEquals(LITERAL_FALSE, Literal("0", XSD_INTEGER).asBoolean)
      assertEquals(LITERAL_FALSE, Literal("0.0", XSD_DOUBLE).asBoolean)
      assertEquals(LITERAL_FALSE, Literal("0.0", XSD_FLOAT).asBoolean)
      assertEquals(LITERAL_FALSE, Literal("0.0", XSD_DECIMAL).asBoolean)
  }
  
  @Test def asInt {
      // see http://www.w3.org/TR/xpath-functions/#casting-to-numerics for details
      assertEquals(Literal(10, XSD_INTEGER), Literal(10, XSD_INTEGER).asInt)
      assertEquals(Literal(9, XSD_INTEGER), Literal(9.81, XSD_DOUBLE).asInt)
      assertEquals(Literal(-17, XSD_INTEGER), Literal(-17.89, XSD_DOUBLE).asInt)
      assertEquals(Literal(1, XSD_INTEGER), LITERAL_TRUE.asInt)
      assertEquals(Literal(0, XSD_INTEGER), LITERAL_FALSE.asInt)
      assertEquals(Literal(3, XSD_INTEGER), Literal("3.14", XSD_DOUBLE).asInt)
      assertEquals(Literal(3, XSD_INTEGER), Literal("3.14", XSD_STRING).asInt)
     
  }
  
  @Test def asFloat {
      // see http://www.w3.org/TR/xpath-functions/#casting-to-numerics for details
      assertEquals(Literal(10.0, XSD_FLOAT), Literal(10.0, XSD_DOUBLE).asFloat)
  }
  
  @Test (expected = classOf[TypeConversionException]) def asIntDoesNotParse {
      Literal("3,14", XSD_STRING).asInt
  }

  @Test (expected = classOf[TypeConversionException]) def asIntDoesNotParse2 {
      Literal("Unparseable", XSD_STRING).asInt
  }

  @Test (expected = classOf[TypeConversionException]) def asFloatDoesNotParse {
      Literal("Unparseable", XSD_STRING).asFloat
  }

  @Test (expected = classOf[TypeConversionException]) def asDoubleDoesNotParse {
      Literal("Unparseable", XSD_STRING).asDouble
  }
  
}
	class NamedResourceTest extends JUnitSuite {


    val prefixes = Seq(("foo" ,NamedResource("http://example.org/foo/")),("ex" ,NamedResource("http://example.org/")))
	  val resource1 = NamedResource("http://example.org/foo/bar")
	  val resource2 = NamedResource("http://example.org/doo")
	  val resource3 = NamedResource("http://example.com/doo")
    val resource4 = NamedResource("mailto:foo@example.org")
    val resource5 = NamedResource("phone:678548479")
    val resource6 = NamedResource("fax:678548479")
    val resource7 = NamedResource("http://example.org/tabÑels/project/test")
    val resource8 = NamedResource("http://example.org/tab els/project/test")


    @Test def toCurie {
        assertEquals(Some("foo:bar"),resource1.toCurie(prefixes))
        assertEquals(Some("foo:bar"),resource1.toCurie(prefixes.reverse))
        assertEquals(Some("ex:doo"),resource2.toCurie(prefixes))
        assertEquals(None,resource3.toCurie(prefixes))
      }

     @Test def exceptionThrowingTest {
       val resource4 = NamedResource("http://localhost:8080/tabels/project")
       try {
         val resource5 = NamedResource("http://localhost")
       } catch {
         case e:ServerReferedURIException => assert(true)
         case _ => fail()
       }

       val resource6 = NamedResource("http://localhost:8080/tabels/project/test/")

       try {
         val resource7 = NamedResource("fadsf//localhost:8080/tabels/project/test/")
       } catch {
         case e:NotValidUriException => assert(true)
         case _ => fail()
       }

     }

	}

