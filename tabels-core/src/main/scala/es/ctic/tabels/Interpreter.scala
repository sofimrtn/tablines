package es.ctic.tabels

abstract class Interpreter {
  
  def interpret(root : S, dataSource: DataSource,dataOut : DataOutput) = {
    
    var evContext = new EvaluationContext()

    root.grammarEvaluation(evContext, dataSource)
	  
  }

}

case class Event (bindingsList : BindingList)

case class Binding(label : String  , value : CellValue)

case class BindingList(bindingsList : List[Binding] = List())

case class EvaluationContext(pointList : List[Point] = List() , eventList: List[Event] = List())