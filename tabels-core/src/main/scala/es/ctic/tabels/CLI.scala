package es.ctic.tabels

import grizzled.slf4j.Logging
import java.io.File
import org.apache.commons.cli.{Options,CommandLineParser,PosixParser,CommandLine,HelpFormatter}
import scala.collection.JavaConversions

object CLI extends Logging {

	val defaultTabelsFilename = "transform.tabels"
	lazy val excelFilesCurrentDirectory : Seq[File] = new File(".").listFiles()filter(f => """.*\.xls$""".r.findFirstIn(f.getName).isDefined)
  
	def main(args: Array[String]) {
	    val options = new Options()
	    options.addOption("t", true, "path to the Tabels program")
	    val cliParser = new PosixParser();
		try {
            val cmd : CommandLine = cliParser.parse(options, args)

            val tabelsFilename = if (cmd hasOption "t") cmd.getOptionValue("t") else defaultTabelsFilename
			logger.debug("Parsing Tabels program")
			val parser = new TabelsParser()
            val programFile = new File(tabelsFilename)
            val program = parser.parseProgram(programFile)		  	

			val spreadsheetFiles : Seq[File] = if (cmd.getArgs isEmpty) excelFilesCurrentDirectory else cmd.getArgs.map(new File(_))
			val dataSource : DataSource = new ExcelDataSource(spreadsheetFiles)
			logger.debug("Processing these input files: " + dataSource.filenames)
		
		    val visitorToString = new VisitorToString()
		    program.accept(visitorToString)
			logger.debug("Interpreting AST: " + visitorToString.toString)
			val interpreter : Interpreter = new Interpreter()
			val dataOutput : JenaDataOutput = new JenaDataOutput(Map() ++ program.prefixes)
			interpreter.interpret(program, dataSource, dataOutput)

			logger.debug("Writing output (" + dataOutput.model.size + " triples)")
			dataOutput.model.write(System.out, "RDF/XML")
		} catch {
		    case e : org.apache.commons.cli.ParseException =>
		      System.err.println(e.getMessage)
              new HelpFormatter().printHelp("tabels [OPTIONS] [SPREADSHEET FILES]", options );
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