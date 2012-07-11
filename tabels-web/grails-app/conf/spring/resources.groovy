import es.ctic.tapinos.services.*
import es.ctic.tapinos.debugger.*

// Place your Spring DSL code here
beans = {
    
//    locale(java.util.Locale, "es")
    
    sparqlDataSource(es.ctic.tabels.SparqlDataSourceImpl) {
        projectService = ref("projectService")
    }
    
    datasetProvider(AutocompleteFromEndpoint, sparqlDataSource, "http://purl.org/NET/scovo#Dataset") {
//		rdfLocale = ref("locale")
		labelProperty = "http://www.w3.org/2004/02/skos/core#prefLabel"
	}
	
	// we need to declare Tapinos services, we cannot use bean autodetection
	// because we're redefining the Autocomplete one, and if we use autodetection,
	// it will crash due to duplicate instances
    chartGenerator(ChartGenerator, sparqlDataSource)
    datasetInspector(DatasetInspector, sparqlDataSource)
    relationsInspector(RelationsInspector, sparqlDataSource)
    sanityCheckerService(SanityCheckerService, sparqlDataSource)
    
}
