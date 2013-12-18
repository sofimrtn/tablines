package es.ctic.tabels

import java.io.File
import grizzled.slf4j.Logging

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
    val TSVFilePattern = """.+\.(tsv|tab)$""".r
    val ExcelFilePattern = """.+\.xlsx?$""".r
    val ODFFilePattern = """.+\.ods$""".r
    val HTMLFilePattern = """.+\.html$""".r
    val DBFFilePattern =  """.+\.dbf$""".r
    val ZIPFilePattern =  """.+\.zip$""".r
    val ZIPSHPFilePattern =  """.+\.shp.zip$""".r
    val PXFilePattern =  """.+\.px$""".r
    
    def createAdapter(url : String) : DataAdapter =
        url match {
            case CSVFilePattern() => new CSVDataAdapter(new File(url))
            case ExcelFilePattern() => new ExcelDataAdapter(new File(url))
            case ODFFilePattern() => new ODFDataAdapter(new File(url))
            case HTMLFilePattern() => new HTMLDataAdapter(new File(url))
            case DBFFilePattern() => new DBFDataAdapter(new File(url))
            case ZIPSHPFilePattern() => new SHPMaplabDataAdapter(new File(url))
            case ZIPFilePattern() => new SHPMaplabDataAdapter(new File(url))
            case PXFilePattern() => new PXDataAdapter(new File(url))
            //There is a problem with regex matching size on the jvm. We alredy reached the limit so we ned to do the following cases in a differente place
            // check: https://issues.scala-lang.org/browse/SI-1133
            case _ => url match{
                            case TSVFilePattern() => new TSVDataAdapter(new File(url))
                            case _=> throw new UnrecognizedSpreadsheetFormatException(url)
                            }
        }
    
    def findAllRecognizedFilesFromDirectory(dir : File) : Seq[File] =
        dir.listFiles.toList.filter(f=> f.getName match {
            case CSVFilePattern()
                 | ExcelFilePattern()
                 | HTMLFilePattern()
                 | ODFFilePattern()
                 | DBFFilePattern()
                 | ZIPFilePattern()
                 | ZIPSHPFilePattern()
                 | PXFilePattern()=> true
            case _ => f.getName match {
                              case TSVFilePattern() => true
                              case _ =>false
                              }
        })

}

class DataAdaptersDelegate(fl : Seq[File], baseDir : Option[File] = None) extends DataSource {

    private val adapters : Map[String, DataAdapter] = Map() ++ fl.map(file => (relativizeFilename(file), DataAdapter.createAdapter(file.getCanonicalPath())))

    val filenames : Seq[String] = adapters.keySet.toSeq
    override def getValue(point : Point) = adapters(point.path).getValue(point)
    override def getTabs(filename : String) = adapters(filename).getTabs()
    override def getRows(filename : String, tabName : String) = adapters(filename).getRows(tabName)
    override def getCols(filename : String, tabName : String) = adapters(filename).getCols(tabName) 
    
    def relativizeFilename(file : File) : String = baseDir match {
        case Some(prefix) if (file.getCanonicalPath().startsWith(prefix.getCanonicalPath())) => prefix.toURI().relativize(file.toURI()).getPath()
        case None => file.getCanonicalPath()
    }
    
}

case class Point(path : String, tab: String, col: Int, row: Int){
  
  def RightPoint : Point = moveHorizontally(1)
  def LeftPoint : Point = moveHorizontally(-1)
  def TopPoint : Point = moveVertically(-1)
  def BottomPoint : Point = moveVertically(1)
  
  def moveHorizontally(delta : Int) : Point = Point(path, tab, col + delta, row)
  def moveVertically(delta : Int) : Point = Point(path, tab, col, row + delta)
}
	

abstract class CellValue extends Logging{
  
    //val date1Pattern =  """^(19|20)\d\d[- /.](0[1-9]|1[012])[- /.](0[1-9]|[12][0-9]|3[01])$""".r //Date format yyyy-mm-dd o yyyy/mm/dd/ o yyyy.mm.dd
    //val date2Pattern = """^(0[1-9]|1[012])[- /.](0[1-9]|[12][0-9]|3[01])[- /.](19|20)\d\d$""".r  //Date format mm-dd-yyy o mm/dd/yyy o mm.dd.yyy
    //val date3Pattern = """^(0[1-9]|[12][0-9]|3[01])[- /.](0[1-9]|1[012])[- /.](19|20)\d\d$""".r  //Date format dd-mm-yyy o dd/mm/yyy o dd.mm.yyy
    val decimalPattern = """(-)?(\d+)?(\.|,)\d+""".r
    val intPattern = """(-)?[0-9]+""".r
    val percentagePattern = """(-)?[0-9]+%""".r

  def getContent : Literal

  /**
   * When there is no formatting information, this method does it
   * best to parse the cell value
   *
   */

  def autodetectFormat(rawStringValue : String) : Literal ={
    logger.info("Trying to autodetect format of value " + rawStringValue)

    val result : Literal = rawStringValue.trim match {
      case lit if(lit.matches(intPattern.toString)) =>  Literal(rawStringValue.toInt, XSD_INTEGER)
      case lit if(lit.matches(decimalPattern.toString))  =>  Literal(rawStringValue.replace(",",".").toDouble, XSD_DOUBLE)  //If the double number is formed with "," instead of "." it's replaced here
      case lit if(lit.matches(percentagePattern.toString)) => Literal((rawStringValue.replace("%", "").trim.toDouble/100).toDouble , XSD_DECIMAL)
      //TODO: Add date format recognition with the above defined patterns
      case default =>  Literal(rawStringValue, XSD_STRING)
    }

    return result
  }
  
}

case class CellStyle(backGColor:(Int,Int,Int)=(255,255,255), font:CellFont=CellFont(), border_top:Option[CellBorder]= None,border_right:Option[CellBorder]= None,border_bottom:Option[CellBorder]= None,border_left:Option[CellBorder]= None) {

  def == (cellStyle: CellStyle) : Boolean = {
      if(backGColor==cellStyle.backGColor && font==cellStyle.font && border_top==cellStyle.border_top && border_right==cellStyle.border_right && border_bottom==cellStyle.border_bottom && border_left==cellStyle.border_left)
        true
      else false
  }
}

case class CellBorder(color: (Int,Int,Int)=(0,0,0), style: String="solid"){

  def ==(cellBorder : CellBorder):Boolean ={
      if (color==cellBorder && style==cellBorder.style)
        true
      else false
  }

}
case class CellFont(color:(Int,Int,Int)=(0,0,0), style: FontStyle.FontStyle=FontStyle.none , size: Float=12.toFloat){

  def ==(cellFont: CellFont):Boolean ={
    if (color==cellFont.color && style==cellFont.style && size==cellFont.size)
      true
    else false
  }

}