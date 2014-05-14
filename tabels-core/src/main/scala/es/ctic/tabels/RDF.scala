package es.ctic.tabels

import scala.util.matching.Regex
import java.net.{URLEncoder, URI, URL}
import grizzled.slf4j.Logging

sealed abstract class RDFNode {
    
}

case class Literal(value : Any, rdfType: NamedResource = XSD_STRING, langTag : String = "") extends RDFNode {
    
    override def toString() = "\"" + value.toString + "\"" + (if (langTag != "") ("@" + langTag) else "") + (if (rdfType != XSD_STRING) ("^^" + rdfType) else "")
	
	def truthValue : Boolean = Set("true", "1") contains this.asBoolean.value.toString

    /**
     * This method calculates the effective boolean value of the
     * literal by applying the rules of fn:boolean, see
     * http://www.w3.org/TR/rdf-sparql-query/#ebv
     */
	def asBoolean : Literal = rdfType match {
	    case XSD_BOOLEAN => this
	    case XSD_STRING =>  if (value.toString.length > 0) LITERAL_TRUE else LITERAL_FALSE
	    case XSD_INT | XSD_INTEGER | XSD_DOUBLE | XSD_DECIMAL | XSD_FLOAT => if (value.toString.toDouble == 0.0) LITERAL_FALSE else LITERAL_TRUE
	}
	
	def asString : Literal = Literal(value)
	
	def asInt : Literal = rdfType match {
	 //   case XSD_INTEGER => this
	   case XSD_INTEGER | XSD_DOUBLE | XSD_DECIMAL | XSD_FLOAT | XSD_INT => Literal(value.toString.toFloat.toInt, XSD_INTEGER) // FIXME
	    case XSD_STRING => try {
	            Literal(value.toString.toFloat.toInt, XSD_INTEGER)
            } catch {
                case e : NumberFormatException => throw new TypeConversionException(this, XSD_INTEGER)
            }
	    case XSD_BOOLEAN => if (truthValue) Literal(1, XSD_INTEGER) else Literal(0, XSD_INTEGER)
	    case _ => throw new TypeConversionException(this, XSD_INTEGER)
	}
	
	def asFloat : Literal = rdfType match {
	   // case XSD_FLOAT => this
	    case XSD_FLOAT | XSD_INT | XSD_INTEGER | XSD_DOUBLE | XSD_DECIMAL => Literal(value.toString.toFloat, XSD_FLOAT) // FIXME
	    case XSD_STRING => try {
	            Literal(value.toString.toFloat, XSD_FLOAT)
            } catch {
                case e : NumberFormatException => throw new TypeConversionException(this, XSD_FLOAT)
            }
	    case XSD_BOOLEAN => if (truthValue) Literal(1.0, XSD_FLOAT) else Literal(0.0, XSD_FLOAT)
	    case _ => throw new TypeConversionException(this, XSD_FLOAT)
	}
	def asDouble : Literal = rdfType match {
	 //   case XSD_DOUBLE => this 
	    case XSD_DOUBLE | XSD_INT | XSD_INTEGER | XSD_FLOAT | XSD_DECIMAL => Literal(value.toString.toDouble, XSD_DOUBLE) // FIXME
	    case XSD_STRING => try {
	            Literal(value.toString.toDouble, XSD_DOUBLE)
            } catch {
                case e : NumberFormatException => throw new TypeConversionException(this, XSD_DOUBLE)
            }
	    case XSD_BOOLEAN => if (truthValue) Literal(1.0, XSD_DOUBLE) else Literal(0.0, XSD_DOUBLE)
		case _ => throw new TypeConversionException(this, XSD_DOUBLE)
	}
	
}

object Literal {
    
    implicit def int2literal(i : Int) : Literal = Literal(i, XSD_INTEGER)
    implicit def float2literal(f : Float) : Literal = Literal(f, XSD_FLOAT)
    implicit def double2literal(d : Double) : Literal = Literal(d, XSD_DOUBLE)
    implicit def boolean2literal(b : Boolean) : Literal = Literal(b, XSD_BOOLEAN)
    implicit def string2literal(s : String) : Literal = Literal(s)
    implicit def regex2literal(s : Regex) : Literal = Literal(s.toString)
   
    implicit def literal2int(l : Literal) : Int = try { l.asInt.value.asInstanceOf[Int] } catch{case e:java.lang.ClassCastException => throw new TypeConversionException(l,XSD_INTEGER)}
    implicit def literal2float(l : Literal) : Float = try {l.asFloat.value.asInstanceOf[Float]} catch{case e:java.lang.ClassCastException => throw new TypeConversionException(l,XSD_FLOAT)}
    implicit def literal2double(l : Literal) : Double = try {l.asDouble.value.asInstanceOf[Double]} catch{case e:java.lang.ClassCastException => throw new TypeConversionException(l,XSD_DOUBLE)}
    implicit def literal2long(l : Literal) : Long = try {l.asDouble.value.asInstanceOf[Long]} catch{case e:java.lang.ClassCastException => throw new TypeConversionException(l,XSD_DOUBLE)}
    implicit def literal2string(l : Literal) : String = l.asString.value.toString
    implicit def literal2regex(l : Literal) : Regex = l.asString.value.asInstanceOf[String].r
    implicit def literal2boolean(l : Literal) : Boolean = l.asString.value.asInstanceOf[Boolean]

}

abstract sealed class Resource() extends RDFNode  {
    
}
//FIXMe to be lazy
case class NamedResource(givenUri : String) extends Resource with Logging {
  val mailPattern = """^mailto:[A-Za-z0-9\._%+-]+@([A-Za-z0-9-]+\.)+[A-Za-z]{2,4}$"""//.r
  val phonePattern = """^tel:+?[0-9]{9,20}$"""

  //uri regex Source: http://snipplr.com/view/6889/regular-expressions-for-uri-validationparsing/
  val URIPattern = """^([a-z0-9+.-]+):(?://(?:((?:[a-z0-9-._~!$&'()*+,;=:]|%[0-9A-F]{2})*)@)?((?:[a-z0-9-._~!$&'()*+,;=]|%[0-9A-F]{2})*)(?::(\d*))?(/(?:[a-z0-9-._~!$&'()*+,;=:@/]|%[0-9A-F]{2})*)?|(/?(?:[a-z0-9-._~!$&'()*+,;=:@]|%[0-9A-F]{2})+(?:[a-z0-9-._~!$&'()*+,;=:@/]|%[0-9A-F]{2})*)?)(?:\?((?:[a-z0-9-._~!$&'()*+,;=:/?@]|%[0-9A-F]{2})*))?(?:#((?:[a-z0-9-._~!$&'()*+,;=:/?@]|%[0-9A-F]{2})*))?$"""
  val uri = try
    { givenUri match {
        //TODO: avoid blank named resources. It's allowed to because of the resource expression sintax :
        //      resource(?fullURI,<>) => generates a resource without prepending anything
        //      resource(?y) => generates a resource prepending de default prefix (my)
        case "" => ""
        case regularUri if regularUri matches (URIPattern) =>{
          logger.info(givenUri + " matches regular URI pattern: " + URIPattern )
          new URI(givenUri.trim).toString
        }
        case default => {
          logger.debug(givenUri + " matches regular URI pattern"+ mailPattern )
          val url = new URL(givenUri.trim)
          val protocol = url.getProtocol
          val user = if (url.getUserInfo != null) url.getUserInfo else null
          val port = if (url.getPort != null) url.getPort else -1
          val host = url.getHost
          val prePath = url.getPath
          val path = if (prePath.length > 0) prePath else null
          val query = if (url.getQuery != null) URLEncoder.encode(url.getQuery, "UTF-8") else null
          val fragment = if (url.getRef != null) URLEncoder.encode(url.getRef, "UTF-8") else null
          new URI(protocol, user, host, port, path, query, fragment).toString
        }
      }
    }
  catch
    {
         case e => throw new NotValidUriException(e getMessage)
    }

  logger.info("Creating new Namedresource: " + this.toString )

  val whitelistStartsWith = List( // Exceptions to the black listed urls
    "http://idi.fundacionctic.org/scovoxl/scovoxl",
    "http://idi.fundacionctic.org/tabels/project",
    "http://idi.fundacionctic.org/map-styles/symbols/",
    "http://localhost:8080/tabels/project"
  )

  val blacklistContains = List( // urls conteining an specific wrd or ip are blacklisted
    "192.168."
  )
  val blacklistStartsWith = List( // urls referencing internal addresses ar blacklisted
    "192.",
    "10.",
    "192.168.",
    "127.0.0.1",
    "localhost"
  )
  if (givenUri!="")
    try //Check here if the the given uri is not in conflict with the blacklist urls
      {
         if(( blacklistStartsWith.exists(entry => givenUri.toLowerCase.split(":")(1).replace("//","").startsWith(entry)) || blacklistContains.exists(entry => givenUri.toLowerCase.contains(entry))) &&
          !whitelistStartsWith.exists(entry => givenUri.toLowerCase.startsWith(entry)))
          throw new ServerReferedURIException(givenUri)


        // if(!(uri.toLowerCase.contains("http://idi.fundacionctic.org/scovoxl/scovoxl")|uri.toLowerCase.contains("/idi.fundacionctic.org/tabels/project"))&(uri.toLowerCase.contains("192.168.")|uri.toLowerCase.contains("fundacionctic")))
        // throw new ServerReferedURIException(uri)
      }
    catch
      {
        case srue:ServerReferedURIException => throw srue
      }

    override def toString() = "<" + uri + ">"
    
    def toAbbrString(prefixes : Seq[(String,NamedResource)]) : String = toCurie(prefixes) getOrElse toString()
    
    def toCurie(prefixes : Seq[(String,NamedResource)]) : Option[String] =
        if (this == RDF_TYPE) Some("a")
        else {val filteredPrefix = prefixes.filter(uri startsWith _._2.uri)
        	  if (!filteredPrefix.isEmpty){
        		  val prefix = filteredPrefix.maxBy(ns => ns._2.uri)
              logger.info("prefix: " +  prefix._2.uri)
        		  Some(uri.replace(prefix._2.uri, prefix._1 + ":"))
        	  }
        	  else None//Some("uri")
        	}
          /*prefixes find (uri startsWith _._2.uri) map { case (prefix, ns) => uri.replace(ns.uri, prefix + ":") }*/
    
	def +(suffix : String) : NamedResource = NamedResource(this.uri + suffix)

}

object RDF_TYPE extends NamedResource("http://www.w3.org/1999/02/22-rdf-syntax-ns#type")
object XSD_STRING extends NamedResource("http://www.w3.org/2001/XMLSchema#string")
object XSD_BOOLEAN extends NamedResource("http://www.w3.org/2001/XMLSchema#boolean")
object XSD_INT extends NamedResource("http://www.w3.org/2001/XMLSchema#int")
object XSD_INTEGER extends NamedResource("http://www.w3.org/2001/XMLSchema#integer")
object XSD_LONG extends NamedResource("http://www.w3.org/2001/XMLSchema#long")
object XSD_DOUBLE extends NamedResource("http://www.w3.org/2001/XMLSchema#double")
object XSD_FLOAT extends NamedResource("http://www.w3.org/2001/XMLSchema#float")
object XSD_DECIMAL extends NamedResource("http://www.w3.org/2001/XMLSchema#decimal")
object XSD_DATE extends NamedResource("http://www.w3.org/2001/XMLSchema#date")
object LITERAL_TRUE extends Literal("true", XSD_BOOLEAN)
object LITERAL_FALSE extends Literal("false", XSD_BOOLEAN)


case class BlankNode(id : Either[String,Int]) extends Resource {
    
    override def toString() = id match {
        case Left(x) => "_:" + x
        case Right(n) => "[]"
    }
    
}

// FIXME: literals can not be properties
case class Statement(subject: RDFNode, property: RDFNode, obj:RDFNode){

}

case class Namespace(ns : String) {
    
    def apply(localName : String = "") = NamedResource(ns + localName)
    override def toString() : String = ns
    
}

object CommonNamespaces {

    object EX   extends Namespace("http://example.org/ex#")
    object SCV  extends Namespace("http://purl.org/NET/scovo#")
    object RDF  extends Namespace("http://www.w3.org/1999/02/22-rdf-syntax-ns#")
    object RDFS extends Namespace("http://www.w3.org/2000/01/rdf-schema#")
    object SKOS extends Namespace("http://www.w3.org/2004/02/skos/core#")
    object XSD  extends Namespace("http://www.w3.org/2001/XMLSchema#")
    object DCAT  extends Namespace("http://www.w3.org/ns/dcat#")
    object DCT  extends Namespace("http://purl.org/dc/terms/")
    object FOAF  extends Namespace("http://xmlns.com/foaf/0.1/#")
    object NEOGEOSPATIAL  extends Namespace("http://geovocab.org/spatial#")
    object NEOGEOGEOMETRY  extends Namespace("http://geovocab.org/geometry#")
    object EMERGEL extends Namespace("http://purl.org/emergel#")
    
}

// type classes

// the class of the types that can be transformed to an RDF node
trait CanToRDFNode[a] {
    def toRDFNode(x : a) : RDFNode
}

object CanToRDFNode {
    import es.ctic.tabels.Literal._
    implicit def intToRDFNode = new CanToRDFNode[Int] {
        def toRDFNode(x : Int) : RDFNode = x
    }
    implicit def floatToRDFNode = new CanToRDFNode[Float] {
        def toRDFNode(x : Float) : RDFNode = x
    }
    implicit def doubleToRDFNode = new CanToRDFNode[Double] {
        def toRDFNode(x : Double) : RDFNode = x
    }
    implicit def longToRDFNode = new CanToRDFNode[Long] {
        def toRDFNode(x : Long) : RDFNode = x
    }
    implicit def stringToRDFNode = new CanToRDFNode[String] {
        def toRDFNode(x : String) : RDFNode = x
    }
    implicit def booleanToRDFNode = new CanToRDFNode[Boolean] {
        def toRDFNode(x : Boolean) : RDFNode = x
    }
    implicit def namedResourceToRDFNode = new CanToRDFNode[NamedResource] {
        def toRDFNode(x : NamedResource) : RDFNode = x
    }
    implicit def blankNodeToRDFNode = new CanToRDFNode[BlankNode] {
        def toRDFNode(x : BlankNode) : RDFNode = x
    }
    implicit def seqToRDFNode = new CanToRDFNode[Seq[Resource]] {
        def toRDFNode(x : Seq[Resource]) : RDFNode = x.head
    }
     implicit def literalToRDFNode = new CanToRDFNode[Literal] {
        def toRDFNode(x : Literal) : RDFNode = x
    }
     implicit def blankNodeBlockToRDFNode = new CanToRDFNode[Seq[TripleTemplate]] {
        def toRDFNode(x : Seq[TripleTemplate]) : RDFNode = if (x.head.s.isLeft)
        														x.head.s.left.get
        													else
        													  x.head.s.right.get.toString
        													  
    }
    
}

// the class of the types that can be obtained from an RDF node
trait CanFromRDFNode[a] {
    def fromRDFNode(rdfNode : RDFNode) : a
}

object CanFromRDFNode {
    import es.ctic.tabels.Literal._
    implicit def intFromRDFNode = new CanFromRDFNode[Int] {
        def fromRDFNode(rdfNode : RDFNode) : Int = rdfNode match {
            case l : Literal => l
            case r : Resource => throw new CannotConvertResourceToLiteralException(r)
        }
    }
    implicit def floatFromRDFNode = new CanFromRDFNode[Float] {
        def fromRDFNode(rdfNode : RDFNode) : Float = rdfNode match {
            case l : Literal => l
            case r : Resource => throw new CannotConvertResourceToLiteralException(r)
        }
    }
    implicit def doubleFromRDFNode = new CanFromRDFNode[Double] {
        def fromRDFNode(rdfNode : RDFNode) : Double = rdfNode match {
            case l : Literal => l
            case r : Resource => throw new CannotConvertResourceToLiteralException(r)
        }
    }
    implicit def longFromRDFNode = new CanFromRDFNode[Long] {
        def fromRDFNode(rdfNode : RDFNode) : Long = rdfNode match {
            case l : Literal => l
            case r : Resource => throw new CannotConvertResourceToLiteralException(r)
        }
    }
    implicit def stringFromRDFNode = new CanFromRDFNode[String] {
        def fromRDFNode(rdfNode : RDFNode) : String = rdfNode match {
            case l : Literal => l
            case r : Resource => throw new CannotConvertResourceToLiteralException(r)
        }
    }
    implicit def regexFromRDFNode = new CanFromRDFNode[Regex] {
        def fromRDFNode(rdfNode : RDFNode) : Regex = rdfNode match {
            case l : Literal => l
            case r : Resource => throw new CannotConvertResourceToLiteralException(r)
        }
    }
    implicit def booleanFromRDFNode = new CanFromRDFNode[Boolean] {
        def fromRDFNode(rdfNode : RDFNode) : Boolean = rdfNode match {
            case l : Literal => l
            case r : Resource => throw new CannotConvertResourceToLiteralException(r)
        }
    }
    implicit def anyFromRDFNode = new CanFromRDFNode[Any] {
        def fromRDFNode(rdfNode : RDFNode) : Any = rdfNode match {
            case l : Literal => l.value
            case r : Resource => throw new CannotConvertResourceToLiteralException(r)
        }
    }
   
}
