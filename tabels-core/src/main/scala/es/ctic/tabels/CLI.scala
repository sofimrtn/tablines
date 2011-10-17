package es.ctic.tabels

import grizzled.slf4j.Logging
import java.io.File

object CLI extends Logging {
	
	lazy val excelFilesCurrentDirectory : Seq[File] = new File(".").listFiles()filter(f => """.*\.xls$""".r.findFirstIn(f.getName).isDefined)
  
	def main(args: Array[String]) {
		try {
		  	logger.info("And... Tabular Bells!")
			val files : Seq[File] = if (args.isEmpty) excelFilesCurrentDirectory else args.map(new File(_))
			val dataSource : DataSource = new ExcelDataSource(files)
			logger.debug("Found these input files: " + dataSource.filenames)
		
			logger.debug("Parsing Tabels program")
			val parser = new TabelsParser()

            val programFile = new File("transform.tabels")
            val program = parser.parseProgram(programFile)
		  	
			logger.debug("Interpreting AST: " + program)
			val interpreter : Interpreter = new Interpreter()
			val dataOutput : JenaDataOutput = new JenaDataOutput(Map() ++ program.prefixes)
			interpreter.interpret(program, dataSource, dataOutput)

			logger.debug("Writing output (" + dataOutput.model.size + " triples)")
			dataOutput.model.write(System.out, "RDF/XML")
		} catch {
		    case e : ParseException =>
		      System.err.println(e.getMessage)
		      System.err.println(e.line)
		      System.err.println(" " * (e.column-1) + "^^")
		    case e : TabelsException =>
		      logger.error("User error", e)
		      System.err.println(e.getMessage)
			case e : Exception => logger.error("Internal error", e)
		}
	}

}