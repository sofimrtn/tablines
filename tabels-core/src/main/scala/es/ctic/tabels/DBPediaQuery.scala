package es.ctic.tabels


import com.hp.hpl.jena.query.QueryExecutionFactory;
import com.hp.hpl.jena.query.QueryFactory;
import com.hp.hpl.jena.query.QuerySolution;


class DBPediaQuery {
  
  def END_POINT = "http://dbpedia.org/sparql"
  
  
  
  def queryResource(rdfNode:RDFNode):RDFNode = {
    val queryString =
    "PREFIX dbont: <http://dbpedia.org/ontology/> "+
    "PREFIX dbp: <http://dbpedia.org/property/>"+
    "PREFIX geo: <http://www.w3.org/2003/01/geo/wgs84_pos#>"+
    "   SELECT ?subject ?predicate"+
    "   WHERE {  "+
    "       "+ rdfNode.toString()+" a ?predicate ."+
    "        }"


    val query = QueryFactory.create(queryString);

    val qexec = QueryExecutionFactory.sparqlService(END_POINT, query);
   
    	val results  = qexec.execSelect();
    	var querySolution : QuerySolution = null
    	
    	while(results.hasNext){
    	  querySolution = results.nextSolution()
    	  println("Resultado: " + querySolution.get("predicate").toString)
    	}
    	
    	//ResultSetFormatter.out(System.out, results, query)
    	
    	val lit = NamedResource(querySolution.get("predicate").toString)    	
    	qexec.close()
    	return lit
    	
    // Result processing is done here.
    
  }
}


