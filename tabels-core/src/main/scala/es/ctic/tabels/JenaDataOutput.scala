package es.ctic.tabels
import com.hp.hpl.jena.rdf.model.{Model,ModelFactory}

class JenaDataOutput extends DataOutput{

  val model : Model = ModelFactory.createDefaultModel()
  
  def generateOutput(statement: Statement){ 
    
    //FIX ME//
    val s = model.createResource(statement.subject)
    val p = model.createProperty(statement.property)
    val o = model.createLiteral(statement.obj)
    model.add(model.createStatement(s,p,o))
  }

}