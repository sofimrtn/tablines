package es.ctic.tabels

import es.ctic.tabels.RelativePos._

abstract class Position(r: Int, c : Int) extends Evaluable {
  
	val row = r
	val col = c
	def accept(vis : Visitor) 
	def getRow(evaluationContext: EvaluationContext) : Int
	def getCol(evaluationContext: EvaluationContext) : Int
}

case class FixedPosition (override val row : Int , override val col: Int) extends Position(row , col) {
	
	override def toString() : String = columnConverter.intToAlpha(col) + (row+1)
	
	override def getRow(evaluationContext: EvaluationContext) : Int = row
	override def getCol(evaluationContext: EvaluationContext) : Int = col
	override def accept(vis : Visitor) = {
	    
	    vis.visit(this)
	  }
}

case class WithVariablePosition (variable : Variable) extends Position(0,0) {
	
	override def toString() : String = columnConverter.intToAlpha(col) + (row+1)
	
	override def getRow(evaluationContext: EvaluationContext) : Int = evaluationContext.bindings.getPoint(variable).row
	override def getCol(evaluationContext: EvaluationContext) : Int = evaluationContext.bindings.getPoint(variable).col
	override def accept(vis : Visitor) = {
	    
	    vis.visit(this)
	  }
}

case class RelativePosition (relativity : RelativePos, reference:Position, displacement: Int) extends Position(0,0) {
	
	override def toString() : String = columnConverter.intToAlpha(col) + (row+1)
	
	override def getRow(evaluationContext: EvaluationContext) : Int ={
	 
	  println("posicion row\n" + reference.getCol(evaluationContext))
	  
	  relativity match{
	    case RelativePos.top =>  (reference.getRow(evaluationContext)) - displacement
	    case RelativePos.bottom =>  (reference.getRow(evaluationContext)) + displacement
	    case _=> reference.getRow(evaluationContext)
	  }
	}
	
	override def getCol(evaluationContext: EvaluationContext) : Int = {
		
	  println("posicion col \n" + reference.getCol(evaluationContext))
	  
	  relativity match{
	    case RelativePos.left =>  (reference.getCol(evaluationContext)) - displacement
	    case RelativePos.right =>  (reference.getCol(evaluationContext)) + displacement
	    case _=> reference.getCol(evaluationContext)
	  }
	  
	  
	}
	override def accept(vis : Visitor) = {
	    
	    vis.visit(this)
	  }
}

