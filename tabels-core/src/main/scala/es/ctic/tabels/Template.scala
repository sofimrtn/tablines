package es.ctic.tabels

import grizzled.slf4j.Logging

case class Template(triples : Seq[TripleTemplate] = Seq()) extends Logging {
    
    override def toString() = toAbbrString(Seq())
    
    def toAbbrString(prefixes : Seq[(String,NamedResource)]) =
        "{\n" + (triples map ("    " + _.toAbbrString(prefixes)) mkString " .\n") + "\n}\n"
	
	val variables : Set[Variable] = triples.toSet[TripleTemplate] flatMap (_.variables)
	
	val blankNodes : Set[BlankNode] = triples.toSet[TripleTemplate] flatMap (_.blankNodes)
	
	def blankNodeRenamingSubstitution(seed : Int) : Map[BlankNode, BlankNode] =
	    blankNodes map { bn => (bn, BlankNode(Left("__" + bn.id + "_" + seed))) } toMap
  	
	def instantiate(bindingList : Bindings, dataOut: DataOutput, seed : Option[Int] = None) {
		logger.debug("Instantiating template " + this + " with seed " + seed)
		val substitution = seed match {
		    case Some(someSeed) => blankNodeRenamingSubstitution(someSeed)
		    case None => Map[BlankNode,BlankNode]()
	    }
	    logger.debug("Blank node renaming substitution: " + substitution)
	 	triples.foreach(t => t.substitute(substitution).instantiate(bindingList, dataOut)) 
	}
	  					
}

case class TripleTemplate(s : Either[RDFNode, Variable], p : Either[RDFNode, Variable], o : Either[RDFNode, Variable]) {
    
    override def toString() = toAbbrString(Seq())
    
    def toAbbrString(prefixes : Seq[(String,NamedResource)]) : String =
        rdfNodeOrVariableToString(s, prefixes) + " " +
        rdfNodeOrVariableToString(p, prefixes) + " " +
        rdfNodeOrVariableToString(o, prefixes)
        
    def rdfNodeOrVariableToString(rdfNodeOrVariable : Either[RDFNode, Variable],
                                  prefixes : Seq[(String,NamedResource)]) : String =
        rdfNodeOrVariable match {
            case Left(resource : NamedResource) => resource.toAbbrString(prefixes)
            case Left(bnode : BlankNode) => bnode.toString
            case Left(literal : Literal) => literal.toString
            case Right(variable) => variable.toString
        }
        
	
	val variables : Set[Variable] = Set(s,p,o) flatMap {
		case Left(_) => None
		case Right(variable) => Some(variable)
	}
	
	val blankNodes : Set[BlankNode] = Set(s,o) flatMap {
	    case Left(blankNode : BlankNode) => Some(blankNode)
	    case Left(_) => None
	    case Right(_) => None
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
	
	private def substitute(x : Either[RDFNode, Variable], substitution : Map[BlankNode, BlankNode]) : Either[RDFNode, Variable] = {
	    x match {
	        case Left(bn : BlankNode) if substitution.contains(bn) => Left(substitution(bn))
	        case another => another
	    }
	}
	
	def substitute(substitution : Map[BlankNode, BlankNode]) : TripleTemplate =
	    TripleTemplate(substitute(s, substitution), substitute(p, substitution), substitute(o, substitution))

}

object TripleTemplate {
    
    implicit def rdfNodeToTripleAtom(rdfNode : RDFNode) : Either[RDFNode, Variable] = Left(rdfNode)
    implicit def variableToTripleAtom(variable : Variable) : Either[RDFNode, Variable] = Right(variable)
    
    implicit def tuple3AtoTripleTemplate(tuple : Tuple3[RDFNode,  RDFNode,  RDFNode] ) : TripleTemplate = TripleTemplate(tuple._1, tuple._2, tuple._3)
    implicit def tuple3BtoTripleTemplate(tuple : Tuple3[RDFNode,  RDFNode,  Variable]) : TripleTemplate = TripleTemplate(tuple._1, tuple._2, tuple._3)
    implicit def tuple3CtoTripleTemplate(tuple : Tuple3[RDFNode,  Variable, RDFNode] ) : TripleTemplate = TripleTemplate(tuple._1, tuple._2, tuple._3)
    implicit def tuple3DtoTripleTemplate(tuple : Tuple3[RDFNode,  Variable, Variable]) : TripleTemplate = TripleTemplate(tuple._1, tuple._2, tuple._3)
    implicit def tuple3EtoTripleTemplate(tuple : Tuple3[Variable, RDFNode,  RDFNode] ) : TripleTemplate = TripleTemplate(tuple._1, tuple._2, tuple._3)
    implicit def tuple3FtoTripleTemplate(tuple : Tuple3[Variable, RDFNode,  Variable]) : TripleTemplate = TripleTemplate(tuple._1, tuple._2, tuple._3)
    implicit def tuple3GtoTripleTemplate(tuple : Tuple3[Variable, Variable, RDFNode] ) : TripleTemplate = TripleTemplate(tuple._1, tuple._2, tuple._3)
    implicit def tuple3HtoTripleTemplate(tuple : Tuple3[Variable, Variable, Variable]) : TripleTemplate = TripleTemplate(tuple._1, tuple._2, tuple._3)
    
}