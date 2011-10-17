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
  
  def RightPoint : Point = moveHorizontally(1)
  def LeftPoint : Point = moveHorizontally(-1)
  def TopPoint : Point = moveVertically(-1)
  def BottomPoint : Point = moveVertically(1)
  
  def moveHorizontally(delta : Int) : Point = Point(path, tab, col, row + delta)
  def moveVertically(delta : Int) : Point = Point(path, tab, col + delta, row)
}
	

abstract class CellValue{
  
  def getContent : Literal
  
}