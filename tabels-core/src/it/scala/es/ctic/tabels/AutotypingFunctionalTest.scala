package es.ctic.tabels

import org.junit.Test
import org.junit.Assert._

class AutotypingFunctionalTest extends AbstractFunctionalTest {

	val program = """
	    FOR ?row IN ROWS
	        MATCH [?v1,?v2]
	        LET ?r = resource(?v1, <http://example.org/>)
	    {
	        ?r rdf:value ?v2
	    }
	    """
    val spreadsheets = Seq("types.xls")

    @Test def testAutotyping {
		val model = runTabels()
		assertTrue(model.size > 0)
		assertAskTrue(model, """ASK { ex:One   rdf:value "6"^^xsd:int }""")
		assertAskTrue(model, """ASK { ex:Two   rdf:value "6.6"^^xsd:decimal }""")
		assertAskTrue(model, """ASK { ex:Three rdf:value "0.06"^^xsd:decimal }""")
		assertAskTrue(model, """ASK { ex:Four  rdf:value "6"^^xsd:int }""")
		assertAskTrue(model, """ASK { ex:Five  rdf:value "-6"^^xsd:int }""")
		assertAskTrue(model, """ASK { ex:Six   rdf:value "6"^^xsd:int }""")
		assertAskTrue(model, """ASK { ex:Seven rdf:value "6"^^xsd:int }""")
		assertAskTrue(model, """ASK { ex:Eight rdf:value "Thu Jan 12 01:00:00 CET 2012"^^xsd:date }""") // beware of time zone!
		assertAskTrue(model, """ASK { ex:Nine  rdf:value "-6"^^xsd:int }""")
    }
    
}

