package es.ctic.tabels

import com.hp.hpl.jena.vocabulary.RDF
import com.hp.hpl.jena.vocabulary.RDFS
import com.hp.hpl.jena.rdf.model.Resource
import com.hp.hpl.jena.rdf.model.Model
import com.hp.hpl.jena.rdf.model.ModelFactory
import org.apache.commons.io.FileUtils

class ProjectService {

    static transactional = true
    static scope = "singleton"

    static String defaultProgramFilename = "default.tabels"
    
    File tabelsDir = new File(FileUtils.tempDirectory, "tabels")
    File inputDir = new File(tabelsDir, "upload")
    File programFile = new File(inputDir, defaultProgramFilename)
    File outputCache = new File(tabelsDir, "output.rdf")

    def File[] getFiles() {
        FileUtils.forceMkdir(inputDir)
        log.debug "Listing files in temporary dir: ${inputDir}"
        inputDir.listFiles()
    }
    
    def boolean isCacheValid() {
        FileUtils.forceMkdir(inputDir)
        if (outputCache.exists() == false || inputDir.list().length == 0) {
            return false
        } else {
            return inputDir.listFiles().every { FileUtils.isFileOlder(it, outputCache) }
        }
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
        FileUtils.forceMkdir(inputDir)
        return new DataAdaptersDelegate(DataAdapter.findAllRecognizedFilesFromDirectory(inputDir))
    }
    
    def getModel() throws RunTimeTabelsException {
        FileUtils.forceMkdir(inputDir)
        Model model = null
        if (isCacheValid()) {
            log.info "Returning cached model"
            model = ModelFactory.createDefaultModel()
            model.read(new FileInputStream(outputCache), null, "RDF/XML")
        } else {
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
            model = dataOutput.model
        
    		if (programFile.exists() == false) {
    		    saveProgram(program)
    	    }

    	    // save cache
    	    def os = new FileOutputStream(outputCache)
            model.write(os, "RDF/XML")
            os.close()
            log.info "Saved model cache at ${outputCache}"
        }
	    
	    return model
    }
    
    def getResources() throws RunTimeTabelsException {
		def model = getModel()
		def subjectsIterator = model.listSubjects()
		def subjects = []
		while(subjectsIterator.hasNext()) {
		    def subject = subjectsIterator.nextResource()
		    def description = [uri: subject.getURI()]
		    description["label"] = getLabel(subject)
	        def stmtIterator = subject.listProperties()
	        while(stmtIterator.hasNext()) {
	            def stmt = stmtIterator.nextStatement()
	            if (stmt.getPredicate() != RDFS.label) {
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

    def saveProgram(String newProgram) throws ParseException, CompileTimeTabelsException {
        def parser = new TabelsParser()
        def program = parser.parseProgram(newProgram) // validates the program
        programFile.setText(newProgram)
    }
    
}
