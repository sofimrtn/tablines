package es.ctic.tabels
import java.io.File
import jxl._
import java.util.Arrays

class ExcelDataSource(fl : Seq[String]) extends DataSource{
  
  val files : Seq[String] = fl
  
  override def getValue(point : Point) : CellValue = {
    val workbook : Workbook = Workbook.getWorkbook(new File (point.path) )
	val sheet : Sheet = workbook.getSheet(point.tab)
	val a1 : Cell = sheet.getCell(point.col, point.row)
    return new ExcelCellValue(a1)
  }
  
  override def getFiles() : List[String]={
   return List()
  }
  override def getTabs(file : String) : scala.collection.mutable.Seq[String] = {
	 
    val workbook : Workbook = Workbook.getWorkbook(new File (file) )
    val sheetNames : Array[String] = workbook.getSheetNames()
    val listSheets : java.util.List[String] = new java.util.LinkedList()
    sheetNames.foreach(sheet => listSheets.add(sheet))
    return scala.collection.JavaConversions.asScalaBuffer(listSheets)
}

case class ExcelCellValue (cell : Cell) extends CellValue {

  override def getContent : String =  cell.getContents()

}
}