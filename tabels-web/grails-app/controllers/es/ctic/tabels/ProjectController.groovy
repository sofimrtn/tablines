package es.ctic.tabels

import java.net.HttpURLConnection
import javax.servlet.http.HttpServletResponse
import org.fundacionctic.su4j.endpoint.EndpointFactory
import org.fundacionctic.su4j.endpoint.SparqlEndpoint
import org.fundacionctic.su4j.endpoint.http.MimeTypes
import org.fundacionctic.su4j.endpoint.utils.ResultSetHelper
import org.apache.commons.httpclient.HttpClient
import org.apache.commons.httpclient.methods.GetMethod
import org.apache.commons.httpclient.HttpMethod
import org.codehaus.groovy.grails.commons.ConfigurationHolder
import com.hp.hpl.jena.query.QueryParseException

class ProjectController {

    def projectService
    def datasetProvider
    
    private def indexModel = {
        [path: projectService.workDir,
         files: projectService.files,
         program: projectService.program]
    }
    
    def index = {
        indexModel()
    }
    
    def saveProgram = {
        String program = params.program
        log.info "Saving a new program: ${program}"
        try {
            projectService.saveProgram(program)
            log.info "The new Tabels program has been successfully saved"
            flash.message = "The Tabels program has been successfully updated"
            redirect(action: "index")
        } catch (es.ctic.tabels.ParseException e) {
            log.error "Failed to save the new program: ${e.message} at line ${e.lineNumber}: ${e.line}"
            flash.error = "Failed to save the new program: ${e.message} at line ${e.lineNumber}"
            response.status = HttpURLConnection.HTTP_BAD_REQUEST
            render(view: "index", model: indexModel() + [program: program])
        }
    }
    
    def downloadSource = {
        HttpMethod method = new GetMethod(params.sourceUrl)
        def client = new HttpClient();
        try {
            log.info "Downloading ${params.sourceUrl}"
            int statusCode = client.executeMethod(method)
            String filename = "download-${System.currentTimeMillis()}.html"
            def downloadedFile = new File(projectService.workDir, filename)
            OutputStream os = new FileOutputStream(downloadedFile)
            log.debug "Writing ${params.sourceUrl} to file ${downloadedFile}"
            os.write(method.responseBody);
            os.close();
            flash.message = "The new source from ${params.sourceUrl} has been successfully downloaded"
            render(view: "index", model: indexModel() + [sourceUrl: params.sourceUrl])
        } catch (Exception e) {
            flash.error = "Failed to download source URL ${params.sourceUrl}: ${e.message}"
            response.status = HttpURLConnection.HTTP_BAD_REQUEST
            render(view: "index", model: indexModel() + [sourceUrl: params.sourceUrl])
        } finally {
            method.releaseConnection()
        }
    }
    
    def autogenerateProgram = {
        projectService.autogenerateProgram(params.strategy)
        flash.message = "The Tabels program has been generated"
        render(view: "index", model: indexModel())
    }
    
    def rdf = {
        try{
			def model = projectService.getModel()
	        
	        if (request.method == "HEAD") {
	            render HttpServletResponse.SC_OK // otherwise Grails will return 404, see http://adhockery.blogspot.com/2011/08/grails-gotcha-beware-head-requests-when.html
	        } else {
	            log.debug "Serializing RDF response, ${model.size()} triples"
			    response.contentType = "application/rdf+xml"
			    response.setHeader("Content-Disposition", "attachment; filename=data.rdf")
	            model.write(response.outputStream, "RDF/XML")
	        }
        } catch(RunTimeTabelsException e){
			log.error "Failed to execute transformation: ${e.message}"
			flash.error = "Failed to execute transformation: ${e.message}"
            response.status = HttpURLConnection.HTTP_INTERNAL_ERROR
            render(view: "index", model: indexModel())
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
            response.status = HttpURLConnection.HTTP_BAD_REQUEST
            render(view:"sparqlForm", params: [query: params.query])
		} catch (Exception e) {
			log.error("While executing SPARQL query: ${params.query}", e)
			render(status: 500, text: e.getMessage())
		}
	}
	
	def sparqlForm = {
		log.debug("Showing SPARQL form")
		[query: params.query != null ? params.query : "SELECT * \nFROM <${graph}> \nWHERE { ?s ?p ?o }"]
	}
	
	def tapinos = {
        log.info("Providing the list of datasets to the view");
        def datasets = datasetProvider.getSuggestions(null);
	    [datasets: datasets ]
	}
	
	def exhibit = {
	}
	
	def exhibitData = {
	    render(contentType: "text/json") {
	        items = array {
	            for (resource in projectService.resources.take(100)) {
	                item resource
	            }
	        }
	    }
	}
	
	private String getGraph() {
	    // FIXME: not really portable
	    return ConfigurationHolder.config.grails.serverURL
	}
	
}
