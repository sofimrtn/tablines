package es.ctic.tabels

import es.ctic.tabels.Dimension._
import scala.collection.mutable.ListBuffer
import scala.collection.immutable.HashMap

case class EvaluationContext (dimensionMap : Map[Dimension, String] = new HashMap(), bindings: Bindings = Bindings()) {

	def dimensions : Set[Dimension] = dimensionMap.keySet
	
	def getValue(dimension : Dimension) : String = {
	  if(dimensionMap.contains(dimension))
	    dimensionMap.get(dimension).get // throws exception if unbound
	  else "0"
	}
	
	// lazy because some of the dimensions may not exist yet
	lazy val cursor : Point = Point(getValue(Dimension.files),getValue(Dimension.sheets),getValue(Dimension.cols).toInt, getValue(Dimension.rows).toInt)
	
	def addBinding(variable : Variable, value : RDFNode, point: Point) : EvaluationContext =
	EvaluationContext(dimensionMap, bindings.addBinding(variable,value, point))
		
	
	def addDimension(dimension : Dimension, value : String) : EvaluationContext =
		EvaluationContext(dimensionMap + (dimension -> value), bindings)
		
	def removeDimension(dimension : Dimension) : EvaluationContext =
		EvaluationContext(dimensionMap - (dimension), bindings)


    def getDimensionRange(dimension : Dimension, dataSource : DataSource) : Seq[Any] = {
        lazy val filename = dimensionMap(Dimension.files)
      	lazy val sheet = dimensionMap(Dimension.sheets)
        dimension match {
            case Dimension.files => dataSource.filenames
            case Dimension.sheets => return dataSource.getTabs(filename)
		    case Dimension.rows => return List.range(0, dataSource.getRows(filename,sheet))
		    case Dimension.cols => return List.range(0, dataSource.getCols(filename,sheet))
	    }
	}

}

case class Binding(value : RDFNode, point: Point)

case class Bindings(bindingsMap : Map[Variable, Binding] = new HashMap()) {

  def variables : Set[Variable] = bindingsMap.keySet

  def isBound(variable : Variable) : Boolean = bindingsMap.contains(variable)
  
  def getValue(variable : Variable) : RDFNode = 
    
    if(isBound(variable))
    	bindingsMap.get(variable).get.value
    else throw new UnboundVariableException(variable)
   

  def getPoint(variable : Variable) : Point = bindingsMap.get(variable).get.point // throws exception if unbound
  
  def addBinding(variable : Variable, value : RDFNode, point: Point) : Bindings =
	Bindings(bindingsMap + (variable -> Binding(value, point)))
  
  def removeBinding(variable : Variable) : Bindings =
	Bindings(bindingsMap - (variable))
  
  def clear : Bindings = Bindings(Map())

}

