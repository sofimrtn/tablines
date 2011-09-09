package es.ctic.tabels

//FIX IT: literals can not be properties
case class Statement(subject: RDFNode, property: RDFNode, obj:RDFNode){

}

case class Template(triples : List[TripleTemplate]) {
  	
	def instantiate(bindingList : List[Binding], dataOut: DataOutput) = {
	
	 triples.foreach(t => t.instantiate(bindingList, dataOut)) 
	}
	  					
}

case class TripleTemplate(s : Either[RDFNode, Variable], p : Either[RDFNode, Variable], o : Either[RDFNode, Variable]) {
	
	def instantiate(bindingList : List[Binding], dataOut: DataOutput) = {
	  
	  dataOut.generateOutput(new Statement(substitute(s, bindingList) ,substitute(p, bindingList), substitute(o, bindingList))) 
	  
	}
	
	def substitute(x : Either[RDFNode, Variable], bindingList : List[Binding]) : RDFNode = {
	  
	   x match {
	   	case Left(rdfNode) => rdfNode
	   	case Right(variable) => Resource("hola")
	   }
	}

}

abstract class RDFNode()

case class Literal(value : String) extends RDFNode

case class Resource(uri : String) extends RDFNode

