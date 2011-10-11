package es.ctic.tabels

import es.ctic.tabels.RelativePos._

abstract class Position(r: Int, c : Int) extends EvaluablePosition {
  
	val row = r
	val col = c
	def accept(vis : Visitor) 
	def getPosition(evaluationContext: EvaluationContext) : Point

}
case class FixedPosition (override val row : Int , override val col: Int) extends Position(row , col) {
	
	override def toString() : String = columnConverter.intToAlpha(col) + (row+1)
	
	override def getPosition(evaluationContext: EvaluationContext) : Point = Point(evaluationContext.getValue(Dimension.files),evaluationContext.getValue(Dimension.sheets),col,row)
	
	override def accept(vis : Visitor) = {
	    
	    vis.visit(this)
	  }
}

case class WithVariablePosition (variable : Variable) extends Position(0,0) {
	
	override def toString() : String = columnConverter.intToAlpha(col) + (row+1)
	
	override def getPosition(evaluationContext: EvaluationContext) : Point = evaluationContext.bindings.getPoint(variable)
	override def accept(vis : Visitor) = {
	    
	    vis.visit(this)
	  }
}

case class RelativePosition (relativity : RelativePos, reference:Position, displacement: Int) extends Position(0,0) {
	
	override def toString() : String = columnConverter.intToAlpha(col) + (row+1)
	
	override def getPosition(evaluationContext: EvaluationContext) : Point ={
	  Point(evaluationContext.getValue(Dimension.files),evaluationContext.getValue(Dimension.sheets),col,row)
	  relativity match{
	    case RelativePos.top =>  Point((reference.getPosition(evaluationContext)).path,(reference.getPosition(evaluationContext)).tab,(reference.getPosition(evaluationContext)).col,(reference.getPosition(evaluationContext)).row - displacement)
	    case RelativePos.bottom =>  Point((reference.getPosition(evaluationContext)).path,(reference.getPosition(evaluationContext)).tab,(reference.getPosition(evaluationContext)).col,(reference.getPosition(evaluationContext)).row + displacement)
	    case RelativePos.left =>  Point((reference.getPosition(evaluationContext)).path,(reference.getPosition(evaluationContext)).tab,(reference.getPosition(evaluationContext)).col - displacement,(reference.getPosition(evaluationContext)).row)
	    case RelativePos.right =>  Point((reference.getPosition(evaluationContext)).path,(reference.getPosition(evaluationContext)).tab,(reference.getPosition(evaluationContext)).col + displacement,(reference.getPosition(evaluationContext)).row)
	  }
	
	}
	
	override def accept(vis : Visitor) = {
	    
	    vis.visit(this)
	  }
}

