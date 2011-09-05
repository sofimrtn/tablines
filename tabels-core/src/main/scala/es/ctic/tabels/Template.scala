package es.ctic.tabels

case class Statement

case class StatementList

case class Template(eventList : List[Event]) {
  	
	def instantiate()/*:StatemenList*/ = {
	
	  eventList.foreach(event => event.bindingsList.bindingsList.foreach(binding => print(binding.label)))
	}
}