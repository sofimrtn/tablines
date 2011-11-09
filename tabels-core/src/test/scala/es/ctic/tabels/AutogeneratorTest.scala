package es.ctic.tabels

import org.scalatest.junit.JUnitSuite
import org.junit.Test
import org.junit.Assert._

class AutogeneratorTest extends Autogenerator with JUnitSuite {
    
    override def autogenerateProgram(dataSource : DataSource) : S = null
    
    @Test def testLiteralToLocalName {
        assertEquals(Some("foo"), literalToLocalName(Literal("foo")))
        assertEquals(Some("fooBar"), literalToLocalName(Literal("foo bar")))
        assertEquals(Some("fOOBAR"), literalToLocalName(Literal("F.//OO(BA)%#R")))
        assertEquals(None, literalToLocalName(Literal("")))
        assertEquals(Some("n2011"), literalToLocalName(Literal("2011", XSD_INT)))
    }
    
    @Test def testLiteralsToUniqueLocalNames {
        val literals1 = List(Literal("foo"), Literal("bar"), Literal("foo"), Literal("%"), Literal("2011"))
        val localNames = literalsToUniqueLocalNames(literals1, "prop")
        assertEquals(List("foo", "bar", "prop3", "prop4", "n2011"), localNames)
    }

}