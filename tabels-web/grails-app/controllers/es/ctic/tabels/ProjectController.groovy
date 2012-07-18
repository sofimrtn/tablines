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
import grails.converters.*

class ProjectController {

    static defaultAction = "index"
    
    def projectService
    def datasetProvider
    
    private def indexModel(String projectId) throws ProjectDoesNotExistException {
        return [path: projectService.getInputDir(projectId),
         files: projectService.getFiles(projectId),
         program: projectService.getProgram(projectId)]
    }
    
    def index = {
        try {
            indexModel(params.id)
        } catch (ProjectDoesNotExistException e) {
            log.error("While trying to access project ${e.projectId}", e)
            render(status: 404, text: e.getMessage())
        }
    }
    
    def list = {
        def projects = projectService.listProjects()
        withFormat {
            html { [projects: projects] }
            json {
                render projects as JSON
    	    }
        }        
    }
    
    def create = {
        try {
            projectService.createProject(params.newProjectId)
            flash.message="msg.project.successfully.created"
            flash.args = [params.newProjectId]
            redirect(action: "index", params: [id: params.newProjectId])
        } catch (ProjectDoesNotExistException e) {
            log.error("While trying to create project ${e.projectId}", e)
            render(status: 400, text: e.getMessage())            
        }
    }
    
    def delete = {
        String projectId = params.id
        Boolean confirm = params.confirm
        if (confirm) {
            try {
                projectService.deleteProject(projectId)
                flash.message = "msg.project.successfully.deleted"
                flash.args = [projectId]
                redirect(action: "list")
            } catch (ProjectDoesNotExistException e) {
                log.error("While trying to delete project ${e.projectId}", e)
                render(status: 404, text: e.getMessage())            
            }
        }
    }
    
    def rename = {
        String oldProjectId = params.id
        String newProjectId = params.newProjectId
        if (newProjectId) {
            projectService.renameProject(oldProjectId, newProjectId)
            flash.message = "msg.project.successfully.renamed"
            flash.args = [newProjectId]
            redirect(action: "index", id: newProjectId)
        } else {
            []
        }
    }

	def program = {
		String projectId = params.id
		try {
			def program = projectService.getProgram(projectId)
		    // response.setHeader("Content-Disposition", "attachment; filename=${projectId}.tabels")
            render(text: program, contentType: "text/plain", encoding: "UTF-8")
        } catch (ProjectDoesNotExistException e) {
            log.error("While trying to access project ${e.projectId}", e)
            render(status: 404, text: e.getMessage())
		}
	}
    
    def saveProgram = {
        String program = params.program
        String projectId = params.id
        try {
            projectService.saveProgram(projectId, program)
            flash.message = "msg.program.successfully.updated"
            redirect(action: "index", id: projectId)
        } catch (ProjectDoesNotExistException e) {
            log.error("While trying to access project ${e.projectId}", e)
            render(status: 404, text: e.getMessage())
        } catch (es.ctic.tabels.ParseException e) {
            log.error "Failed to save the new program: ${e.message} at line ${e.lineNumber}: ${e.line}", e
            flash.error = "msg.tabels.program.error.at.line"
            flash.args = [e.message, e.lineNumber]
            response.status = HttpURLConnection.HTTP_BAD_REQUEST
            render(view: "index", model: indexModel(projectId) + [program: program])
        } catch (es.ctic.tabels.CompileTimeTabelsException e) {
            log.error "Failed to save the new program: ${e.message}", e
            flash.error = "msg.tabels.program.error"
            flash.args = [e.message]
            response.status = HttpURLConnection.HTTP_BAD_REQUEST
            render(view: "index", model: indexModel(projectId) + [program: program])
        }
    }
    
    def downloadSource = {
        String projectId = params.id // FIXME: validate
        HttpMethod method = new GetMethod(params.sourceUrl)
        def client = new HttpClient();
        try {
            log.info "Downloading ${params.sourceUrl}"
            int statusCode = client.executeMethod(method)
            String filename = "download-${System.currentTimeMillis()}.html"
            def downloadedFile = new File(projectService.getInputDir(projectId), filename)
            OutputStream os = new FileOutputStream(downloadedFile)
            log.debug "Writing ${params.sourceUrl} to file ${downloadedFile}"
            os.write(method.responseBody);
            os.close();
            flash.message = "msg.source.url.downloaded.successfully"
            flash.args = [params.sourceUrl]
            render(view: "index", model: indexModel(projectId) + [sourceUrl: params.sourceUrl])
        } catch (ProjectDoesNotExistException e) {
            log.error("While trying to access project ${e.projectId}", e)
            render(status: 404, text: e.getMessage())
        } catch (Exception e) {
            flash.error = "msg.source.url.download.failed"
            flash.args = [params.sourceUrl, e.message]
            response.status = HttpURLConnection.HTTP_BAD_REQUEST
            render(view: "index", model: indexModel(projectId) + [sourceUrl: params.sourceUrl])
        } finally {
            method.releaseConnection()
        }
    }
    
    def listInputs = {
        String projectId = params.id
        try {
            def inputs = projectService.listInputs(projectId)
            withFormat {
                // html { [inputs: inputs] }
                json {
                    render inputs as JSON
        	    }
            }        
        } catch (ProjectDoesNotExistException e) {
            log.error("While trying to access project ${e.projectId}", e)
            render(status: 404, text: e.getMessage())
        }
    }
    
    def uploadInput = {
        String projectId = params.id
        def f = request.getFile('file')
        if (f.empty) {
            response.sendError(400)
        } else {
            try {
                projectService.saveInput(projectId, f)
                response.sendError(200, 'Done')   
            } catch (ProjectDoesNotExistException e) {
                log.error("While trying to access project ${e.projectId}", e)
                render(status: 404, text: e.getMessage())
            }            
        }
    }
    
    def deleteInput = {
        String projectId = params.id
        String filename = params.filename
        try {
            projectService.deleteInput(projectId, filename)
            response.sendError(200, 'Done')
        } catch (ProjectDoesNotExistException e) {
            log.error("While trying to access project ${e.projectId}", e)
            render(status: 404, text: e.getMessage())
        }            
    }
    
    def datasetInfo = {
        String projectId = params.id
        try {
            def model = projectService.getModel(projectId)
            render(contentType:"text/json") {
            	info(triplesCount: model.size())
            };
        } catch (ProjectDoesNotExistException e) {
            log.error("While trying to access project ${e.projectId}", e)
            render(status: 404, text: e.getMessage())
        } catch(RunTimeTabelsException e){
			log.error "Failed to execute transformation: ${e.message}", e
			flash.error = "msg.transformation.failed"
			flash.args = [e.message]
            response.status = HttpURLConnection.HTTP_INTERNAL_ERROR
            render(view: "index", model: indexModel(projectId))
        }
    }
    
    def autogenerateProgram = {
        String projectId = params.id
        try {
            projectService.autogenerateProgram(projectId, params.strategy)
            flash.message = "msg.tabels.program.autogenerated.successfully"
            render(view: "index", model: indexModel(projectId))
        } catch (ProjectDoesNotExistException e) {
            log.error("While trying to access project ${e.projectId}", e)
            render(status: 404, text: e.getMessage())
        }
    }
    
    def data = {
        String projectId = params.id
        try{
			def model = projectService.getModel(projectId)
	        
	        if (request.method == "HEAD") {
	            render HttpServletResponse.SC_OK // otherwise Grails will return 404, see http://adhockery.blogspot.com/2011/08/grails-gotcha-beware-head-requests-when.html
	        } else {
	            log.debug "Serializing RDF response, ${model.size()} triples"
				withFormat {
				    rdfxml {
						response.contentType = "application/rdf+xml"
					    response.setHeader("Content-Disposition", "attachment; filename=${projectId}.rdf")
			            model.write(response.outputStream, "RDF/XML")
					}
				    ttl {
						response.contentType = "text/turtle"
					    response.setHeader("Content-Disposition", "attachment; filename=${projectId}.ttl")
			            model.write(response.outputStream, "TURTLE")
					}
				    text {
						response.contentType = "text/plain"
					    response.setHeader("Content-Disposition", "attachment; filename=${projectId}.nt")
			            model.write(response.outputStream, "N-TRIPLE")
					}
				} ?: response.sendError(response.SC_UNSUPPORTED_MEDIA_TYPE)
	        }
        } catch (ProjectDoesNotExistException e) {
            log.error("While trying to access project ${e.projectId}", e)
            render(status: 404, text: e.getMessage())
        } catch(RunTimeTabelsException e){
			log.error "Failed to execute transformation: ${e.message}", e
			flash.error = "msg.transformation.failed"
			flash.args = [e.message]
            response.status = HttpURLConnection.HTTP_INTERNAL_ERROR
            render(view: "index", model: indexModel(projectId))
        }
		
    }
    
	def sparql = {
	    String projectId = params.id
		def projects = (projectId != null) ? [projectId] : projectService.listProjects()
		def namedGraphs = (projectId != null) ? [] : (projects.collect { getGraph(it) })
		try {
		    if (params.query == null || params.forceForm) {
				def defaultQuery = (projectId != null) ?
					"SELECT *\nWHERE { ?s ?p ?o }\nLIMIT 50" :
					"SELECT *\n" + namedGraphs.collect { "FROM NAMED <${it}>\n" }.join("") + "WHERE { GRAPH ?g { ?s ?p ?o } }\nLIMIT 50"
		        render(view:"sparqlForm", model: [query: (params.query == null ? defaultQuery : params.query), namedGraphs: namedGraphs])
		    } else {
        		SparqlEndpoint endpoint = EndpointFactory.createDefaultSparqlEndpoint()
				if (projectId != null) {
			        endpoint.setDefaultNamedGraph(projectService.getModel(projectId))
				} else {
			        projects.each { 
			            log.info "Loading model of project ${it} as named graph ${getGraph(it)} in SPARQL endpoint"
			            endpoint.addNamedGraph(getGraph(it), projectService.getModel(it))
			        }
				}
        	    endpoint.setRequest(request)
        		endpoint.setResponse(response)
        		if (endpoint.isQuery()) {
    				log.info("Executing SPARQL query (result format=${endpoint.getFormat()}) over projects ${projects}: ${params.query}")
    				if (endpoint.isSelectQuery() && MimeTypes.HTML.equals(endpoint.getFormat())) {
    					def results = endpoint.getResults().getResult()
    					def vars = ResultSetHelper.extractVariables(results)
    					def tuples = ResultSetHelper.extractTuples(results)
    					def size = tuples.size()					
    					log.debug "Serializing to HTML ${size} results"
    					render(view: "sparqlQuery", model: [vars: vars, tuples: tuples, size: size, query: params.query])
    				} else {
    					endpoint.query() // bypass GSP rendering
    				}
        		} else {
        			render(view:"sparqlForm", model: [query: params])
        		}
    		}
		} catch (QueryParseException e) {
            log.error("While parsing query: ${params.query}", e)
            flash.error = "msg.sparql.parse.error"
            flash.args = [e.message]
            render(view:"sparqlForm", model: [query: params.query], status: HttpURLConnection.HTTP_BAD_REQUEST)
        } catch (ProjectDoesNotExistException e) {
            log.error("While trying to execute SPARQL query on project ${e.projectId}", e)
            flash.error = "msg.project.does.not.exist.error"
            flash.args = [e.projectId]
            render(status: 404, text: e.getMessage())
		} catch (Exception e) {
			log.error("While executing SPARQL query: ${params.query}", e)
			render(status: 500, text: e.getMessage())
		}
	}
	
	def tapinos = {
	    try {
            log.info("Providing the list of datasets to the view");
            def datasets = datasetProvider.getSuggestions(null); // FIXME: broken due to multi-project
    	    [datasets: datasets ]
        } catch (ProjectDoesNotExistException e) {
            log.error("While trying to access project ${e.projectId}", e)
            render(status: 404, text: e.getMessage())
	    }
	}
	
	def exhibit = {
	    try {
	        String projectId = params.id
    	    Set facets = projectService.getResources(projectId).collectMany { it.keySet() }.toList() as Set
    	    facets = facets -  ["label", "type", "prefLabel", "uri", "id"]
    	    [facets: facets]
        } catch (ProjectDoesNotExistException e) {
            log.error("While trying to access project ${e.projectId}", e)
            render(status: 404, text: e.getMessage())
        }
	}
	
	def exhibitData = {
	    String projectId = params.id
	    try {
    	    render(contentType: "text/json") {
    	        items = array {
    	            for (resource in projectService.getResources(projectId).take(100)) {
    	                item resource
    	            }
    	        }
    	    }
        } catch (ProjectDoesNotExistException e) {
            log.error("While trying to access project ${e.projectId}", e)
            render(status: 404, text: e.getMessage())
        }
	}
	
	def map = {
	    String projectId = params.id
	    try {
	        [geopoints: projectService.getGeopoints(projectId)]
        } catch (ProjectDoesNotExistException e) {
            log.error("While trying to access project ${e.projectId}", e)
            render(status: 404, text: e.getMessage())
        }
	}
	
	def parrot = {
	    String projectId = params.id
	    try {
    	    def model = projectService.getModel(projectId)
    	    def os = new ByteArrayOutputStream()
    		model.write(os, "RDF/XML")
    	    [documentText: os.toString()]
        } catch (ProjectDoesNotExistException e) {
            log.error("While trying to access project ${e.projectId}", e)
            render(status: 404, text: e.getMessage())
        }    
	}
	
	def trace = {
	    String projectId = params.id
	    try {
    	    def trace = projectService.getTrace(projectId)
    	    [trace: trace]
	    } catch (ProjectDoesNotExistException e) {
            log.error("While trying to access project ${e.projectId}", e)
            render(status: 404, text: e.getMessage())
        }    
	}
	
	private String getGraph(String projectId) {
	    // FIXME: not really portable
	    return ConfigurationHolder.config.grails.serverURL + "/project/${projectId}"
	}
	
	def config = {
	    [
	    proxyHost: projectService.configObject.proxyHost,
	    proxyPort: projectService.configObject.proxyPort,
	    tabelsPath: projectService.configObject.tabelsPath,
	    tabelsDir: projectService.configObject.tabelsDir
	    ]
	}
	
}
