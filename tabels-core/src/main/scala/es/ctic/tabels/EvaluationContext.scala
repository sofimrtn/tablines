package es.ctic.tabels

import es.ctic.tabels.Dimension._
import scala.collection.immutable.HashMap

case class EvaluationContext (dimensionMap : Map[Dimension, String] = new HashMap(), bindings: Bindings = Bindings(),wLimit: Option[(Int,Dimension)]=None) {

	val workingArea = new WorkArea
	val windowLimit: Option[(Int,Dimension)] = wLimit
    def dimensions : Set[Dimension] = dimensionMap.keySet
	
	def addWLimit(winLimit : Option[(Int,Dimension)]):EvaluationContext=
	  EvaluationContext(dimensionMap, bindings,winLimit)
	
    
    def getValue(dimension : Dimension) : String = {
	  if(dimensionMap.contains(dimension))
	    dimensionMap.get(dimension).get // throws exception if unbound
	  else "0"
	}
	
	// lazy because some of the dimensions may not exist yet
	lazy val cursor : Point = Point(getValue(Dimension.files),getValue(Dimension.sheets),getValue(Dimension.cols).toInt, getValue(Dimension.rows).toInt)
	
	def addBinding(variable : Variable, value : RDFNode, point: Point) : EvaluationContext =
	EvaluationContext(dimensionMap, bindings.addBinding(variable,value, point),windowLimit)
		
	
	def addDimension(dimension : Dimension, value : String) : EvaluationContext =
		EvaluationContext(dimensionMap + (dimension -> value), bindings,windowLimit)
		
	def removeDimension(dimension : Dimension) : EvaluationContext =
		EvaluationContext(dimensionMap - (dimension), bindings,windowLimit)


    def getDimensionRange(dimension : Dimension, dataSource : DataSource) : Seq[Any] = {
        lazy val filename = dimensionMap(Dimension.files)
      	lazy val sheet = dimensionMap(Dimension.sheets)
        dimension match {
            case Dimension.files => dataSource.filenames
            case Dimension.sheets => return dataSource.getTabs(filename)
		    case Dimension.rows => return List.range(cursor.row, dataSource.getRows(filename,sheet))
		    case Dimension.cols => return List.range(cursor.col, dataSource.getCols(filename,sheet))
	    }
	}

}