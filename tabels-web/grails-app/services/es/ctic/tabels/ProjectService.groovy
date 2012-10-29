package es.ctic.tabels

import com.hp.hpl.jena.vocabulary.RDF
import com.hp.hpl.jena.vocabulary.RDFS
import com.hp.hpl.jena.rdf.model.Resource
import com.hp.hpl.jena.rdf.model.Model
import com.hp.hpl.jena.rdf.model.ModelFactory
import com.hp.hpl.jena.query.QueryExecutionFactory
import org.apache.commons.io.FileUtils
import org.codehaus.groovy.grails.commons.ConfigurationHolder     
import scala.Some

class ProjectService {

    static transactional = true
    static scope = "singleton"
    static modelCache = [:]

    static String defaultProgramFilename = "default.tabels"
    static String configurationFilename = "configuration.properties"
    
    def configObject = new es.ctic.tabels.Config()

    File projectsDir = new File(configObject.tabelsDir, "projects")
    
    File getProjectDir(String projectId, Boolean evenIfItDoesNotExist = false) throws ProjectDoesNotExistException {
        if (projectId == null || projectId.trim().equals("")) {
            throw new ProjectDoesNotExistException("((empty))")
        } else if (! (projectId ==~ /[\w-]+/ )) {
            log.error "Project name ${projectId} is invalid"
            throw new ProjectDoesNotExistException(projectId)
        } else {
            File projectDir = new File(projectsDir, projectId)
            if (projectDir.exists() || evenIfItDoesNotExist) {
                return projectDir
            } else {
                log.error "Project ${projectId} does not exist"
                throw new ProjectDoesNotExistException(projectId)
            }
        }
    }
    
    File getInputDir(String projectId) throws ProjectDoesNotExistException {
        return new File(getProjectDir(projectId), "upload")
    }
    
    File getProgramFile(String projectId) throws ProjectDoesNotExistException {
        return new File(getProjectDir(projectId), defaultProgramFilename)
    }
    
    File getProgramMapResource(String projectId,String fileName, String folder) throws ProjectDoesNotExistException {
        return new File(getProjectDir(projectId).getAbsolutePath()+"/"+folder , fileName)
    }
    
    File getOutputCache(String projectId) throws ProjectDoesNotExistException {
        return new File(getProjectDir(projectId), "output.rdf")
    }

    def getFiles(String projectId) throws ProjectDoesNotExistException {
        log.debug "Listing files in temporary dir: ${getInputDir(projectId)}"
        getInputDir(projectId).listFiles()
    }

    def getConfigurationFile(String projectId) throws ProjectDoesNotExistException {
        def props = new Properties()
        new File(getProjectDir(projectId), configurationFilename).withInputStream { 
            stream -> props.load(stream) 
        }
        return props;
    }
    
    def listProjects() {
        FileUtils.forceMkdir(projectsDir)
        return projectsDir.listFiles().collect { it.name }
    }
    
    def listInputs(String projectId) {
        return getInputDir(projectId).listFiles().collect { it.name }
    }
    
    def saveInput(String projectId, def f) throws Exception{
        //TODO: Add specific exceptions for each filter
        def extensions=configObject.allowedExtensions.replaceAll(' ','').split(',')
        def allowed = false
        extensions.each{if(f.originalFilename.endsWith("." + it)) allowed =true}
        if(!allowed) throw new FileFormatNotAllowedException(f.originalFilename, configObject.allowedExtensions)
        
        def maxSize = configObject.maxFileSize//8789000
        if(f.size>maxSize) throw new FileTooLargeException(f.originalFilename, f.size, maxSize)
        
        def destination = new File(getInputDir(projectId), f.originalFilename)
        log.info "Saving new input file of project ${projectId} to ${destination}"
        return f.transferTo(destination)
    }
    
    def deleteInput(String projectId, String filename) {
        def f = new File(getInputDir(projectId), filename)
        log.info "Deleting file ${f} of project ${projectId}"
        return f.delete()
    }
    
    def createProject(String projectId) throws ProjectDoesNotExistException {
        log.info "Creating project ${projectId}"
        FileUtils.forceMkdir(getProjectDir(projectId, true))
        FileUtils.forceMkdir(getInputDir(projectId))
        createConfigurationFile(projectId)
    }

    def createConfigurationFile(String projectId) throws ProjectDoesNotExistException{
        def conf = new File(getProjectDir(projectId), configurationFilename)
        conf.createNewFile()
        conf.append("readonly=false\n");
    }

    def isReadOnly(projectId){
        def conf=getConfigurationFile(projectId)
        return conf["readonly"].toBoolean();
    }
    
    def deleteProject(String projectId) throws ProjectDoesNotExistException {
        log.info "Deleting project ${projectId}"
        FileUtils.forceDelete(getProjectDir(projectId))
        modelCache.remove(projectId)
    }
    
    def renameProject(String oldProjectId, String newProjectId) throws ProjectDoesNotExistException {
        log.info "Renaming project ${oldProjectId} to ${newProjectId}"
        FileUtils.moveDirectory(getProjectDir(oldProjectId), getProjectDir(newProjectId, true))
    }
    
    def boolean isCacheValid(String projectId) throws ProjectDoesNotExistException {
        if (getOutputCache(projectId).exists() == false || getInputDir(projectId).list().length == 0) {
            return false
        } else {
            return getInputDir(projectId).listFiles().every { FileUtils.isFileOlder(it, getOutputCache(projectId)) } && FileUtils.isFileOlder(getProgramFile(projectId), getOutputCache(projectId))
        }
    }
    
    def getDefaultNamespace(String projectId) {
        new Namespace(ConfigurationHolder.config.grails.serverURL + "/project/" + projectId + "/")
    }
    
    def autogenerateProgram(String projectId, String strategy) throws  Exception // ProjectDoesNotExistException, RunTimeTabelsException, InvalidInputFile 
	{
        def autogenerator
        if (strategy == "SCOVO") {
            autogenerator = new ScovoAutogenerator(getDefaultNamespace(projectId), projectId)
        }else if (strategy == "MAPS") {
                    autogenerator = new MaplabAutogenerator(getDefaultNamespace(projectId), projectId)
                } else {
                    autogenerator = new BasicAutogenerator(getDefaultNamespace(projectId), projectId)
                }
        def program = autogenerator.autogenerateProgram(getDataSource(projectId))
        saveProgram(projectId, program)
    }
    
    def getDataSource(String projectId) throws ProjectDoesNotExistException, InvalidInputFile {
        return new DataAdaptersDelegate(DataAdapter.findAllRecognizedFilesFromDirectory(getInputDir(projectId)), new Some(getInputDir(projectId)))
    }
    
    def getModel(String projectId) throws ProjectDoesNotExistException, RunTimeTabelsException, InvalidInputFile {
        if(modelCache.get(projectId)){
            return modelCache.get(projectId);
        }else{
            log.info "Getting model of ${projectId}"
            Model model
            if (isCacheValid(projectId)) {
                log.info "Returning cached model from ${getOutputCache(projectId)}"
                model = ModelFactory.createDefaultModel()
                model.read(new FileInputStream(getOutputCache(projectId)), null, "RDF/XML")
            } else {
                model=runTransformation(projectId).model
            }
            modelCache.put(projectId,model)
            return model
        }
    }
    
    def getGlobalModel() throws RunTimeTabelsException, InvalidInputFile {
        log.info "Getting global model of all projects"
        Model model = ModelFactory.createDefaultModel()
        listProjects().each { model.add(getModel(it)) }        
        return model
    }
    
    def runTransformation(String projectId) throws ProjectDoesNotExistException, RunTimeTabelsException, InvalidInputFile {
        // invalidate cache
        FileUtils.deleteQuietly(getOutputCache(projectId))
        def dataSource = getDataSource(projectId)
        log.info "And Tabular Cells! Project ${projectId}. Datasource includes these files: ${dataSource.filenames}, and Tabels program: ${getProgramFile(projectId).canonicalPath} (available? ${getProgramFile(projectId).exists()})" 
		def parser = new TabelsParser()
		def autogenerator = new BasicAutogenerator(getDefaultNamespace(projectId), projectId)
        def program = getProgramFile(projectId).exists() ? parser.parseProgram(getProgramFile(projectId)) : autogenerator.autogenerateProgram(dataSource)
		def interpreter = new Interpreter()
		def dataOutput = new JenaDataOutput(program.prefixesAsMap())

	    // add local RDF and OWL files
	    FileUtils.listFiles(getInputDir(projectId), ["owl", "rdf"] as String[], false).each {
	        dataOutput.model.read(new FileInputStream(it), null, "RDF/XML")
	    }
	    FileUtils.listFiles(getInputDir(projectId), ["n3","nt"] as String[], false).each {
	        dataOutput.model.read(new FileInputStream(it), null, "N3")
	    }
		// read turtle
		FileUtils.listFiles(getInputDir(projectId), ["ttl"] as String[], false).each {
			dataOutput.model.read(new FileInputStream(it), null, "TTL")
		}

        // execute the interpreter
		def trace = interpreter.interpret(program, dataSource, dataOutput)
    
		if (getProgramFile(projectId).exists() == false) {
		    saveProgram(projectId, program)
	    }
	    
	    // save cache
	    def os = new FileOutputStream(getOutputCache(projectId))
        dataOutput.model.write(os, "RDF/XML")
        os.close()
        log.info "Saved model cache (${dataOutput.model.size()} triples) to ${getOutputCache(projectId)}"
        
        return [model: dataOutput.model, trace: trace]
    }
    
    def getTrace(String projectId) throws ProjectDoesNotExistException, RunTimeTabelsException, InvalidInputFile {
        return runTransformation(projectId).trace // refresh the model
    }
    
    def getResources(String projectId) throws ProjectDoesNotExistException, RunTimeTabelsException, InvalidInputFile {
		def model = getModel(projectId)
		def subjectsIterator = model.listSubjects()
		def subjects = []
		while(subjectsIterator.hasNext()) {
		    def subject = subjectsIterator.nextResource()
		    def description = [id: subject.toString()]
		    if (subject.getURI() != null) description["uri"] = subject.getURI()
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
    
    def getGeopoints(String projectId) throws ProjectDoesNotExistException, RunTimeTabelsException, InvalidInputFile {
        def queryString = """
        PREFIX rdfs: <http://www.w3.org/2000/01/rdf-schema#>
        PREFIX geo: <http://www.w3.org/2003/01/geo/wgs84_pos#>
        SELECT ?uri ?lon ?lat ?label
        WHERE {
            ?uri geo:long ?lon ; geo:lat ?lat .
            OPTIONAL { ?uri rdfs:label ?label }
        }
        """
        def queryExecution = QueryExecutionFactory.create(queryString, getModel(projectId))
        queryExecution.execSelect().collect {
            [id: Math.abs(it.getResource("uri").URI.hashCode()),
             lat: it.getLiteral("lat").getDouble(),
             lon: it.getLiteral("lon").getDouble(),
             label: it.getLiteral("label")
            ]
        }
    }

    def hasStatisticalData(projectId){
        def queryString = """
        PREFIX rdf: <http://www.w3.org/1999/02/22-rdf-syntax-ns#> 
        PREFIX dq: <http://purl.org/linked-data/cube#> 
        SELECT ?uri 
        WHERE {
            ?uri rdf:type qb:DataSet.
        }
        """
        def queryExecution = QueryExecutionFactory.create(queryString, getModel(projectId))
        return queryExecution.execSelect().collect{[uri: it.getResource("uri").URI]}.size() != 0
    }

    def hasGeographicalData(projectId){
        def queryString = """
        PREFIX rdf: <http://www.w3.org/1999/02/22-rdf-syntax-ns#> 
        PREFIX skos: <http://www.w3.org/2004/02/skos/core#> 
        SELECT ?uri 
        WHERE {
            ?uri rdf:type skos:Concept.
        }
        """
        def queryExecution = QueryExecutionFactory.create(queryString, getModel(projectId))
        return queryExecution.execSelect().collect{[uri: it.getResource("uri").URI]}.size() != 0
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
    
    String getProgram(String projectId) throws ProjectDoesNotExistException {
        return getProgramFile(projectId).exists() ? getProgramFile(projectId).getText() : ""
    }
    
    private def saveProgram(String projectId, S program) throws ProjectDoesNotExistException {
	    int indent = 0
	    def prettyPrinter = new PrettyPrint(indent)
	    program.accept(prettyPrinter)
	    log.info "Writing the following program to the file ${getProgramFile(projectId)}:\n${prettyPrinter.toString()}"
	    getProgramFile(projectId).setText(prettyPrinter.toString())
    }

    def saveProgram(String projectId, String newProgram) throws ProjectDoesNotExistException, ParseException, CompileTimeTabelsException {
	    log.info "Writing the following program to the file ${getProgramFile(projectId)}:\n${newProgram}"
        modelCache.remove(projectId)
        def parser = new TabelsParser()
        def program = parser.parseProgram(newProgram) // validates the program
        getProgramFile(projectId).setText(newProgram)
        log.info "The new Tabels program has been successfully saved to ${getProgramFile(projectId)}"
    }
    
}

class ProjectDoesNotExistException extends Exception {
    
    String projectId
    
    ProjectDoesNotExistException(String projectId) {
        super("Project does not exist: ${projectId}")
        this.projectId = projectId
    }
    
}

class FileTooLargeException extends Exception {
		String fileName
		double fileSize
		double maxSize
		
		FileTooLargeException(String fileName, double fileSize, double maxSize) {
			super("File size too large. File: ${fileName} was: ${fileSize} bytes but max allowed size is ${maxSize}")
			this.fileName = fileName
			this.fileSize = fileSize
			this.maxSize = maxSize
		}
}

class FileFormatNotAllowedException extends Exception {
	String fileName
	String allowedExtensions
	
	FileFormatNotAllowedException(String fileName, String allowedExtensions) {
		super("File extension not allowed. File: ${fileName} extension not in ${allowedExtensions}")
		this.fileName = fileName
		this.allowedExtensions = allowedExtensions
	}
}
			
