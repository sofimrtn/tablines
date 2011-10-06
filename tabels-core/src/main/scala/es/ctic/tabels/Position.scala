package es.ctic.tabels

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

case class VariableRelativePosition (variable : Variable) extends Position(0,0) {
	
	override def toString() : String = columnConverter.intToAlpha(col) + (row+1)
	
	override def getRow(evaluationContext: EvaluationContext) : Int = evaluationContext.bindings.getPoint(variable).row
	override def getCol(evaluationContext: EvaluationContext) : Int = evaluationContext.bindings.getPoint(variable).col
	override def accept(vis : Visitor) = {
	    
	    vis.visit(this)
	  }
}

case class RelativePosition (variable : Variable) extends Position(0,0) {
	
	override def toString() : String = columnConverter.intToAlpha(col) + (row+1)
	//FIX ME: I'm not sure where the problem comes from but columns and rows are crossed at these point 
	override def getRow(evaluationContext: EvaluationContext) : Int = evaluationContext.bindings.getPoint(variable).row
	override def getCol(evaluationContext: EvaluationContext) : Int = evaluationContext.bindings.getPoint(variable).col
	override def accept(vis : Visitor) = {
	    
	    vis.visit(this)
	  }
}