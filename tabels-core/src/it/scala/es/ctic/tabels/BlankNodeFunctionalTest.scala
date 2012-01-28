package es.ctic.tabels

import org.junit.Test
import org.junit.Assert._

class BlankNodeFunctionalTest extends AbstractFunctionalTest {

	val program = """
	    FOR ?row IN ROWS
	        MATCH [?v1,?v2,?v3]
	    {
	        [ ex:A ?v1 ; ex:B ?v2 ]
	    }
	    {
	        [] ex:C ?v3
	    }
	    """
    val spreadsheets = Seq("simple-grid.xls")

    @Test def testAutotyping {
		val model = runTabels()
		assertTrue(model.size > 0)
		assertAskTrue(model, """ASK { [] ex:A "A1" ; ex:B "B1" }""")
		assertAskTrue(model, """ASK { ?x ex:A "A1" . ?y ex:B "B2" . FILTER (?x != ?y) }""")
		assertAskTrue(model, """ASK { ?x ex:C "C1" . ?y ex:C "C2" . FILTER (?x != ?y) }""")
    }
    
}

