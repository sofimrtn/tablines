package es.ctic.tabels

abstract class RDFNode {
    
	def asBoolean : Literal
	def asString : Literal
	def +(suffix : String) : RDFNode  // FIXME: this method should not exist here
	
}

case class Literal(value : Any, rdfType: Resource = XSD_STRING, langTag : String = "") extends RDFNode {
    
    override def toString() = "\"" + value.toString + "\"" + (if (langTag != "") ("@" + langTag) else "") + (if (rdfType != XSD_STRING) ("^^" + rdfType) else "")
	
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
	def asInt : Literal = rdfType match {
	    case XSD_INT => this
	    case XSD_DOUBLE | XSD_DECIMAL | XSD_FLOAT => Literal(value.toString.toInt, XSD_INT)
	    case XSD_STRING => Literal(value.toString.toInt, XSD_INT)
	    case _ => throw new TypeConversionException(this, XSD_INT)
	}
	
}

object Literal {
    
    implicit def int2literal(i : Int) : Literal = Literal(i, XSD_INT)
    implicit def float2literal(f : Float) : Literal = Literal(f, XSD_FLOAT)
    implicit def double2literal(d : Double) : Literal = Literal(d, XSD_DOUBLE)
    implicit def boolean2literal(b : Boolean) : Literal = Literal(b, XSD_BOOLEAN)
    implicit def string2literal(s : String) : Literal = Literal(s)
    
    implicit def literal2int(l : Literal) : Int = l.asInt.value.asInstanceOf[Int]

}

case class Resource(uri : String) extends RDFNode {
    
    override def toString() = "<" + uri + ">"
    
    def toAbbrString(prefixes : Seq[(String,Resource)]) : String = toCurie(prefixes) getOrElse toString()
    
    def toCurie(prefixes : Seq[(String,Resource)]) : Option[String] =
        if (this == RDF_TYPE) Some("a")
        else prefixes find (uri startsWith _._2.uri) map { case (prefix, ns) => uri.replace(ns.uri, prefix + ":") }
    
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

case class Namespace(ns : String) {
    
    def apply(localName : String = "") = Resource(ns + localName)
    override def toString() : String = ns
    
}

object CommonNamespaces {

    object EX   extends Namespace("http://example.org/ex#")
    object SCV  extends Namespace("http://purl.org/NET/scovo#")
    object RDF  extends Namespace("http://www.w3.org/1999/02/22-rdf-syntax-ns#")
    object RDFS extends Namespace("http://www.w3.org/2000/01/rdf-schema#")
    object SKOS extends Namespace("http://www.w3.org/2004/02/skos/core#")
    object XSD  extends Namespace("http://www.w3.org/2001/XMLSchema#")
    
}
