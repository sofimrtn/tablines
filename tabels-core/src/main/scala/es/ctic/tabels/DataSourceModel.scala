package es.ctic.tabels

abstract class DataSource {

  def getValue(point : Point) : CellValue
  def getFiles() : Seq[String]
  def getTabs(file : String) : scala.collection.mutable.Seq[String]
  
  
}

case class Point(path : String, tab: String, col: Int, row: Int)

abstract class CellValue{
  
  def getContent : String
  
}