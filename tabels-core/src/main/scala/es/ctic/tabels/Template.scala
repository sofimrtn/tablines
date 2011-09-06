package es.ctic.tabels

case class Statement(subject: String, property: String, obj:String){

}

case class Template(eventList : List[Event], dataOut: DataOutput) {
  	
	def instantiate()/*:StatemenList*/ = {
	
	  
	  eventList.foreach(event => event.bindingList.foreach(binding => print(binding.label)))
	}
}