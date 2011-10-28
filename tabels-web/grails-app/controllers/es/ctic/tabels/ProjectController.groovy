package es.ctic.tabels

class ProjectController {

    def index = { }
    
    def rdf = {
        def workDir = "/tmp/tabels/uploadr"
        def tabelsFilename = "default.tabels"
        def dataSource = ExcelDataSource.loadAllExcelFilesFromDirectory(new File(workDir))
		def parser = new TabelsParser()
        def programFile = new File(workDir + tabelsFilename)
        def program = (programFile.exists()) ? parser.parseProgram(programFile) : Autogenerator.autogenerateProgram(dataSource)
		def interpreter = new Interpreter()
		def dataOutput = new JenaDataOutput(/* Map() ++ program.prefixes */)
		interpreter.interpret(program, dataSource, dataOutput)
		def os = System.out
        dataOutput.model.write(os, "RDF/XML")
    }
    
}
