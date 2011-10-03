package es.ctic.tabels

import grizzled.slf4j.Logging

//FIX IT: literals can not be properties
case class Statement(subject: RDFNode, property: RDFNode, obj:RDFNode){

}

case class Template(triples : Set[TripleTemplate] = Set()) extends Logging {
	
	val variables : Set[Variable] = triples flatMap (_.variables)
  	
	def instantiate(bindingList : Bindings, dataOut: DataOutput) = {
		logger.info("Instantiating template " + this + " with bindings " + bindingList)
	 	triples.foreach(t => t.instantiate(bindingList, dataOut)) 
	}
	  					
}

case class TripleTemplate(s : Either[RDFNode, Variable], p : Either[RDFNode, Variable], o : Either[RDFNode, Variable]) {
	
	val variables : Set[Variable] = Set(s,p,o) flatMap {
		case Left(_) => None
		case Right(variable) => Some(variable)
	}
	
	def instantiate(bindingList : Bindings, dataOutput: DataOutput) = {
	  
	  dataOutput.generateOutput(new Statement(substitute(s, bindingList) ,substitute(p, bindingList), substitute(o, bindingList))) 
	  
	}
	
	def substitute(x : Either[RDFNode, Variable], bindingList : Bindings) : RDFNode = {
	  
	   x match {
	   	case Left(rdfNode) => rdfNode
	   	//FIX ME: variables can also be literals
	   	case Right(variable) => bindingList.getValue(variable)
	   }
	}

}

abstract class RDFNode()

case class Literal(value : String) extends RDFNode

case class Resource(uri : String) extends RDFNode

