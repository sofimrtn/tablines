package es.ctic.tabels

abstract class Interpreter {
  
  type RDFgraph
  
  def interpret(root : S, dataSource: DataSource) : RDFgraph 

}

case class Event (bindingsList : BindingList)

case class Binding(label : String  , value : CellValue)

case class BindingList(bindingsList : List[Binding] = List()) 