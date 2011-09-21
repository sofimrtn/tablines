package es.ctic.tabels

import es.ctic.tabels.Dimension._
import scala.collection.immutable.HashMap
import scala.collection.mutable.ListBuffer
import grizzled.slf4j.Logging

class Interpreter extends Logging {
  
  def interpret(root : S, dataSource: DataSource, dataOut : DataOutput) = {
    //FIX IT//
    var visitor = new VisitorEvaluate(dataSource) 
    
    visitor.visit(root)
     
    logger.debug("List of events: " + visitor.events)
   
    // FIXME: do not instantiate ALL templates for EACH event, be selective
    for ( t <- root.templateList ; e <- visitor.events ) {
		logger.debug("Considering instantiation of template " + t + " for event " + e)
		if ( t.variables subsetOf e.bindings.variables ) {
			if ( e.lastBoundVariables subsetOf t.variables ) {
				t.instantiate(e.bindings, dataOut)				
			} else {
				logger.debug("The template " + t + " is not relevant for event " + e)
			}
		} else {
			logger.debug("The template " + t + " cannot be instantiated for event " + e + " because there are unbound variables")
		}
     }
  }

}

case class Event(bindings : Bindings, lastBoundVariables : Set[Variable])

case class Binding(value : String, point: Point)

case class Bindings(bindingsMap : Map[Variable, Binding] = new HashMap()) {

  def variables : Set[Variable] = bindingsMap.keySet

  def isBound(variable : Variable) : Boolean = bindingsMap.contains(variable)
  
  def getValue(variable : Variable) : String = bindingsMap.get(variable).get.value // throws exception if unbound

  def getPoint(variable : Variable) : Point = bindingsMap.get(variable).get.point // throws exception if unbound
  
  def addBinding(variable : Variable, value : String, point: Point) : Bindings =
	Bindings(bindingsMap + (variable -> Binding(value, point)))
  
  def removeBinding(variable : Variable) : Bindings =
	Bindings(bindingsMap - (variable))
  
  def clear : Bindings = Bindings(Map())

}

case class EvaluationContext (dimensionMap : Map[Dimension, String] = new HashMap()) {

	def dimensiones : Set[Dimension] = dimensionMap.keySet
	
	def getValue(dimension : Dimension) : String = {
	  if(dimensionMap.contains(dimension))
	    dimensionMap.get(dimension).get // throws exception if unbound
	  else "0"
	}
	
	def addDimension(dimension : Dimension, value : String) : EvaluationContext =
		EvaluationContext(dimensionMap + (dimension -> value))
		
	def removeDimension(dimension : Dimension) : EvaluationContext =
		EvaluationContext(dimensionMap - (dimension))


	
}