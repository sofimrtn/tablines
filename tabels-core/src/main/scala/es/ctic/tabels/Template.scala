package es.ctic.tabels

case class Statement(subject: String, property: String, obj:String){

}

case class Template(triples : List[TripleTemplate]) {
  	
	def instantiate(bindingList : List[Binding], dataOut: DataOutput) = {
	
	 triples.foreach(t => t.instantiate(bindingList, dataOut)) 
	}
	  					
}

case class TripleTemplate(s : Any, p : Any, o : Any) {
	
	def instantiate(bindingList : List[Binding], dataOut: DataOutput) = {
	  
	  dataOut.generateOutput(new Statement(s.toString,p.toString, o.toString)) 
	  
	}

}