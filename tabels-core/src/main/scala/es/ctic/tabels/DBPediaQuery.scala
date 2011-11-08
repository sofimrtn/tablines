package es.ctic.tabels
import com.hp.hpl.jena.query.Query;
import com.hp.hpl.jena.query.QueryExecution;
import com.hp.hpl.jena.query.QueryExecutionFactory;
import com.hp.hpl.jena.query.QueryFactory;
import com.hp.hpl.jena.query.QuerySolution;
import com.hp.hpl.jena.query.ResultSet;
import com.hp.hpl.jena.query.ResultSetFormatter

import scala.collection.JavaConversions._
import scala.collection._

class DBPediaQuery {
  
  def END_POINT = "http://dbpedia.org/sparql"
  
  
  
  def queryResource(rdfNode:RDFNode):RDFNode = {
    val queryString =
    "PREFIX dbont: <http://dbpedia.org/ontology/> "+
    "PREFIX dbp: <http://dbpedia.org/property/>"+
    "PREFIX geo: <http://www.w3.org/2003/01/geo/wgs84_pos#>"+
    "   SELECT ?musician  ?place"+
    "   WHERE {  "+
    "       ?musician dbont:birthPlace ?place ."+
    "        }"


    val query = QueryFactory.create(queryString);

    val qexec = QueryExecutionFactory.sparqlService(END_POINT, query);
   
    	val results  = qexec.execSelect();
    	var querySolution : QuerySolution = null
    	
    	while(results.hasNext){
    	  querySolution = results.nextSolution()
    	  println("Resultado: " + querySolution.get("place").toString)
    	}
    	
    	//ResultSetFormatter.out(System.out, results, query)
    	
    	val lit = Literal(querySolution.get("place").toString)    	
    	qexec.close()
    	return lit
    	
    // Result processing is done here.
    
  }
}


