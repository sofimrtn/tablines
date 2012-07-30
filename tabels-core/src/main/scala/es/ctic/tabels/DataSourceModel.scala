package es.ctic.tabels

import es.ctic.tabels.Dimension._
import java.io.File

abstract class DataSource {

  def getValue(point : Point) : CellValue
  val filenames : Seq[String]
  def getTabs(filename : String) : Seq[String]
  def getRows(filename : String, tabName : String) : Int
  def getCols(filename : String, tabName : String) : Int
  def getRow(filename : String, tabName : String, row : Int) : Seq[Literal] =
    for (col <- 0 until getCols(filename, tabName))
        yield getValue(Point(filename, tabName, row = row, col = col)).getContent
  
}

abstract class DataAdapter {
    
    val uri : String
    def getValue(point : Point) : CellValue
    def getTabs() : Seq[String]
    def getRows(tabName : String) : Int
    def getCols(tabName : String) : Int    
    
}

object DataAdapter {
    
    val CSVFilePattern = """.+\.csv$""".r
    val ExcelFilePattern = """.+\.xls$""".r // FIXME: match .xlsx files too
    val ODFFilePattern = """.+\.ods$""".r
    val HTMLFilePattern = """.+\.html$""".r
    val DBFFilePattern =  """.+\.dbf$""".r
    val ZIPFilePattern =  """.+\.zip$""".r
    
    def createAdapter(url : String) : DataAdapter =
        url match {
            case CSVFilePattern() => new CSVDataAdapter(new File(url))
            case ExcelFilePattern() => new ExcelDataAdapter(new File(url))
            case ODFFilePattern() => new ODFDataAdapter(new File(url))
            case HTMLFilePattern() => new HTMLDataAdapter(new File(url))
            case DBFFilePattern() => new DBFDataAdapter(new File(url))
            case ZIPFilePattern() => new ZIPDataAdapter(new File(url))
            case _ => throw new UnrecognizedSpreadsheetFormatException(url)
        }
    
    def findAllRecognizedFilesFromDirectory(dir : File) : Seq[File] =
        dir.listFiles.toList.filter(_.getName match {
            case CSVFilePattern() | ExcelFilePattern() | HTMLFilePattern() | ODFFilePattern() | DBFFilePattern()=> true
            case _ => false
        })

}

class DataAdaptersDelegate(fl : Seq[File]) extends DataSource {

    private val adapters : Map[String, DataAdapter] = Map() ++ fl.map(file => (file.getCanonicalPath(), DataAdapter.createAdapter(file.getCanonicalPath())))

    val filenames : Seq[String] = fl.map(_.getCanonicalPath())
    override def getValue(point : Point) = adapters(point.path).getValue(point)
    override def getTabs(filename : String) = adapters(filename).getTabs()
    override def getRows(filename : String, tabName : String) = adapters(filename).getRows(tabName)
    override def getCols(filename : String, tabName : String) = adapters(filename).getCols(tabName)
    
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