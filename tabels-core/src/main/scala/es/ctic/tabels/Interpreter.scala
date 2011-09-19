package es.ctic.tabels
import scala.collection.mutable.ListBuffer
import scala.collection.mutable.HashMap
import grizzled.slf4j.Logging

class Interpreter extends Logging {
  
  def interpret(root : S, dataSource: DataSource, dataOut : DataOutput) = {
    //FIX IT//
    var visitor = new VisitorEvaluate(dataSource) 
    
    visitor.visit(root)
     
    logger.debug("List of events: " + visitor.events)
   
    // FIXME: do not instantiate ALL templates for EACH event, be selective
    for ( t <- root.templateList ; e <- visitor.events ) t.instantiate(e.bindings, dataOut)
      
  }

}

case class Event(bindings : Bindings, lastBoundVariables : Seq[Variable])

class Bindings {
 
  case class Binding(value : String, point: Point)

  var bindings = new HashMap[Variable, Binding]

  def isBound(variable : Variable) : Boolean = bindings.contains(variable)
  
  def getValue(variable : Variable) : String = bindings.get(variable).get.value // throws exception if unbound

  def getPoint(variable : Variable) : Point = bindings.get(variable).get.point // throws exception if unbound
  
  def addBinding(variable : Variable, value : String, point: Point) = bindings.put(variable, Binding(value, point))
  
}

class EvaluationContext(pointList : List[Point] = List() , var eventList: List[Event] = null){
	var buffList = new ListBuffer[Event]
}