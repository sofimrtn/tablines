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
   
    for ( t <- root.templateList ; (e, eventIndex) <- events.zipWithIndex ) {
		logger.debug("Considering instantiation of template " + t + " for event " + e)
		if ( t.variables subsetOf e.bindings.variables ) {
			if ( !((e.lastBoundVariables intersect t.variables) isEmpty) ) {
				t.instantiate(e.bindings, dataOut, Some(eventIndex))				
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

case class Binding(value : RDFNode, point: Point)

case class Bindings(bindingsMap : Map[Variable, Binding] = new HashMap()) {

  def variables : Set[Variable] = bindingsMap.keySet

  def isBound(variable : Variable) : Boolean = bindingsMap.contains(variable)
  
  def getValue(variable : Variable) : RDFNode = 
    
    if(isBound(variable))
    	bindingsMap.get(variable).get.value
    else throw new UnboundVariableException(variable)
   

  def getPoint(variable : Variable) : Point = bindingsMap.get(variable).get.point // throws exception if unbound
  
  def addBinding(variable : Variable, value : RDFNode, point: Point) : Bindings =
	Bindings(bindingsMap + (variable -> Binding(value, point)))
  
  def removeBinding(variable : Variable) : Bindings =
	Bindings(bindingsMap - (variable))
  
  def clear : Bindings = Bindings(Map())

}

