package es.ctic.tabels

import es.ctic.tabels.RelativePos._

abstract class Position extends EvaluablePosition {
  
	def getPosition(evaluationContext: EvaluationContext) : Point

}

case class FixedPosition (row : Int, col: Int) extends Position {
	
	override def toString() : String = columnConverter.intToAlpha(col) + (row+1)
	
	override def getPosition(evaluationContext: EvaluationContext) : Point =
	  Point(evaluationContext.getValue(Dimension.files),evaluationContext.getValue(Dimension.sheets),col,row)
	
}

case class WithVariablePosition (variable : Variable) extends Position {
	
	override def toString() : String = variable.toString
	
	override def getPosition(evaluationContext: EvaluationContext) : Point =
	  evaluationContext.bindings.getPoint(variable)

}

case class RelativePosition (relativity : RelativePos, reference:Position, displacement: Int) extends Position {
	
	override def toString() : String = displacement.toString + " " + relativity + " " + reference.toString
	
	override def getPosition(evaluationContext: EvaluationContext) : Point = {
	  relativity match {
	    case RelativePos.top =>  Point((reference.getPosition(evaluationContext)).path,(reference.getPosition(evaluationContext)).tab,(reference.getPosition(evaluationContext)).col,(reference.getPosition(evaluationContext)).row - displacement)
	    case RelativePos.bottom =>  Point((reference.getPosition(evaluationContext)).path,(reference.getPosition(evaluationContext)).tab,(reference.getPosition(evaluationContext)).col,(reference.getPosition(evaluationContext)).row + displacement)
	    case RelativePos.left =>  Point((reference.getPosition(evaluationContext)).path,(reference.getPosition(evaluationContext)).tab,(reference.getPosition(evaluationContext)).col - displacement,(reference.getPosition(evaluationContext)).row)
	    case RelativePos.right =>  Point((reference.getPosition(evaluationContext)).path,(reference.getPosition(evaluationContext)).tab,(reference.getPosition(evaluationContext)).col + displacement,(reference.getPosition(evaluationContext)).row)
	  }
	
	}
	
}

