package es.ctic.tabels

import scala.collection.JavaConversions$

class ProjectController {

    def index = { }
    
    def rdf = {
        def workDir = "/tmp/tabels/"
        def tabelsFilename = "default.tabels"
        def spreadsheetFiles = new File(workDir).listFiles().collect { it.name =~ ".*xls\$" }
        def dataSource = new ExcelDataSource(spreadsheetFiles)
		def parser = new TabelsParser()
        def programFile = new File(workDir + tabelsFilename)
        def program = (programFile.exists()) ? parser.parseProgram(programFile) : Autogenerator$.autogenerateProgram(dataSource)
		def interpreter = new Interpreter()
		def dataOutput = new JenaDataOutput(/* Map() ++ program.prefixes */)
		interpreter.interpret(program, dataSource, dataOutput)
		def os = System.out
        dataOutput.model.write(os, "RDF/XML")
    }
    
}
