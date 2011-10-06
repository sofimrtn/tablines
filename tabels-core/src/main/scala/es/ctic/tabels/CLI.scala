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
			    prefix steel: <http://ontorule-project.eu/resources/steel#>
				For ?idCoil in rows	
					let @tuple[?idCoil,?codeOutput,?steelGrade,?productType,?minWidth,?maxWidth,?minThick,?maxThick,?minZincThick,?maxZincThick,?minWeight,?maxWeight, ?targetElongation, ?minElongation, ?maxElongation, ?minYieldStr, ?maxYieldStr, ?minEndTemp, ?maxEndTemp ] as horizontal
			          let ?idCoilAsResource = RESOURCE(?idCoil, <http://ontorule-project.eu/resources/steeldata#coil>)
						
						let ?orderAsResource = RESOURCE(?idCoil, <http://ontorule-project.eu/resources/steeldata#order>)
							let ?steelGradeAsResource = RESOURCE(?steelGrade, <http://ontorule-project.eu/resources/steel#>)
								let ?asignacion = ?codeOutput
									let ?prueba in cell is located left placed with ?steelGrade
							
				{ 
			      steel:steelGrade <http://ontorule-project.eu/resources/steel#steelGrade> ?prueba.
			    
				  ?idCoilAsResource a steel:Coil ;
				  steel:order ?orderAsResource ;
			      <http://ontorule-project.eu/resources/steel#steelGrade> ?steelGradeAsResource ;
			      <http://ontorule-project.eu/resources/steel#codeOutput> ?codeOutput ;
			      <http://ontorule-project.eu/resources/steel#identifier> ?idCoil.
			      
				  ?orderAsResource <http://www.w3.org/1999/02/22-rdf-syntax-ns#type> <http://ontorule-project.eu/resources/steel#Order>;
				  <http://ontorule-project.eu/resources/steel#widthMin> ?minWidth ;
			      <http://ontorule-project.eu/resources/steel#widthMax> ?maxWidth ;
			      <http://ontorule-project.eu/resources/steel#originalCoilThicknessMin> ?minThick ;
			      <http://ontorule-project.eu/resources/steel#originalCoilThicknessMax> ?maxThick ;
			      <http://ontorule-project.eu/resources/steel#zincThicknessMin> ?minZincThick ;
			      <http://ontorule-project.eu/resources/steel#zincThicknessMax> ?maxZincThick ;
			      <http://ontorule-project.eu/resources/steel#weightMin> ?minWeight ;
			      <http://ontorule-project.eu/resources/steel#weightMax> ?maxWeight ;
			      <http://ontorule-project.eu/resources/steel#skinPassElongationMin> ?minElongation ;
			      <http://ontorule-project.eu/resources/steel#skinPassElongationMax> ?maxElongation ;
			      <http://ontorule-project.eu/resources/steel#yieldStrengthMin> ?minYieldStr ;
			      <http://ontorule-project.eu/resources/steel#yieldStrengthMax> ?maxYieldStr ;
			      <http://ontorule-project.eu/resources/steel#temperatureMin> ?minEndTemp ;
			      <http://ontorule-project.eu/resources/steel#temperatureMax> ?maxEndTemp 
				  
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