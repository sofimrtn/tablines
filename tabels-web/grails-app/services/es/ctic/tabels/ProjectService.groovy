package es.ctic.tabels

import com.hp.hpl.jena.vocabulary.RDF
import com.hp.hpl.jena.vocabulary.RDFS
import com.hp.hpl.jena.rdf.model.Resource

class ProjectService {

    static transactional = true
    static scope = "singleton"

    static String defaultProgramFilename = "default.tabels"
    
    String path = "tabels" + File.separator + "upload";
    File workDir = new File(System.getProperty("java.io.tmpdir"), path)
    File programFile = new File(workDir, defaultProgramFilename)

    def File[] getFiles() {
        log.debug "Listing files in temporary dir: ${workDir}"
        if (workDir.exists() == false) {
            log.info "Creating temporary dir: ${workDir}"
            workDir.mkdirs()
        }
        workDir.listFiles()
    }
    
    def autogenerateProgram(String strategy) {
        def autogenerator
        if (strategy == "SCOVO") {
            autogenerator = new ScovoAutogenerator()
        } else {
            autogenerator = new BasicAutogenerator(new Namespace("http://localhost:8080/tabels-web/pubby/resource/")) // FIXME: generalize
        }
        def program = autogenerator.autogenerateProgram(getDataSource())
        saveProgram(program)
    }
    
    def getDataSource() {
        return new DataAdaptersDelegate(DataAdapter.findAllRecognizedFilesFromDirectory(workDir))
    }
    
    def getModel() throws RunTimeTabelsException{
        log.info "And Tabular Cells!"
        def dataSource = getDataSource()
        log.debug "Datasource includes these files: ${dataSource.filenames}"
        log.debug "Using Tabels program: ${programFile.canonicalPath} (available? ${programFile.exists()})" 
		def parser = new TabelsParser()
		def autogenerator = new BasicAutogenerator(new Namespace("http://localhost:8080/tabels-web/pubby/resource/")) // FIXME: generalize
        def program = programFile.exists() ? parser.parseProgram(programFile) : autogenerator.autogenerateProgram(dataSource)
		def interpreter = new Interpreter()
		def dataOutput = new JenaDataOutput(program.prefixesAsMap())
		interpreter.interpret(program, dataSource, dataOutput)

		if (programFile.exists() == false) {
		    saveProgram(program)
	    }
	    
	    return dataOutput.model
    }
    
    def getResources() throws RunTimeTabelsException {
		def model = getModel()
		def subjectsIterator = model.listSubjects()
		def subjects = []
		while(subjectsIterator.hasNext()) {
		    def subject = subjectsIterator.nextResource()
		    def description = [uri: subject.getURI()]
		    description["label"] = getLabel(subject)
//		    if (subject.getProperty(RDF.type) != null) {
//		        description["type"] = getLabel(subject.getProperty(RDF.type).getResource())
//	        }
	        def stmtIterator = subject.listProperties()
	        while(stmtIterator.hasNext()) {
	            def stmt = stmtIterator.nextStatement()
	            if (stmt.getPredicate() != RDF.type) {
    	            def propName = getLabel(stmt.getPredicate())
    	            def objectLabel = stmt.getObject().isLiteral() ? stmt.getString() : getLabel(stmt.getResource())
    	            description[propName] = ((propName in description) ? description[propName] : []) + [objectLabel]
	            }
	        }
		    subjects = subjects + description
		}
		return subjects
    }
    
    private String getLabel(Resource resource) {
        def SKOS_PREFLABEL = resource.model.createProperty("http://www.w3.org/2004/02/skos/core#prefLabel")
        if (resource.getProperty(RDFS.label) != null) {
            return resource.getProperty(RDFS.label).getString()
        } else if (resource.getProperty(SKOS_PREFLABEL) != null) {
                return resource.getProperty(SKOS_PREFLABEL).getString()
        } else if (resource.getURI() == null) {
            return "Anonymous node"
        } else if (resource.getURI().contains("#")) {
            return resource.getURI().substring(resource.getURI().indexOf('#') + 1)
        } else {
            return resource.localName
        }
    }
    
    String getProgram() {
        return programFile.exists() ? programFile.getText() : ""
    }
    
    private def saveProgram(S program) {
	    int indent = 0
	    def prettyPrinter = new PrettyPrint(indent)
	    program.accept(prettyPrinter)
	    log.info "The writing the following program to the file ${programFile.canonicalPath}:\n${prettyPrinter.toString()}"
	    programFile.setText(prettyPrinter.toString())
    }

    def saveProgram(String newProgram) throws ParseException {
        def parser = new TabelsParser()
        def program = parser.parseProgram(newProgram) // validates the program
        programFile.setText(newProgram)
    }
    
}
