package es.ctic.tabels

import java.io.File
import scala.collection.mutable.HashMap
import collection.JavaConversions._
import jxl._
import java.util.Arrays
import jxl.read.biff.BiffException
import es.ctic.tabels.Dimension._
import grizzled.slf4j.Logging


class ExcelDataSource(fl : Seq[File]) extends DataSource with Logging {
  
	private val files : Seq[File] = fl
	
	val filenames : Seq[String] = files.map(_.getName())
	
	private val workbooks = new HashMap[File, Workbook]()
	
	private def getWorkbook(file : File) : Workbook = {
	    try {
	        workbooks.get(file) match {
                case Some(workbook) => return workbook
                case None =>
                    val workbook = Workbook.getWorkbook(file)
	                workbooks.put(file, workbook)
	                return workbook
            }
        } catch {
            case e : BiffException =>
                logger.error("While reading Excel workbook", e)
                throw new InvalidInputFile(file.getName)
	   }
	}
  
  override def getValue(point : Point) : CellValue = {
	logger.debug("Getting value at " + point)
    val workbook : Workbook = getWorkbook(new File (point.path) )
	val sheet : Sheet = workbook.getSheet(point.tab)
	val cell : Cell = sheet.getCell(point.col, point.row)
    return ExcelCellValue(cell)
  }
  
  override def getTabs(filename : String) : Seq[String] = {
	 
    val workbook : Workbook = getWorkbook(new File (filename) )
    val sheetNames : Array[String] = workbook.getSheetNames()
    return sheetNames
}
  
  override def getRows(filename : String, tabName : String) : Int = {
    
    val workbook : Workbook = getWorkbook(new File (filename))
    val sheet : Sheet = workbook.getSheet(tabName)

    return sheet.getRows()
}
  override def getCols(filename : String, tabName : String) : Int = {
    
    val workbook : Workbook = getWorkbook(new File (filename))
    val sheet : Sheet = workbook.getSheet(tabName)

    return sheet.getColumns()
}

}
  
case class ExcelCellValue (cell : Cell) extends CellValue with Logging {
    
    val decimalFormatPattern = """^(?:#,##)?0(?:\.0+)?(?:;.*)?$""".r
    
  override def getContent : Literal ={
    logger.info("Actual cell type is: " + cell.getType() + " and its value is: "+cell.getContents())
    cell.getType() match{
	  case CellType.NUMBER => val value = cell.asInstanceOf[NumberCell].getValue()
	  						if(value == value.toInt)
	  							Literal(value.toInt, XSD_INT)
	  						else Literal(value.toString, XSD_DECIMAL)
	  case CellType.LABEL =>Literal(cell.asInstanceOf[LabelCell].getString(), XSD_STRING)
	  case CellType.BOOLEAN => Literal(cell.asInstanceOf[BooleanCell].getValue().toString, XSD_BOOLEAN)
	  case CellType.NUMBER_FORMULA =>val value = cell.asInstanceOf[NumberFormulaCell].getValue()
	  						if(value == value.toInt)
	  							Literal(value.toInt, XSD_INT)
	  						else Literal(value.toFloat, XSD_DECIMAL)
	  case CellType.BOOLEAN_FORMULA => Literal(cell.asInstanceOf[BooleanFormulaCell].getValue().toString, XSD_BOOLEAN)
	  case CellType.STRING_FORMULA =>Literal(cell.asInstanceOf[StringFormulaCell].getString(), XSD_STRING)
	  case CellType.DATE =>Literal(cell.asInstanceOf[DateCell].getDate(), XSD_DATE)
	  case CellType.DATE_FORMULA =>Literal(cell.asInstanceOf[DateFormulaCell].getDate(), XSD_DATE)
	  case CellType.EMPTY => autodetectFormat(cell.getContents)
	
	}
    }
    /*if (cell.getCellFormat == null) Literal(cell.getContents)      
    else cell.getCellFormat.getFormat.getFormatString match {
        case "" => autodetectFormat
        case "@" => Literal(cell.getContents, XSD_STRING)
        case "0" => Literal(cell.getContents, XSD_INT)
        case decimalFormatPattern() => Literal(cell.getContents, XSD_DECIMAL)
        
        case str if str endsWith "%" => Literal(cell.getContents.dropRight(1), XSD_DECIMAL)
        case str if str contains "$" => Literal(cell.getContents.drop(1), XSD_DECIMAL)
        case "d-mmm-yy" => Literal(cell.getContents, XSD_DATE) // FIXME: parse date
        case x => logger.info("Unrecognized cell format: '" + x + "'")
                  return Literal(cell.getContents, XSD_STRING)
    }*/
    
}
