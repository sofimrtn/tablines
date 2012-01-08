// Place your Spring DSL code here
beans = {
    
    locale(java.util.Locale, "es")
    
    sparqlDataSource(es.ctic.tabels.SparqlDataSourceImpl) {
        projectService = ref("projectService")
    }
    
    datasetProvider(es.ctic.data.tapinos.autocomplete.AutocompleteFromEndpoint, sparqlDataSource, "http://purl.org/NET/scovo#Dataset") {
		rdfLocale = ref("locale")
		labelProperty = "http://www.w3.org/2004/02/skos/core#prefLabel"
	}

    chartGenerator(es.ctic.data.tapinos.chart.ChartGenerator, sparqlDataSource)

    datasetInspector(es.ctic.data.tapinos.chart.DatasetInspector, sparqlDataSource)
    
}
