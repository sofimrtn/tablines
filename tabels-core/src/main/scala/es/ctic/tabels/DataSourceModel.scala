package es.ctic.tabels

abstract class DataSource{

  def getValue(point : Point) : CellValue 
  
}

case class Point(path : String, tab: String, col: Int, row: Int)

abstract class CellValue{
  
  def getContent : String
  
}