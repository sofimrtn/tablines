package es.ctic.tabels
import es.ctic.tabels.Dimension._


abstract class DataSource {

  def getValue(point : Point) : CellValue
  val filenames : Seq[String]
  def getTabs(filename : String) : Seq[String]
  def getRows(filename : String, tabName : String) : Int
  def getCols(filename : String, tabName : String) : Int
  
}

case class Point(path : String, tab: String, col: Int, row: Int){
  
  def RightPoint : Point = moveHorizontally(1)
  def LeftPoint : Point = moveHorizontally(-1)
  def TopPoint : Point = moveVertically(-1)
  def BottomPoint : Point = moveVertically(1)
  
  def moveHorizontally(delta : Int) : Point = Point(path, tab, col + delta, row)
  def moveVertically(delta : Int) : Point = Point(path, tab, col, row + delta)
}
	

abstract class CellValue {
  
    val decimalPattern = """[0-9]*\.[0-9]+""".r
    val intPattern = """[0-9]+""".r

  def getContent : Literal

  /**
   * When there is no formatting information, this method does it
   * best to parse the cell value
   *
   */
  def autodetectFormat(rawStringValue : String) : Literal = rawStringValue match {
      case intPattern() => Literal(rawStringValue, XSD_INT)
      case decimalPattern() => Literal(rawStringValue, XSD_DECIMAL)
      case x => Literal(rawStringValue, XSD_STRING)
  }
  
}