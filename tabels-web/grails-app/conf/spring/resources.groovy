// Place your Spring DSL code here
beans = {
    
    sparqlDataSource(es.ctic.tabels.SparqlDataSourceImpl) {
        projectService = ref("projectService")
    }
    
    chartGenerator(es.ctic.data.tapinos.chart.ChartGenerator, sparqlDataSource)

    datasetInspector(es.ctic.data.tapinos.chart.DatasetInspector, sparqlDataSource)
    
}
