package es.ctic.tabels

import org.junit.Test
import org.junit.Assert._

class airlineTest extends AbstractFunctionalTest {

  override val prefixes=
    """
            PREFIX project: <http://idi.fundacionctic.org/tabels/project/IATA_ICAO_AIRLINE_MATCHING/>
            PREFIX my: <http://idi.fundacionctic.org/tabels/project/IATA_ICAO_AIRLINE_MATCHING/resource/>
            PREFIX rdf: <http://www.w3.org/1999/02/22-rdf-syntax-ns#>
            PREFIX rdfs: <http://www.w3.org/2000/01/rdf-schema#>
            PREFIX dcat: <http://www.w3.org/ns/dcat#>
            PREFIX dct: <http://purl.org/dc/terms/>
            PREFIX foaf: <http://xmlns.com/foaf/0.1/#>
            PREFIX skos: <http://www.w3.org/2004/02/skos/core#>
            PREFIX emergelModules: <http://purl.org/emergel/modules#>
            PREFIX emergel: <http://purl.org/emergel/core#>

    """.stripMargin
	val program =
    """
      FOR ?rowId IN rows FILTER get-row(?rowId)
          MATCH [?ICAOCallSign,?ICAOCode,?IATACode,?AirlineCountry] IN horizontal
        LET ?Airline = substring(?AirlineCountry,0,last-index-of(?AirlineCountry,"-"))
        LET ?AirlineResource = resource(replace(replace(?Airline,"[ .'éèáíóúàìòù\"äëïöü-]",""),",",""), emergelModules)
        LET ?Country = replace(substring(?AirlineCountry,last-index-of(?AirlineCountry,"-")),"[ '-]","")
        { WHEN not matches(?Country,"") DO
              LET ?CountryResource = resource(concat("COUNTRY_",?Country),emergelModules)
           ;
            WHEN not matches(?ICAOCode,"") DO
          LET ?ICAOCodeResource = resource(concat("ICAO_AIRLINE_CODE_",replace(?ICAOCode," ","")),emergelModules)
              LET ?ICAOLabel = replace(?ICAOCode, "", "")
        ;
          WHEN not matches(?IATACode,"") DO
          LET ?IATACodeResource = resource(concat("IATA_AIRLINE_CODE_",replace(?IATACode," ","")),emergelModules)
              LET ?IATALabel = replace(?IATACode, "", "")
        }

      CONSTRUCT {


          emergelModules:ICAOAirlineClassification a emergelModules:Airline ;
                                     rdfs:label "ICAO airline classification" .

          emergelModules:IATAAirlineClassification a emergelModules:Airline ;
                                     rdfs:label "IATA airline classification" .

          emergelModules:AirlineClassification a emergelModules:Airline ;
                                     rdfs:label "Airline classification" .

      }

      CONSTRUCT {
          ?ICAOCodeResource a skos:Concept ;
                            skos:inScheme emergelModules:ICAOAirlineClassification ;
                            skos:notation ?ICAOLabel ;
                            rdfs:label ?ICAOLabel .

          ?AirlineResource emergel:hasCode ?ICAOCodeResource .

      }

      CONSTRUCT{
        ?AirlineResource a skos:Concept ;
                           emergel:callSign ?ICAOCallSign ;
                           skos:inScheme emergelModules:AirlineClassification ;
                           rdfs:label ?Airline .
      }

      CONSTRUCT{
        ?AirlineResource emergel:inCountry ?CountryResource .
      }
      CONSTRUCT{
          ?IATACodeResource a skos:Concept ;
                            skos:inScheme emergelModules:IATAAirlineClassification ;
                            skos:notation ?IATALabel ;
                            rdfs:label ?IATALabel .

          ?AirlineResource emergel:hasCode ?IATACodeResource
      }

	  """
    val spreadsheets = Seq("airlineTest.xls")

    @Test def testAirlineSample {
		val model = runTabels()
		assertTrue(model.size > 0)
		assertAskTrue(model, """
		   PREFIX my: <http://idi.fundacionctic.org/tabels/project/IATA_ICAO_AIRLINE_MATCHING/resource/>
		   PREFIX emergelModules: <http://purl.org/emergel/modules#>
       PREFIX emergel: <http://purl.org/emergel/core#>
       ASK {
		   emergelModules:FederalArmoredServiceInc emergel:callSign "Fedarm" ;
				}""")
    }
    
}

