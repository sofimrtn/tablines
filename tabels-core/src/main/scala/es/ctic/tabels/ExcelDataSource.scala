package es.ctic.tabels
import java.io.File
import jxl._

class ExcelDataSource extends DataSource{
  
  override def getValue(point : Point) : CellValue = {
    val workbook : Workbook = Workbook.getWorkbook(new File (point.path) )
	val sheet : Sheet = workbook.getSheet(point.tab)
	val a1 : Cell = sheet.getCell(point.col, point.row)
    return new ExcelCellValue(a1)
  }

}

case class ExcelCellValue (cell : Cell) extends CellValue {

  override def getContent : String =  cell.getContents()

}