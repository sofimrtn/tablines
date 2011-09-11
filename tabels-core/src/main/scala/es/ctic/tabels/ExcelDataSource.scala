package es.ctic.tabels

import java.io.File
import jxl._
import java.util.Arrays
import grizzled.slf4j.Logging


class ExcelDataSource(fl : Seq[String]) extends DataSource with Logging {
  
  val files : Seq[String] = fl
  
  override def getValue(point : Point) : CellValue = {
	logger.debug("Getting value at " + point)
    val workbook : Workbook = Workbook.getWorkbook(new File (point.path) )
	val sheet : Sheet = workbook.getSheet(point.tab)
	val cell : Cell = sheet.getCell(point.col, point.row)
    return ExcelCellValue(cell)
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