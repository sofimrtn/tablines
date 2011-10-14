package es.ctic.tabels
import com.hp.hpl.jena.rdf.model.{Model,ModelFactory}
import com.hp.hpl.jena.datatypes.xsd.XSDDatatype

class JenaDataOutput extends DataOutput{

  val model : Model = ModelFactory.createDefaultModel()
  
  def generateOutput(statement: Statement){ 
   model.add(model.createStatement(createSubject(statement.subject),createProperty(statement.property),createObject(statement.obj)))
  }
  
  def createSubject(s : RDFNode) : com.hp.hpl.jena.rdf.model.Resource = {
    s match {
    	case Resource(uri) => model.createResource(uri) 
    	case Literal(value, _, _) => throw new TemplateInstantiationException("Unable to convert literal "+value+ " to RDF resource in the subject of a triple" )
    						
    }
  }

  def createProperty(s : RDFNode) : com.hp.hpl.jena.rdf.model.Property = {
    s match {
    	case Resource(uri) => model.createProperty(uri) 
    	case Literal(value, _, _) => throw new TemplateInstantiationException("Unable to convert literal "+value+ " to RDF resource in the predicate of a triple" )
    }
  }

  def createObject(s : RDFNode) : com.hp.hpl.jena.rdf.model.RDFNode = {
    s match {
    	case Resource(uri) => model.createResource(uri) 
    	case Literal(value, XSD_STRING, "") => model.createLiteral(value) // untyped
    	case Literal(value, XSD_STRING, langTag) => model.createLiteral(value, langTag) // with language tag
    	case Literal(value, XSD_BOOLEAN, _) => model.createTypedLiteral(value, XSDDatatype.XSDboolean)
    	case Literal(value, XSD_INT, _) => model.createTypedLiteral(value, XSDDatatype.XSDint)
    	case Literal(value, XSD_DOUBLE, _) => model.createTypedLiteral(value, XSDDatatype.XSDdouble)
    	case Literal(value, XSD_FLOAT, _) => model.createTypedLiteral(value, XSDDatatype.XSDfloat)
    }
  }

}