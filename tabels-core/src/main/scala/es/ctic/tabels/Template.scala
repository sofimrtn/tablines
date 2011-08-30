package es.ctic.tabels

abstract class Template {
  
	type RDFGraph
	
	def instantiate : RDFGraph
}