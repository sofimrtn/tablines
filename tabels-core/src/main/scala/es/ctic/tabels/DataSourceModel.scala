package es.ctic.tabels

import es.ctic.tabels.Dimension._
import java.io.File
import es.ctic.miningTree.ClassificationModel

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
            case _ => throw new UnrecognizedSpreadsheetFormatException(url)
        }
    
    def findAllRecognizedFilesFromDirectory(dir : File) : Seq[File] =
        dir.listFiles.toList.filter(_.getName match {
            case CSVFilePattern()
                 | ExcelFilePattern()
                 | HTMLFilePattern()
                 | ODFFilePattern()
                 | DBFFilePattern()
                 | ZIPFilePattern()
                 | ZIPSHPFilePattern()
                 | PXFilePattern()=> true
            case _ => false
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
	
abstract class TabelsCell{

  def getContent:  Any
  def getStyle : CellStyle
}
abstract class CellHeading extends TabelsCell {
  var cellType: String = null
  var range=1
  def getClassification: String
  def getCellType: String
  def getValueType: NamedResource
  def getDataType: Option[NamedResource]
  def getDataStyle: Option[Seq[CellStyle]]
  def setRange(rang:Int) = range=rang

}

class CellBoxHeading(content:Any, classification:String, valueType:NamedResource, style:CellStyle, dataType:Option[NamedResource], dataStyle:Option[Seq[CellStyle]] ) extends CellHeading {

  override def getContent: Any = content
  override def getStyle : CellStyle = style
  override def getClassification: String = classification
  override def getCellType: String = getCellType
  override def getValueType: NamedResource = valueType
  override def getDataType: Option[NamedResource] = dataType
  override def getDataStyle: Option[Seq[CellStyle]] = dataStyle
  override def toString() : String = content.toString()

  //Override the Any.equals(Any):Boolean method to compare by content not by object hash
  override def equals(cell :Any): Boolean =  cell.isInstanceOf[CellBoxHeading] match{
                                                  case true =>  this.equals(cell.asInstanceOf[CellBoxHeading])
                                                  case false => this.equals(cell.toString)
                                              }
  def equals(cell :CellBoxHeading): Boolean =  content.toString==cell.getContent.toString
  def equals(cell :String): Boolean =  content.toString==cell


}

abstract class CellValue extends TabelsCell {
  
    val decimalPattern = """[0-9]*\.[0-9]+""".r
    val intPattern = """[0-9]+""".r

  def getClassification(inputAdapter:DataAdapter, point: Point,trainingPath:File) : String ={
    ClassificationModel.createModel(trainingPath)
    ClassificationModel.classifyCell(inputAdapter, point)

  }
  def getContent : Literal
  def getStyle : CellStyle

  /**
   * When there is no formatting information, this method does it
   * best to parse the cell value
   */
  def autodetectFormat(rawStringValue : String) : Literal = rawStringValue match {
      case intPattern() => Literal(rawStringValue, XSD_INTEGER)
      case decimalPattern() => Literal(rawStringValue, XSD_DOUBLE)
      case x => Literal(rawStringValue, XSD_STRING)
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