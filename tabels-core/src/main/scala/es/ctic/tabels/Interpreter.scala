package es.ctic.tabels

case class Interpreter {
  
  def interpret(root : S, dataSource: DataSource,dataOut : DataOutput) = {
    
    var evContext = new EvaluationContext()
    evContext.eventList :::= root.grammarEvaluation(evContext, dataSource)
    val template = new Template(evContext.eventList)
	template.instantiate()  
  }

}

case class Event (bindingList : List[Binding])

case class Binding(label : String  , value : String)

case class EvaluationContext(pointList : List[Point] = List() , var eventList: List[Event] = List())