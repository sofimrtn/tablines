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
			
				For ?idCoil in rows	
					let @tuple[?idCoil,?codeOutput,?steelGrade,?productType,?minWidth,?maxWidth,?minThick,?maxThick,?minZincThick,?maxZincThick,?minWeight,?maxWeight, ?targetElongation, ?minElongation, ?maxElongation, ?minYieldStr, ?maxYieldStr, ?minEndTemp, ?maxEndTemp ] as horizontal
			          let ?idCoilAsResource = RESOURCE(?idCoil, http://ontorule-project.eu/resources/steeldata#coil)
						let ?orderAsResource = RESOURCE(?idCoil, http://ontorule-project.eu/resources/steeldata#order)
							let ?steelGradeAsResource = RESOURCE(?steelGrade, http://ontorule-project.eu/resources/steel#)
							
				{ ?idCoilAsResource <http://www.w3.org/1999/02/22-rdf-syntax-ns#type> <http://ontorule-project.eu/resources/steel#Coil> .
				  ?idCoilAsResource <http://ontorule-project.eu/resources/steel#order> ?orderAsResource .
			      ?idCoilAsResource <http://ontorule-project.eu/resources/steel#steelGrade> ?steelGradeAsResource .
			      ?idCoilAsResource <http://ontorule-project.eu/resources/steel#codeOutput> ?codeOutput .
			      ?idCoilAsResource <http://ontorule-project.eu/resources/steel#identifier> ?idCoil.
			      
				  ?orderAsResource <http://www.w3.org/1999/02/22-rdf-syntax-ns#type> <http://ontorule-project.eu/resources/steel#Order>.
				  ?orderAsResource <http://ontorule-project.eu/resources/steel#widthMin> ?minWidth .
			      ?orderAsResource <http://ontorule-project.eu/resources/steel#widthMax> ?maxWidth .
			      ?orderAsResource <http://ontorule-project.eu/resources/steel#originalCoilThicknessMin> ?minThick .
			      ?orderAsResource <http://ontorule-project.eu/resources/steel#originalCoilThicknessMax> ?maxThick.
			      ?orderAsResource <http://ontorule-project.eu/resources/steel#zincThicknessMin> ?minZincThick .
			      ?orderAsResource <http://ontorule-project.eu/resources/steel#zincThicknessMax> ?maxZincThick .
			      ?orderAsResource <http://ontorule-project.eu/resources/steel#weightMin> ?minWeight .
			      ?orderAsResource <http://ontorule-project.eu/resources/steel#weightMax> ?maxWeight .
			      ?orderAsResource <http://ontorule-project.eu/resources/steel#skinPassElongationMin> ?minElongation .
			      ?orderAsResource <http://ontorule-project.eu/resources/steel#skinPassElongationMax> ?maxElongation .
			      ?orderAsResource <http://ontorule-project.eu/resources/steel#yieldStrengthMin> ?minYieldStr .
			      ?orderAsResource <http://ontorule-project.eu/resources/steel#yieldStrengthMax> ?maxYieldStr .
			      ?orderAsResource <http://ontorule-project.eu/resources/steel#temperatureMin> ?minEndTemp .
			      ?orderAsResource <http://ontorule-project.eu/resources/steel#temperatureMax> ?maxEndTemp .
				  
			    }
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