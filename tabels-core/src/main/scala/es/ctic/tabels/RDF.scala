package es.ctic.tabels

abstract class RDFNode() {
    
	def asBoolean : Literal
	def asString : Literal
	def +(suffix : String) : RDFNode
	
}

case class Literal(value : Any, rdfType: Resource = XSD_STRING, langTag : String = "") extends RDFNode {
	
	def truthValue : Boolean = Set("true", "1") contains this.asBoolean.value.toString

	override def +(suffix : String) : Literal = Literal(this.value + suffix)

    /**
     * This method calculates the effective boolean value of the
     * literal by applying the rules of fn:boolean, see
     * http://www.w3.org/TR/rdf-sparql-query/#ebv
     */
	override def asBoolean : Literal = rdfType match {
	    case XSD_BOOLEAN => this
	    case XSD_STRING =>  if (value.toString.length > 0) LITERAL_TRUE else LITERAL_FALSE
	    case XSD_INT | XSD_DOUBLE | XSD_DECIMAL | XSD_FLOAT => if (value.toString.toDouble == 0.0) LITERAL_FALSE else LITERAL_TRUE
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
object XSD_DATE extends Resource("http://www.w3.org/2001/XMLSchema#date")
object LITERAL_TRUE extends Literal("true", XSD_BOOLEAN)
object LITERAL_FALSE extends Literal("false", XSD_BOOLEAN)


// FIXME: literals can not be properties
case class Statement(subject: RDFNode, property: RDFNode, obj:RDFNode){

}
