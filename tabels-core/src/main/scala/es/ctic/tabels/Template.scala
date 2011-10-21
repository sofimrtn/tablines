package es.ctic.tabels

import grizzled.slf4j.Logging

case class Template(triples : Set[TripleTemplate] = Set()) extends Logging {
    
    override def toString() = toAbbrString(Seq())
    
    def toAbbrString(prefixes : Seq[(String,Resource)]) =
        "{\n" + (triples map ("    " + _.toAbbrString(prefixes)) mkString " .\n") + "\n}\n"
	
	val variables : Set[Variable] = triples flatMap (_.variables)
  	
	def instantiate(bindingList : Bindings, dataOut: DataOutput) = {
		logger.debug("Instantiating template " + this + " with bindings " + bindingList)
	 	triples.foreach(t => t.instantiate(bindingList, dataOut)) 
	}
	  					
}

case class TripleTemplate(s : Either[RDFNode, Variable], p : Either[RDFNode, Variable], o : Either[RDFNode, Variable]) {
    
    override def toString() = toAbbrString(Seq())
    
    def toAbbrString(prefixes : Seq[(String,Resource)]) : String =
        rdfNodeOrVariableToString(s, prefixes) + " " +
        rdfNodeOrVariableToString(p, prefixes) + " " +
        rdfNodeOrVariableToString(o, prefixes)
        
    def rdfNodeOrVariableToString(rdfNodeOrVariable : Either[RDFNode, Variable],
                                  prefixes : Seq[(String,Resource)]) : String =
        rdfNodeOrVariable match {
            case Left(resource : Resource) => resource.toAbbrString(prefixes)
            case Left(literal : Literal) => literal.toString
            case Right(variable) => variable.toString
        }
        
	
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
