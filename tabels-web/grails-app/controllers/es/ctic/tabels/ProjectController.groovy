package es.ctic.tabels

import javax.servlet.http.HttpServletResponse

class ProjectController {

    def projectService
    
    def index = {
        [path: projectService.workDir]
    }
    
    def rdf = {
        def model = projectService.getModel()

        if (request.method == "HEAD") {
            render HttpServletResponse.SC_OK // otherwise Grails will return 404, see http://adhockery.blogspot.com/2011/08/grails-gotcha-beware-head-requests-when.html
        } else {
            log.debug "Serializing RDF response, ${model.size()} triples"
		    response.contentType = "application/rdf+xml"
            model.write(response.outputStream, "RDF/XML")
        }
    }
    
}
