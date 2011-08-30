package es.ctic.tabels

abstract class Interpreter {
  
  type RDFgraph
  
  def interpret(root : S, dataSource: DataSource) : RDFgraph 
  

}