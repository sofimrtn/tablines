package es.ctic.tabels
import com.hp.hpl.jena.rdf.model.{Model,ModelFactory}

class JenaDataOutput extends DataOutput{

  val model : Model = ModelFactory.createDefaultModel()
  
  def generateOutput(statement: Statement){ 
   model.add(model.createStatement(createSubject(statement.subject),createProperty(statement.property),createObject(statement.obj)))
  }
  
  private def createSubject(s : RDFNode) : com.hp.hpl.jena.rdf.model.Resource = {
    s match {
    	case Resource(uri) => model.createResource(uri) 
    	case Literal(value, _) => throw new TemplateInstantiationException("Unable to convert literal "+value+ " to RDF resource in the subject of a triple" )
    						
    }
  }

  private def createProperty(s : RDFNode) : com.hp.hpl.jena.rdf.model.Property = {
    s match {
    	case Resource(uri) => model.createProperty(uri) 
    	case Literal(value, _) => throw new TemplateInstantiationException("Unable to convert literal "+value+ " to RDF resource in the predicate of a triple" )
    }
  }

  private def createObject(s : RDFNode) : com.hp.hpl.jena.rdf.model.RDFNode = {
    s match {
    	case Resource(uri) => model.createResource(uri) 
    	case Literal(value, _) => model.createLiteral(value)
    }
  }

}