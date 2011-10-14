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
	  dataOutput.generateOutput(new Statement(substitute(s, bindingList),
	                                          substitute(p, bindingList),
	                                          substitute(o, bindingList))) 
	}
	
	private def substitute(x : Either[RDFNode, Variable], bindingList : Bindings) : RDFNode = {
	   x match {
	   	case Left(rdfNode)   => rdfNode
	   	case Right(variable) => bindingList.getValue(variable)
	   }
	}

}

abstract class RDFNode() {
    
	def asBoolean : Literal
	def asString : Literal
	def +(suffix : String) : RDFNode
	
}

case class Literal(value : String, rdfType: Resource = XSD_STRING, langTag : String = "") extends RDFNode {
	
	def truthValue : Boolean = Set("true", "1") contains this.asBoolean.value

	override def +(suffix : String) : Literal = Literal(this.value + suffix)

    /**
     * This method calculates the effective boolean value of the
     * literal by applying the rules of fn:boolean, see
     * http://www.w3.org/TR/rdf-sparql-query/#ebv
     */
	override def asBoolean : Literal = rdfType match {
	    case XSD_BOOLEAN => this
	    case XSD_STRING =>  if (value.length > 0) LITERAL_TRUE else LITERAL_FALSE
	    case XSD_INT | XSD_DOUBLE | XSD_DECIMAL | XSD_FLOAT => if (value.toDouble == 0.0) LITERAL_FALSE else LITERAL_TRUE
	}
	
	override def asString : Literal = Literal(value)
	
}

case class Resource(uri : String) extends RDFNode {
	override def asBoolean : Literal = LITERAL_TRUE
	override def asString : Literal = Literal(uri)
	override def +(suffix : String) : Resource = Resource(this.uri + suffix)
}

object RDF_TYPE extends Resource("http://www.w3.org/1999/02/22-rdf-syntax-ns#type")
object XSD_STRING extends Resource("http://www.w3.org/2001/XMLSchema#string")
object XSD_BOOLEAN extends Resource("http://www.w3.org/2001/XMLSchema#boolean")
object XSD_INT extends Resource("http://www.w3.org/2001/XMLSchema#int")
object XSD_DOUBLE extends Resource("http://www.w3.org/2001/XMLSchema#double")
object XSD_FLOAT extends Resource("http://www.w3.org/2001/XMLSchema#float")
object XSD_DECIMAL extends Resource("http://www.w3.org/2001/XMLSchema#decimal")
object LITERAL_TRUE extends Literal("true", XSD_BOOLEAN)
object LITERAL_FALSE extends Literal("false", XSD_BOOLEAN)
