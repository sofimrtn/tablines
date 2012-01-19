package es.ctic.tabels

import es.ctic.tabels.Dimension._
import scala.collection.immutable.HashMap
import scala.collection.mutable.ListBuffer
import grizzled.slf4j.Logging

class Interpreter extends Logging {
  
  def interpret(root : S, dataSource: DataSource, dataOut : DataOutput) = {
    //FIX IT//
    val events = new ListBuffer[Event]
    val evaluationContext = EvaluationContext()
    
    root.templateList.filter(_.variables.isEmpty).foreach { t => 
        logger.debug("Instantiation of variable-less template " + t)
        t.instantiate(Bindings(), dataOut)
    }
    
    var visitor = new VisitorEvaluate(dataSource,events, evaluationContext)     
    visitor.visit(root)
     
    logger.debug("List of events: " + events)
   
    // FIXME: do not instantiate ALL templates for EACH event, be selective
    for ( t <- root.templateList ; e <- events ) {
		logger.debug("Considering instantiation of template " + t + " for event " + e)
		if ( t.variables subsetOf e.bindings.variables ) {
			if ( !((e.lastBoundVariables intersect t.variables) isEmpty) ) {
				t.instantiate(e.bindings, dataOut)				
			} else {
				logger.debug("The template " + t + " is not relevant for event " + e)
			}
		} else {
			logger.debug("The template " + t + " cannot be instantiated for event " + e + " because there are unbound variables")
		}
 /*    }//FIXME: Add triples for undisambiguated nodes
    evaluationContext.workingArea.mapUnDisambiguted.foreach{map =>
      dataOut.generateOutput(new Statement(map._1,Resource("relacion"),Literal(map._2.label)))*/
   	}
  }

}

case class Event(bindings : Bindings, lastBoundVariables : Set[Variable])

