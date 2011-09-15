package es.ctic.tabels

abstract class DataSource {

  def getValue(point : Point) : CellValue
  val filenames : Seq[String]
  def getTabs(filename : String) : Seq[String]
  def getRows(filename : String, tabName : String) : Int
  def getCols(filename : String, tabName : String) : Int
  
}

case class Point(path : String, tab: String, col: Int, row: Int)

abstract class CellValue{
  
  def getContent : String
  
}