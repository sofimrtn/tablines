package es.ctic.tabels

case class Statement(subject: String, property: String, obj:String){

}

case class Template(eventList : List[Event], dataOut: DataOutput) {
  	
	def instantiate() = {
	
	  
	  eventList.foreach(event => event.bindingList.foreach(binding =>
	    							{
	    							  dataOut.generateOutput(new Statement(binding.label,"http://example/friends", binding.value)) 
	    							  println(dataOut.asInstanceOf[JenaDataOutput].model)
	    							 }
	    							)
	    				)
	}
	  					
}