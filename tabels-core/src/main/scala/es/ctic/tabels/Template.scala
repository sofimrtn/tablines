package es.ctic.tabels


case class RDFGraph

abstract class Template {
  	
	def instantiate : RDFGraph
}