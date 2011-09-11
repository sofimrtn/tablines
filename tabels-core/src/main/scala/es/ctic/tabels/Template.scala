package es.ctic.tabels

//FIX IT: literals can not be properties
case class Statement(subject: RDFNode, property: RDFNode, obj:RDFNode){

}

case class Template(triples : Seq[TripleTemplate] = List()) {
  	
	def instantiate(bindingList : Bindings, dataOut: DataOutput) = {
	
	 triples.foreach(t => t.instantiate(bindingList, dataOut)) 
	}
	  					
}

case class TripleTemplate(s : Either[RDFNode, Variable], p : Either[RDFNode, Variable], o : Either[RDFNode, Variable]) {
	
	def instantiate(bindingList : Bindings, dataOut: DataOutput) = {
	  
	  dataOut.generateOutput(new Statement(substitute(s, bindingList) ,substitute(p, bindingList), substitute(o, bindingList))) 
	  
	}
	
	def substitute(x : Either[RDFNode, Variable], bindingList : Bindings) : RDFNode = {
	  
	   x match {
	   	case Left(rdfNode) => rdfNode
	   	//FIX ME: variables can also be literals
	   	case Right(variable) => Resource(bindingList.getBinding(variable))
	   }
	}

}

abstract class RDFNode()

case class Literal(value : String) extends RDFNode

case class Resource(uri : String) extends RDFNode

