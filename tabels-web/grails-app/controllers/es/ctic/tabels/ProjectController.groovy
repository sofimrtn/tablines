package es.ctic.tabels

import javax.servlet.http.HttpServletResponse
import org.fundacionctic.su4j.endpoint.EndpointFactory
import org.fundacionctic.su4j.endpoint.SparqlEndpoint
import org.fundacionctic.su4j.endpoint.http.MimeTypes
import org.fundacionctic.su4j.endpoint.utils.ResultSetHelper
import org.codehaus.groovy.grails.commons.ConfigurationHolder
import com.hp.hpl.jena.query.QueryParseException

class ProjectController {

    def projectService
    
    def index = {
        [path: projectService.workDir,
         files: projectService.files,
         program: params.program != null ? params.program : projectService.program]
    }
    
    def saveProgram = {
        try {
            projectService.saveProgram(params.program)
            flash.message = "The Tabels program has been successfully updated"
            redirect(view: index)
        } catch (es.ctic.tabels.ParseException e) {
            flash.error = "Failed to save the new program: ${e.message} at line ${e.lineNumber}"
            redirect(view: index, params: [program: params.program])
        }
    }
    
    def rdf = {
        def model = projectService.getModel()

        if (request.method == "HEAD") {
            render HttpServletResponse.SC_OK // otherwise Grails will return 404, see http://adhockery.blogspot.com/2011/08/grails-gotcha-beware-head-requests-when.html
        } else {
            log.debug "Serializing RDF response, ${model.size()} triples"
		    response.contentType = "application/rdf+xml"
		    response.setHeader("Content-Disposition", "attachment; filename=data.rdf")
            model.write(response.outputStream, "RDF/XML")
        }
    }
    
	def sparqlQuery = {
		try {
    		SparqlEndpoint endpoint = EndpointFactory.createDefaultSparqlEndpoint()
    		endpoint.addNamedGraph(getGraph(), projectService.getModel())
    	    endpoint.setRequest(request)
    		endpoint.setResponse(response)
    		if (endpoint.isQuery()) {
				log.info("Executing SPARQL query: ${params.query}")
				if (endpoint.isSelectQuery() && MimeTypes.HTML.equals(endpoint.getFormat())) {
					def results = endpoint.getResults().getResult()
					def vars = ResultSetHelper.extractVariables(results)
					def tuples = ResultSetHelper.extractTuples(results)
					def size = tuples.size()					
					log.debug "Serializing to HTML ${size} results"
					return [vars: vars, tuples: tuples, size: size ]
				} else {
					endpoint.query() // bypass GSP rendering
				}
    		} else {
    			redirect(action:sparqlForm, params: params)
    		}
		} catch (QueryParseException e) {
            log.error("While parsing query: ${params.query}", e)
            flash.error = "Failed to parse SPARQL Query: ${e.message}"
            redirect(action:sparqlForm, params: [query: params.query])
		} catch (Exception e) {
			log.error("While executing SPARQL query: ${params.query}", e)
			render(status: 500, text: e.getMessage())
		}
	}
	
	def sparqlForm = {
		log.debug("Showing SPARQL form")
		[query: params.query != null ? params.query : "SELECT * \nFROM <${graph}> \nWHERE { ?s ?p ?o }"]
	}
	
	private String getGraph() {
	    // FIXME: not really portable
	    return ConfigurationHolder.config.grails.serverURL
	}
	
}
