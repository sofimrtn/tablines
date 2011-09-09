package es.ctic.tabels
import scala.collection.mutable.ListBuffer

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

case class Event (bindingList : List[Binding])

case class Binding(label : Variable  , value : String)

class EvaluationContext(pointList : List[Point] = List() , var eventList: List[Event] = null){
	var buffList = new ListBuffer[Event]
}