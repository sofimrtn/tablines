package es.ctic.tabels

import es.ctic.tapinos.source.SparqlDataSource
import com.hp.hpl.jena.query.Query;
import com.hp.hpl.jena.query.ResultSet;
import com.hp.hpl.jena.query.QueryExecution;
import com.hp.hpl.jena.query.QueryExecutionFactory;

class SparqlDataSourceImpl implements SparqlDataSource {
    
    def projectService
    
    public ResultSet execSelectQuery(Query query) {
        def model = projectService.globalModel
        QueryExecution qe = QueryExecutionFactory.create(query, model)
        ResultSet results = qe.execSelect()
        return results
    }

}