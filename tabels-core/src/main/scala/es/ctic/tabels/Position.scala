package es.ctic.tabels

import es.ctic.tabels.RelativePos._

abstract class Position {
  
	def calculatePoint(evaluationContext: EvaluationContext) : Point

}

case class FixedPosition (row : Int, col: Int) extends Position {
	
	override def toString() : String = columnConverter.intToAlpha(col) + (row+1)
	
	override def calculatePoint(evaluationContext: EvaluationContext) : Point =
	  Point(evaluationContext.getValue(Dimension.files),evaluationContext.getValue(Dimension.sheets),col,row)
	
}

case class WithVariablePosition (variable : Variable) extends Position {
	
	override def toString() : String = variable.toString
	
	override def calculatePoint(evaluationContext: EvaluationContext) : Point =
	  evaluationContext.bindings.getPoint(variable)

}

case class RelativePosition (relativity : RelativePos, reference:Position, displacement: Int) extends Position {
	
	override def toString() : String = displacement.toString + " " + relativity + " " + reference.toString
	
	override def calculatePoint(evaluationContext: EvaluationContext) : Point = {
	    val referencePoint : Point = reference.calculatePoint(evaluationContext)
	    relativity match {
	        case RelativePos.top => referencePoint.moveVertically(-displacement)
	        case RelativePos.bottom => referencePoint.moveVertically(displacement)
	        case RelativePos.left => referencePoint.moveHorizontally(-displacement)
	        case RelativePos.right => referencePoint.moveHorizontally(displacement)
	  }
	
	}
	
}

