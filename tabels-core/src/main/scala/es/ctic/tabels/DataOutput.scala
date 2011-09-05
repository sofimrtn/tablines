package es.ctic.tabels


abstract class DataOutput  {

	def generateStatement(statement : Statement)
}

case class RDFDataOutput(tripletList:StatementList) extends DataOutput{
  
  def generateStatement(statement: Statement){ 
	  
  }
}
