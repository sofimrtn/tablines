package es.ctic.tabels

import org.scalatest.junit.JUnitSuite
import org.junit.Test
import org.junit.Assert._
import java.io.{File,ByteArrayOutputStream}
import com.hp.hpl.jena.rdf.model.Model
import com.hp.hpl.jena.query.{QueryFactory,QueryExecutionFactory}
import grizzled.slf4j.Logging

abstract class AbstractFunctionalTest extends JUnitSuite with Logging {

    val prefixes = """
        PREFIX rdf: <http://www.w3.org/1999/02/22-rdf-syntax-ns#>
        PREFIX rdfs: <http://www.w3.org/2000/01/rdf-schema#>
        PREFIX xsd: <http://www.w3.org/2001/XMLSchema#>
        PREFIX owl: <http://www.w3.org/2002/07/owl#>
        PREFIX ex: <http://example.org/>
        """
	val program : String
    val spreadsheets : Seq[String]

    def runTabels() : Model = {
        val parser = new TabelsParser()
        val parsedProgram = parser.parseProgram(prefixes + program)
		val interpreter : Interpreter = new Interpreter()
		val spreadsheetFiles = spreadsheets map { x => new File(this.getClass().getResource("/" + x).toURI()) }
		val dataSource : DataSource = new DataAdaptersDelegate(spreadsheetFiles)
		val dataOutput : JenaDataOutput = new JenaDataOutput(parsedProgram.prefixesAsMap)
		interpreter.interpret(parsedProgram, dataSource, dataOutput)
    	return dataOutput.model
    }
    
    def assertAskTrue(model : Model, queryString : String) {
        val query = QueryFactory.create(prefixes + queryString)
        val qe = QueryExecutionFactory.create(query, model)
        assertTrue("Query '" + queryString + "' must return TRUE in model\n" + modelAsString(model), qe.execAsk())
        qe.close()
    }
    
    def modelAsString(model : Model) : String = {
        val modelOutput = new ByteArrayOutputStream()
		model.write(modelOutput, "N3")
		return modelOutput.toString()
    }
    
}

