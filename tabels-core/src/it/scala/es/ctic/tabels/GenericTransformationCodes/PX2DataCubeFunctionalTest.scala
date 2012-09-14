package es.ctic.tabels

import org.junit.Test
import org.junit.Assert._

class PX2DataCubeFunctionalTest extends AbstractFunctionalTest {

	val program = """
PREFIX project: <http://idi.fundacionctic.org/tabels/project/pxRDFDataCUbe/>
PREFIX my: <http://idi.fundacionctic.org/tabels/project/pxRDFDataCUbe/resource/>
PREFIX scv: <http://purl.org/NET/scovo#>
PREFIX scvxl: <http://idi.fundacionctic.org/scovoxl/scovoxl#>
PREFIX rdf: <http://www.w3.org/1999/02/22-rdf-syntax-ns#>
PREFIX rdfs: <http://www.w3.org/2000/01/rdf-schema#>
PREFIX skos: <http://www.w3.org/2004/02/skos/core#>
PREFIX dcat: <http://www.w3.org/ns/dcat#>
PREFIX dct: <http://purl.org/dc/terms/>
PREFIX foaf: <http://xmlns.com/foaf/0.1/#>
PREFIX qb: <http://purl.org/linked-data/cube#>

set sheets "MetaData"
    match[?title,?contents,?units,?source] in vertical at B1
    LET ?langTitle = setLangTag(?title,"es")
    LET ?resDataSet = resource(?title, <http://idi.fundacionctic.org/tabels/project/pxRDFDataCUbe/>)
	set sheets "Contents"
		{not Windowed For ?x in rows
         				 When matches(?x, "")Do
                            For ?heading in cols
                              For ?values in rows filter AND(not matches(?values, ".*@.*"),not matches(?values, ""))
                                when not contains(?values,"..") do
                                  LET ?resValue = resource(concat(get-col(?values),get-row(?values),?values),<http://idi.fundacionctic.org/tabels/project/pxRDFDataCUbe/resource/>) 
                                  WHEN matches(?heading,".*@.*")DO
                                      LET ?resDimension = resource(replace(?heading,"@","_"),<http://idi.fundacionctic.org/tabels/project/pxRDFDataCUbe/resource/>) 
                                      LET ?dimensionValueLabel = substring-before(?heading,"@")
                                      LET ?dimensionClassLabel = substring-after(?heading,"@")
                                      LET ?resComponent = resource(?dimensionClassLabel,<http://idi.fundacionctic.org/tabels/project/pxRDFDataCUbe/resource/component_>)
                                      {WHEN matches(lower-case(?dimensionClassLabel),".*year.*|.*año.*|.*periodo.*")DO
                                          LET ?resContinouosDimensionClass = resource(?dimensionClassLabel,<http://idi.fundacionctic.org/tabels/project/pxRDFDataCUbe/resource/>);
                                       WHEN not matches(lower-case(?dimensionClassLabel),".*year.*|.*año.*|.*periodo.*")DO
                                                  LET ?resDimensionClass = resource(?dimensionClassLabel,<http://idi.fundacionctic.org/tabels/project/pxRDFDataCUbe/resource/>)
                                      };
   		not windowed For ?y in cols
             			When matches(?y,"")Do
             				For ?stub in rows
                              For ?values in cols filter AND(not matches(?values, ".*@.*"),not matches(?values, ""))
                                  when not contains(?values,"..") do
                                    LET ?resValue = resource(concat(get-col(?values),get-row(?values),?values),<http://idi.fundacionctic.org/tabels/project/pxRDFDataCUbe/resource/>) 
                                        WHEN matches(?stub,".*@.*")DO
                                            LET ?resDimension = resource(replace(?stub,"@","_"),<http://idi.fundacionctic.org/tabels/project/pxRDFDataCUbe/resource/>)
                                            LET ?dimensionValueLabel = substring-before(?stub,"@")
                                            LET ?dimensionClassLabel = substring-after(?stub,"@")
                                            LET ?resComponent = resource(?dimensionClassLabel,<http://idi.fundacionctic.org/tabels/project/pxRDFDataCUbe/resource/component_>)
                                            {WHEN matches(lower-case(?dimensionClassLabel),".*year.*|.*año.*|.*periodo.*")DO
                                                LET ?resContinouosDimensionClass = resource(?dimensionClassLabel,<http://idi.fundacionctic.org/tabels/project/pxRDFDataCUbe/resource/>);
                                             WHEN not matches(lower-case(?dimensionClassLabel),".*year.*|.*año.*|.*periodo.*")DO
                                                LET ?resDimensionClass = resource(?dimensionClassLabel,<http://idi.fundacionctic.org/tabels/project/pxRDFDataCUbe/resource/>)}
       }

//Head&Stub Dimensions Template
construct{ 
  ?resDimension rdfs:label ?dimensionValueLabel .
  my:datastructure qb:component ?resComponent .
  ?resComponent qb:dimension ?resDimensionClass ;
                qb:order "10". 
  ?resDimensionClass rdfs:label ?dimensionClassLabel
}

//Head&Stub Continuous Dimensions template
construct{ 
  ?resDimension rdfs:label ?dimensionValueLabel .
  my:datastructure qb:component ?resComponent .
  ?resComponent qb:dimension ?resContinouosDimensionClass ;
                qb:order "1".
  ?resContinouosDimensionClass a scvxl:ContinuousDimension ;
  								rdfs:label ?dimensionClassLabel
  }

//dataset 
construct  
{
    ?resDataSet a qb:DataSet ;
  	 	rdfs:label ?langTitle ;
        scvxl:valuesMeasuredIn ?units;
        qb:structure my:datastructure .
  my:measure rdfs:label ?contents 
 
//?unit rdfs:label "Unidades" .
  
}



// Observation ->Item
CONSTRUCT 
{
 ?resValue a qb:Observation ;
             qb:dataSet ?resDataSet ;
             my:measure ?values
}

CONSTRUCT 
{
 ?resValue ?resDimensionClass ?resDimension .
  
 }
CONSTRUCT
{
 ?resValue ?resContinouosDimensionClass ?resDimension 
 }
           			
 CONSTRUCT { 
   my:datastructure a qb:DataStructureDefinition ;
  					qb:component [ qb:measure my:measure ] ;
					qb:component [ qb:attribute scvxl:valuesMeasuredIn ] 
  }

	  """
    val spreadsheets = Seq("Generic-PX2DataCube-EducationTest.px")

    @Test def testIMDBSample {
		val model = runTabels()
		assertTrue(model.size > 0)
		assertAskTrue(model, """
		   PREFIX my: <http://idi.fundacionctic.org/tabels/project/imdb/resource/>
		   PREFIX qb: <http://purl.org/linked-data/cube#>
		   ASK { 
<http://idi.fundacionctic.org/tabels/project/pxRDFDataCUbe/resource/85108.80> a qb:Observation ;
				}""")
    }
    
}

