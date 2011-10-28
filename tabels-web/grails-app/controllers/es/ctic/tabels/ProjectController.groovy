package es.ctic.tabels

import javax.servlet.http.HttpServletResponse

class ProjectController {

    def index = { }
    
    def rdf = {
        log.info "And Tabular Cells!"
        def workDir = "/tmp/tabels/uploadr/"
        def tabelsFilename = "default.tabels"
        def dataSource = ExcelDataSource.loadAllExcelFilesFromDirectory(new File(workDir))
        log.debug "Datasource includes these files: ${dataSource.filenames}"
		def parser = new TabelsParser()
        def programFile = new File(workDir + tabelsFilename)
        log.debug "Using Tabels program: ${programFile.canonicalPath} (available? ${programFile.exists()})" 
        def program = (programFile.exists()) ? parser.parseProgram(programFile) : Autogenerator.autogenerateProgram(dataSource)
		def interpreter = new Interpreter()
		def dataOutput = new JenaDataOutput(program.prefixesAsMap())
		interpreter.interpret(program, dataSource, dataOutput)

		if (programFile.exists() == false) {
		    def tos = new PrintStream(new FileOutputStream(programFile))
		    int indent = 0
		    def prettyPrinter = new PrettyPrint(indent)
		    program.accept(prettyPrinter)
		    log.info "The writing the following program to the file ${programFile.canonicalPath}:\n${prettyPrinter.toString()}"
		    tos.print(prettyPrinter.toString())
		    tos.close()
	    }

        if (request.method == "HEAD") {
            render HttpServletResponse.SC_OK // otherwise Grails will return 404, see http://adhockery.blogspot.com/2011/08/grails-gotcha-beware-head-requests-when.html
        } else {
            log.debug "Serializing RDF response, ${dataOutput.model.size()} triples"
		    response.contentType = "application/rdf+xml"
            dataOutput.model.write(response.outputStream, "RDF/XML")
        }
    }
    
}
