package es.ctic.tabels
import scala.collection.mutable.ListBuffer
import scala.collection.mutable.HashMap

case class Interpreter {
  
  def interpret(root : S, dataSource: DataSource, dataOut : DataOutput) = {
    //FIX IT//
    var visitor = new VisitorEvaluate(dataSource) 
    
    visitor.visit(root)
     
    println(visitor.events)
   
    // FIXME: do not instantiate ALL templates for EACH event, be selective
    for ( t <- root.templateList ; e <- visitor.events ) t.instantiate(e.bindingList, dataOut)
      
  }

}

case class Event (bindingList : Bindings)

case class Binding(label : Variable  , value : String)

class Bindings(){
 
  var bindings = new HashMap[Variable, String]
  
  def getBinding(variable : Variable) : String = bindings.getOrElse(variable,"FIX ME")
  
  def addBinding(variable : Variable, value : String) = bindings.put(variable, value)
  
}

class EvaluationContext(pointList : List[Point] = List() , var eventList: List[Event] = null){
	var buffList = new ListBuffer[Event]
}