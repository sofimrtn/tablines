package es.ctic.tabels
import scala.collection.mutable.ListBuffer

case class Interpreter {
  
  def interpret(root : S, dataSource: DataSource, dataOut : DataOutput) = {
    
    //FIX IT//
    var evContext = new EvaluationContext()
    evContext.buffList += root.grammarEvaluation(evContext, dataSource)
    evContext.eventList= evContext.buffList.toList 
   
    val template = new Template(evContext.eventList, dataOut)
	template.instantiate()  
  }

}

case class Event (bindingList : List[Binding])

case class Binding(label : String  , value : String)

class EvaluationContext(pointList : List[Point] = List() , var eventList: List[Event] = null){
	var buffList = new ListBuffer[Event]
}