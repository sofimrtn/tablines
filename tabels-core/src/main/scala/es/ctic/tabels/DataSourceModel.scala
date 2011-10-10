package es.ctic.tabels
import es.ctic.tabels.Dimension._


abstract class DataSource {

  def getValue(point : Point) : CellValue
  val filenames : Seq[String]
  def getTabs(filename : String) : Seq[String]
  def getRows(filename : String, tabName : String) : Int
  def getCols(filename : String, tabName : String) : Int
  def getDimensionRange(dimension:Dimension, evaluationContext: EvaluationContext):Seq[String]
  
}

case class Point(path : String, tab: String, col: Int, row: Int){
  
  def RightPoint : Point = Point(path,tab,col+1,row)
  def LeftPoint : Point = Point(path,tab,col-1,row)
  def TopPoint : Point = Point(path,tab,col,row-1)
  def BottomPoint : Point = Point(path,tab,col,row+1)
}
	

abstract class CellValue{
  
  def getContent : String
  
}