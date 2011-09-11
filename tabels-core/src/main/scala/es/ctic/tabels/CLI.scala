package es.ctic.tabels

import grizzled.slf4j.Logging

object CLI extends Logging {
  
	def main(args: Array[String]) {
	  	logger.info("And... Tabular Bells!")
		val parser = new TabelsParser()
		val dataSource : DataSource = new ExcelDataSource(args)
		val dataOutput : JenaDataOutput = new JenaDataOutput()
		val interpreter : Interpreter = new Interpreter()
		val program : S = parser.parseProgram("?x in cell A1 { ?x <http://example.org/> <http://foo.org/> . }") // FIXME
		interpreter.interpret(program, dataSource, dataOutput)
		dataOutput.model.write(System.out, "N3")
	}

}