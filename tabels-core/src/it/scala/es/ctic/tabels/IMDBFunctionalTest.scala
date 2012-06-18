package es.ctic.tabels

import org.junit.Test
import org.junit.Assert._

class IMDBFunctionalTest extends AbstractFunctionalTest {

	val program = """
        IN sheets "0"
            FOR ?rowId IN rows FILTER get-row(?rowId)
                MATCH [?rank,?rating,?title,?votes] IN horizontal 
                LET ?titleTrimmed = trim(substring-before(?title,"("))
                LET ?year = int(substring(?title,int-add(last-index-of(?title,"("),1),4))
                LET ?rankInt = int(substring-before(?rank,"."))
                LET ?votesInt = int(translate(?votes,",",""))
                LET ?ratingFloat = float(?rating)
        {
           [    rdf:type ex:Movie;
                ex:rank ?rankInt ;
    	        ex:rating ?ratingFloat ;
    	        rdfs:label ?titleTrimmed ;
                ex:year ?year ;
    	        ex:votes ?votesInt ]
        }
        """
    val spreadsheets = Seq("imdb-top-250.html")

    @Test def testMovie {
		val model = runTabels()
		assertTrue(model.size > 0)
		assertAskTrue(model, """
		    ASK { [ a ex:Movie ;
		        rdfs:label "Inception" ;
		        ex:rank "13"^^xsd:int ;
		        ex:year "2010"^^xsd:int ;
		        ex:rating "8.8"^^xsd:float ;
		        ex:votes "487784"^^xsd:int
		    ] }""")
    }
    
}

