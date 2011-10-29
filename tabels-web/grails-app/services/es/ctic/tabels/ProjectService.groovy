package es.ctic.tabels

class ProjectService {

    static transactional = true

    File workDir = new File("/tmp/tabels/uploadr/")

    def getModel() {
        log.info "And Tabular Cells!"
        def tabelsFilename = "default.tabels"
        def dataSource = ExcelDataSource.loadAllExcelFilesFromDirectory(workDir)
        log.debug "Datasource includes these files: ${dataSource.filenames}"
		def parser = new TabelsParser()
        def programFile = new File(workDir, tabelsFilename)
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
	    
	    return dataOutput.model
    }
}
