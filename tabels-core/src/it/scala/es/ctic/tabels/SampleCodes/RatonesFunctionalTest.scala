package es.ctic.tabels

import org.junit.Test
import org.junit.Assert._

class MouseExperimentFunctionalTest extends AbstractFunctionalTest {

	val program = """
PREFIX project: <http://idi.fundacionctic.org/tabels/project/ratonesSample/>
PREFIX my: <http://idi.fundacionctic.org/tabels/project/ratonesSample/resource/>
PREFIX scv: <http://purl.org/NET/scovo#>
PREFIX scvxl: <http://idi.fundacionctic.org/scovoxl/scovoxl#>
PREFIX rdf: <http://www.w3.org/1999/02/22-rdf-syntax-ns#>
PREFIX rdfs: <http://www.w3.org/2000/01/rdf-schema#>
PREFIX skos: <http://www.w3.org/2004/02/skos/core#>
PREFIX dcat: <http://www.w3.org/ns/dcat#>
PREFIX dct: <http://purl.org/dc/terms/>
PREFIX foaf: <http://xmlns.com/foaf/0.1/#>



set sheets "Hoja2"
    FOR ?experimentDay IN rows STARTS AT A25 FILTER matches(?experimentDay, "DAY [0-9]+")
    LET ?resourceExperimentDay = resource(?experimentDay, <http://idi.fundacionctic.org/tabels/project/ratonesSample/resource/>)

    FOR ?treatment IN rows starts at 2 bottom of ?experimentDay filter matches(?treatment,"[a-zA-Z0-9.]+")
        LET ?resourceTreatment = resource(concat("F1+",?treatment), <http://idi.fundacionctic.org/tabels/project/ratonesSample/resource/>)
        LET ?mouse = int-substract(get-row( ?treatment),get-row(?experimentDay))
        LET ?resourceMouse = resource(?mouse,<http://idi.fundacionctic.org/tabels/project/ratonesSample/resource/>)
		{
            MATCH [?averageVolume,?deviation] IN horizontal AT 6 right of ?treatment FILTER matches(?averageVolume,"[0-9,.]+") ;
            MATCH [?height,?width,?depth,?death,?volume] IN horizontal AT 1 right of ?treatment FILTER matches(?volume,"[0-9,.]+")
        }


//SCOVO TEMPLATE DATASET Mouse-Day-Treatment
//DIMENSION EXPERIMENT DAY
construct
{
    ?resourceExperimentDay a  my:ExperimentDay ;
                           skos:prefLabel ?experimentDay .

    my:ExperimentDay rdfs:subClassOf scv:Dimension ;
                     skos:prefLabel "Experiment Day" ;
                     scvxl:weight "3" ;


                     rdfs:subClassOf scvxl:ContinuousDimension 

}


//DIMENSION TREATMENT

construct
{
    ?resourceTreatment a my:Treatment ;
                           skos:prefLabel ?treatment .

    my:Treatment rdfs:subClassOf scv:Dimension ;
                     skos:prefLabel "Treatment" ;
                     scvxl:weight "2"



}
//DIMENSION MOUSE
construct
{
    ?resourceMouse a my:Mouse ;
                           skos:prefLabel ?mouse .

    my:Mouse rdfs:subClassOf scv:Dimension ;
                     skos:prefLabel "Mouse" ;
                         scvxl:weight "1"
}

//DATA SEt & ITEM
construct
{
    [ a scv:Item ;
        scv:dataset my:IndividualTumorVolume ;
        scv:dimension ?resourceMouse ;
        scv:dimension ?resourceExperimentDay ;
        rdf:value ?volume;
        scv:dimension ?resourceTreatment;
        my:height ?height;
        my:width ?width;
        my:depth ?depth]
}
 construct
{
    my:IndividualTumorVolume a scv:Dataset ;
         skos:prefLabel "Individual Tumor Volume" ;
         scvxl:valuesMeasuredIn "Unidad volumen"

}

//SCOVO TEMPLATE DATASET Day-Treatment


//DATA SEt & ITEM
construct
{
    [ a scv:Item ;
        scv:dataset my:AverageTumorVolume ;
        scv:dimension ?resourceExperimentDay ;
        rdf:value ?averageVolume;
        scv:dimension ?resourceTreatment;
        my:standardDeviation ?deviation]
}
construct
{
    my:AverageTumorVolume a scv:Dataset ;
         skos:prefLabel "Average Tumor Volume" ;
         scvxl:valuesMeasuredIn "Unidad volumen"

}

        """
    val spreadsheets = Seq("Sample-mouse-experiment.xls")

    @Test def testMouseSample {
		val model = runTabels()
		assertTrue(model.size > 0)
		assertAskTrue(model, """
		    PREFIX my: <http://idi.fundacionctic.org/tabels/project/ratonesSample/resource/>
			PREFIX scv: <http://purl.org/NET/scovo#>
			PREFIX rdf: <http://www.w3.org/1999/02/22-rdf-syntax-ns#>
			ASK{   
		    [] a scv:Item ;
			      rdf:value "1870"^^<http://www.w3.org/2001/XMLSchema#decimal> ;
			      my:depth 10.5 ;
			      my:height 14.6 ;
			      my:width 12.2 ;
			      scv:dataset my:IndividualTumorVolume ;
			      scv:dimension <http://idi.fundacionctic.org/tabels/project/ratonesSample/resource/3> , <http://idi.fundacionctic.org/tabels/project/ratonesSample/resource/F1%2BPBS> , <http://idi.fundacionctic.org/tabels/project/ratonesSample/resource/DAY+19> .

		     }""")
		
    }
    
}

