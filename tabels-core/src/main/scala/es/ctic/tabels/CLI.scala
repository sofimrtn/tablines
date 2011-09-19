package es.ctic.tabels

import grizzled.slf4j.Logging
import java.io.File

object CLI extends Logging {
	
	lazy val filesCurrentDirectory : Seq[File] = new File(".").listFiles()filter(f => """.*\.xls$""".r.findFirstIn(f.getName).isDefined)
  
	def main(args: Array[String]) {
		try {
		  	logger.info("And... Tabular Bells!")
			val files : Seq[File] = if (args.isEmpty) filesCurrentDirectory else args.map(new File(_))
			val dataSource : DataSource = new ExcelDataSource(files)
			logger.debug("Found these input files: " + dataSource.filenames)
			val dataOutput : JenaDataOutput = new JenaDataOutput()
			val interpreter : Interpreter = new Interpreter()
		
			logger.debug("Parsing Tabels program")
			val parser = new TabelsParser()
			val program : S = parser.parseProgram("""
				For ?x in rows 
					?y in cell A1 
					?v in cell A5
				{ ?x <http://http://www.w3.org/1999/02/22-rdf-syntax-ns#type> <http://http://www.w3.org/1999/02/22-rdf-syntax-ns#person> .
				  ?x <http://http://www.w3.org/1999/02/22-rdf-syntax-ns#related> ?y .
				  ?x <http://http://www.w3.org/1999/02/22-rdf-syntax-ns#nonRelated> ?v .	}
				""") // FIXME

			logger.debug("Interpreting AST: " + program)
			interpreter.interpret(program, dataSource, dataOutput)

			logger.debug("Writing output (" + dataOutput.model.size + " triples)")
			dataOutput.model.write(System.out, "N3")
		} catch {
			case e : Exception => logger.error("While running Tabels", e)
		}
	}

}