package es.ctic.tabels
import com.hp.hpl.jena.rdf.model.{Model,ModelFactory}
import com.hp.hpl.jena.datatypes.xsd.XSDDatatype

class JenaDataOutput(prefixes : Map[String,NamedResource] = Map()) extends DataOutput {

  val model : Model = ModelFactory.createDefaultModel()
  prefixes.foreach { case (prefix,ns) => model.setNsPrefix(prefix, ns.uri) }
  
  def generateOutput(statement: Statement){ 
   model.add(model.createStatement(createSubject(statement.subject),createProperty(statement.property),createObject(statement.obj)))
  }
  
  def createSubject(s : RDFNode) : com.hp.hpl.jena.rdf.model.Resource = {
    s match {
    	case NamedResource(uri) => model.createResource(uri)
    	case BlankNode() => model.createResource()
    	case Literal(value, _, _) => throw new TemplateInstantiationException("Unable to convert literal "+value+ " to RDF resource in the subject of a triple" )
    						
    }
  }

  def createProperty(s : RDFNode) : com.hp.hpl.jena.rdf.model.Property = {
    s match {
    	case NamedResource(uri) => model.createProperty(uri) 
    	case BlankNode() => throw new TemplateInstantiationException("Unable to convert blank node to RDF named resource in the predicate of a triple")
    	case Literal(value, _, _) => throw new TemplateInstantiationException("Unable to convert literal "+value+ " to RDF resource in the predicate of a triple" )
    }
  }

  def createObject(s : RDFNode) : com.hp.hpl.jena.rdf.model.RDFNode = {
    s match {
    	case NamedResource(uri) => model.createResource(uri) 
    	case BlankNode() => model.createResource()
    	case Literal(value, XSD_STRING, "") => model.createLiteral(value.toString) // untyped
    	case Literal(value, XSD_STRING, langTag) => model.createLiteral(value.toString, langTag) // with language tag
    	case Literal(value, XSD_BOOLEAN, _) => model.createTypedLiteral(value, XSDDatatype.XSDboolean)
    	case Literal(value, XSD_INT, _) => model.createTypedLiteral(value, XSDDatatype.XSDint)
    	case Literal(value, XSD_DOUBLE, _) => model.createTypedLiteral(value, XSDDatatype.XSDdouble)
    	case Literal(value, XSD_FLOAT, _) => model.createTypedLiteral(value, XSDDatatype.XSDfloat)
    	case Literal(value, XSD_DECIMAL, _) => model.createTypedLiteral(value, XSDDatatype.XSDdecimal)
    	case Literal(value, XSD_DATE, _) => model.createTypedLiteral(value, XSDDatatype.XSDdate)
    }
  }

}