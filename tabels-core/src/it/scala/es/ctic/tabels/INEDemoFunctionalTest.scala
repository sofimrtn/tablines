package es.ctic.tabels

import org.junit.Test
import org.junit.Assert._

class INEDemoFunctionalTest extends AbstractFunctionalTest {

	val program = """
        	PREFIX inv: <http://data.fundacionctic.org/easturias/inversiones#>
	  
		    IN sheets "por comarcas SADEI"
		      IN cols "1" {
		        FOR ?rowId IN rows FILTER not Matches(?rowId,"(Comarca.*)?")
		            MATCH [?municipio,?tdtimp,?tdtinv,?v4,?v5,?v6,?v7,?v8,?v9,?v10,?hogarimp,?hogarinv,?ftthimp,?ftthinv,?v15,?v16,?sateliteimp,?sateliteinv,?v19,?v20,?v21,?v22,?v23,?v24,?v25,?v26,?v27,?v28,?bandaimp,?bandainv,?totalinv,?comarca] IN horizontal
		               LET ?zonaAsResource = resource(translate(?municipio, "áéíóúñÁÉÍÓÚÑ ", "aeiounAEIOUN-"),<http://data.fundacionctic.org/easturias/inversiones#Muncipio->)
		            LET ?municipioAsResource = resource(translate(?municipio, "áéíóúñÁÉÍÓÚÑ ", "aeiounAEIOUN-"),<http://data.fundacionctic.org/easturias/inversiones#Muncipio->)
		               LET ?comarcaDeMunicipioAsResource = resource(translate(?comarca, "áéíóúñÁÉÍÓÚÑ ", "aeiounAEIOUN-"),<http://data.fundacionctic.org/easturias/ine#>)
		    
		            LET ?nombreMunicipio = setLangTag(?rowId,"es")
		            LET ?tdtimpf = Float(?tdtimp) LET ?tdtinvf = Float(?tdtinv) LET ?hogarinvf = Float(?hogarinv) LET ?ftthinvf = Float(?ftthinv)
		            LET ?sateliteinvf = Float(?sateliteinv) LET ?bandainvf = Float(?bandainv)
		                                 
		            LET ?resourcetdtimp = resource(get-row(?municipio),<http://localhost:8080/tabels-web/pubby/resource/tdtimp/>)
		            LET ?resourcetdtinv = resource(get-row(?municipio),<http://localhost:8080/tabels-web/pubby/resource/tdtinv/>)
		            LET ?resourcehogarimp = resource(get-row(?municipio),<http://localhost:8080/tabels-web/pubby/resource/hogarimp/>)
		            LET ?resourcehogarinv = resource(get-row(?municipio),<http://localhost:8080/tabels-web/pubby/resource/hogarinv/>)                                 
		            LET ?resourceftthimp = resource(get-row(?municipio),<http://localhost:8080/tabels-web/pubby/resource/ftthimp/>)
		            LET ?resourceftthinv = resource(get-row(?municipio),<http://localhost:8080/tabels-web/pubby/resource/ftthinv/>)
		            LET ?resourcesateliteimp = resource(get-row(?municipio),<http://localhost:8080/tabels-web/pubby/resource/sateliteimp/>)
		            LET ?resourcesateliteinv = resource(get-row(?municipio),<http://localhost:8080/tabels-web/pubby/resource/sateliteinv/>)
		            LET ?resourcebandaimp = resource(get-row(?municipio),<http://localhost:8080/tabels-web/pubby/resource/bandaimp/>)
		            LET ?resourcebandainv = resource(get-row(?municipio),<http://localhost:8080/tabels-web/pubby/resource/bandainv/>)
		
		               LET ?datasetimpactosAsResource = resource("Dataset-Impactos-Municipios",<http://data.fundacionctic.org/easturias/inversiones#>)
		               LET ?datasetinversionesAsResource = resource("Dataset-Inversiones-Municipios",<http://data.fundacionctic.org/easturias/inversiones#>)
		;
		        FOR ?rowId IN rows FILTER Matches(?rowId,"Comarca.*")
		         MATCH [?comarca,?tdtimp,?tdtinv,?v4,?v5,?v6,?v7,?v8,?v9,?v10,?hogarimp,?hogarinv,?ftthimp,?ftthinv,?v15,?v16,?sateliteimp,?sateliteinv,?v19,?v20,?v21,?v22,?v23,?v24,?v25,?v26,?v27,?v28,?bandaimp,?bandainv,?totalinv] IN horizontal
		            LET ?zonaAsResource = resource(translate(substring(?comarca,8), "áéíóúñÁÉÍÓÚÑ ", "aeiounAEIOUN-"),<http://data.fundacionctic.org/easturias/ine#>)
		
		            LET ?tdtimpf = Float(?tdtimp) LET ?tdtinvf = Float(?tdtinv) LET ?hogarinvf = Float(?hogarinv) LET ?ftthinvf = Float(?ftthinv)
		            LET ?sateliteinvf = Float(?sateliteinv) LET ?bandainvf = Float(?bandainv)
		                                 
		            LET ?resourcetdtimp = resource(get-row(?comarca),<http://localhost:8080/tabels-web/pubby/resource/tdtimp/>)
		            LET ?resourcetdtinv = resource(get-row(?comarca),<http://localhost:8080/tabels-web/pubby/resource/tdtinv/>)
		            LET ?resourcehogarimp = resource(get-row(?comarca),<http://localhost:8080/tabels-web/pubby/resource/hogarimp/>)
		            LET ?resourcehogarinv = resource(get-row(?comarca),<http://localhost:8080/tabels-web/pubby/resource/hogarinv/>)                                 
		            LET ?resourceftthimp = resource(get-row(?comarca),<http://localhost:8080/tabels-web/pubby/resource/ftthimp/>)
		            LET ?resourceftthinv = resource(get-row(?comarca),<http://localhost:8080/tabels-web/pubby/resource/ftthinv/>)
		            LET ?resourcesateliteimp = resource(get-row(?comarca),<http://localhost:8080/tabels-web/pubby/resource/sateliteimp/>)
		            LET ?resourcesateliteinv = resource(get-row(?comarca),<http://localhost:8080/tabels-web/pubby/resource/sateliteinv/>)
		            LET ?resourcebandaimp = resource(get-row(?comarca),<http://localhost:8080/tabels-web/pubby/resource/bandaimp/>)
		            LET ?resourcebandainv = resource(get-row(?comarca),<http://localhost:8080/tabels-web/pubby/resource/bandainv/>)
		
		               LET ?datasetimpactosAsResource = resource("Dataset-Impactos-Comarcas",<http://data.fundacionctic.org/easturias/inversiones#>)
		               LET ?datasetinversionesAsResource = resource("Dataset-Inversiones-Comarcas",<http://data.fundacionctic.org/easturias/inversiones#>)              
		}
		{
		    ?municipioAsResource a <http://data.fundacionctic.org/easturias/inversiones#Municipios>;
		     skos:prefLabel ?nombreMunicipio;
		     geo:locatedIn ?comarcaDeMunicipioAsResource  
		}
		        
		{
		    ?resourcetdtimp a scv:Item ;
		     rdf:value ?tdtimpf;
		     scv:dimension inv:Proyecto-TDT ;
		     scv:dimension inv:Impacto-Poblacion-Cubierta ;
		     scv:dimension ?zonaAsResource ;
		     scv:dataset ?datasetimpactosAsResource
		}
		{
		    ?resourcetdtinv a scv:Item ;
		      rdf:value ?tdtinvf ;
		      scv:dimension inv:Proyecto-TDT ;
		      scv:dimension ?zonaAsResource ;
		      scv:dataset ?datasetinversionesAsResource
		}
		{
		    ?resourcehogarimp a scv:Item ;
		     rdf:value ?hogarimp ;
		     scv:dimension inv:Proyecto-HogarTIC ;
		     scv:dimension inv:Impacto-Subvenciones ;
		     scv:dimension ?zonaAsResource ;
		     scv:dataset ?datasetimpactosAsResource
		}
		{
		    ?resourcehogarinv a scv:Item ;
		      rdf:value ?hogarinvf ;
		      scv:dimension inv:Proyecto-HogarTIC ;
		      scv:dimension ?zonaAsResource ;
		      scv:dataset ?datasetinversionesAsResource
		}
		{
		    ?resourceftthimp a scv:Item ;
		     rdf:value ?ftthimp ;
		     scv:dimension inv:Proyecto-FTTH ;
		     scv:dimension inv:Impacto-Viviendas-FTTH ;
		     scv:dimension ?zonaAsResource ;
		     scv:dataset ?datasetimpactosAsResource
		}
		{
		    ?resourceftthinv a scv:Item ;
		      rdf:value ?ftthinvf ;
		      scv:dimension inv:Proyecto-FTTH ;
		      scv:dimension ?zonaAsResource ;
		      scv:dataset ?datasetinversionesAsResource
		}
		{
		    ?resourcesateliteimp a scv:Item ;
		     rdf:value ?sateliteimp ;
		     scv:dimension inv:Proyecto-InternetSatelite ;
		     scv:dimension inv:Impacto-Solicitudes ;
		     scv:dimension ?zonaAsResource ;
		     scv:dataset ?datasetimpactosAsResource
		}
		{
		    ?resourcesateliteinv a scv:Item ;
		      rdf:value ?sateliteinvf ;
		      scv:dimension inv:Proyecto-InternetSatelite ;
		      scv:dimension ?zonaAsResource ;
		      scv:dataset ?datasetinversionesAsResource
		}
		{
		    ?resourcebandaimp a scv:Item ;
		     rdf:value ?bandaimp ;
		     scv:dimension inv:Proyecto-BandaAncha ;
		     scv:dimension inv:Impacto-Viviendas-Banda-Ancha ;
		     scv:dimension ?zonaAsResource ;
		     scv:dataset ?datasetimpactosAsResource
		}
		{
		    ?resourcebandainv a scv:Item ;
		      rdf:value ?bandainvf ;
		      scv:dimension inv:Proyecto-BandaAncha ;
		      scv:dimension ?zonaAsResource ;
		      scv:dataset ?datasetinversionesAsResource
		}
        """
    val spreadsheets = Seq("Calculo presupuestario inversiones.xls")

    @Test def testDemo {
		val model = runTabels()
		assertTrue(model.size > 0)
		assertAskTrue(model, """
		    PREFIX inv: <http://data.fundacionctic.org/easturias/inversiones#>
		    ASK { <http://localhost:8080/tabels-web/pubby/resource/tdtinv/70> a scv:Item ;
			       scv:dataset inv:Dataset-Inversiones-Municipios ;
				   scv:dimension inv:Muncipio-Llanera ;
				   scv:dimension inv:Proyecto-TDT ;
				   rdf:value "111843.01"^^xsd:float ;
		     }""")
    }
    
}

