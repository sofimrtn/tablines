package es.ctic.tabels

import org.scalatest.junit.JUnitSuite
import org.junit.Test
import org.junit.Assert._

class AutogeneratorTest extends Autogenerator with JUnitSuite {
    
    override def autogenerateProgram(dataSource : DataSource) : S = null
    
    @Test def testLiteralToLocalName {
        assertEquals(Some("foo"), literalToLocalName(Literal("foo")))
        assertEquals(Some("foobar"), literalToLocalName(Literal("foo bar")))
        assertEquals(Some("FOOBAR"), literalToLocalName(Literal("F.//OO(BA)%#R")))
        assertEquals(None, literalToLocalName(Literal("")))
        assertEquals(Some("n2011"), literalToLocalName(Literal("2011", XSD_INT)))
    }

}