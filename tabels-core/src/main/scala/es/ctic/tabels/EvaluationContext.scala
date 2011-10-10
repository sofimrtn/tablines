package es.ctic.tabels

import es.ctic.tabels.Dimension._
import scala.collection.mutable.ListBuffer
import scala.collection.immutable.HashMap

case class EvaluationContext (dimensionMap : Map[Dimension, String] = new HashMap(), bindings: Bindings = Bindings()) {

	def dimensiones : Set[Dimension] = dimensionMap.keySet
	
	def getValue(dimension : Dimension) : String = {
	  if(dimensionMap.contains(dimension))
	    dimensionMap.get(dimension).get // throws exception if unbound
	  else "0"
	}
	
//	def getReferencePosition : Point
	
	def addBinding(variable : Variable, value : RDFNode, point: Point) : EvaluationContext =
	EvaluationContext(dimensionMap, bindings.addBinding(variable,value, point))
		
	
	def addDimension(dimension : Dimension, value : String) : EvaluationContext =
		EvaluationContext(dimensionMap + (dimension -> value), bindings)
		
	def removeDimension(dimension : Dimension) : EvaluationContext =
		EvaluationContext(dimensionMap - (dimension), bindings)
}

case class Binding(value : RDFNode, point: Point)

case class Bindings(bindingsMap : Map[Variable, Binding] = new HashMap()) {

  def variables : Set[Variable] = bindingsMap.keySet

  def isBound(variable : Variable) : Boolean = bindingsMap.contains(variable)
  
  def getValue(variable : Variable) : RDFNode = bindingsMap.get(variable).get.value // throws exception if unbound

  def getPoint(variable : Variable) : Point = bindingsMap.get(variable).get.point // throws exception if unbound
  
  def addBinding(variable : Variable, value : RDFNode, point: Point) : Bindings =
	Bindings(bindingsMap + (variable -> Binding(value, point)))
  
  def removeBinding(variable : Variable) : Bindings =
	Bindings(bindingsMap - (variable))
  
  def clear : Bindings = Bindings(Map())

}

