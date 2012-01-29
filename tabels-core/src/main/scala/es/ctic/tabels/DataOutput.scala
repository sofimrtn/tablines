package es.ctic.tabels


abstract class DataOutput  {

	def generateOutput(statement : Statement)
	
	def postProcess(program : S)
	
}