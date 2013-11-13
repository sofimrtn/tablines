package es.ctic.tabels

import java.io.{FileInputStream, File, FileNotFoundException}

import scala.collection.JavaConversions._

import org.apache.poi.hssf.usermodel._
import grizzled.slf4j.Logging
import java.text.SimpleDateFormat
import org.apache.poi.ss.usermodel.{FormulaEvaluator, Workbook, DateUtil,Cell}
import org.apache.poi.xssf.usermodel.{XSSFFormulaEvaluator, XSSFWorkbook}


class ExcelDataAdapter(file : File) extends DataAdapter with Logging {

	private val workbook = openWorkbook(file)
  private val evaluator =   openEvaluator

  private def openEvaluator :FormulaEvaluator = workbook match
      { case  workbook:HSSFWorkbook => new HSSFFormulaEvaluator(workbook).asInstanceOf[FormulaEvaluator]
        case  workbook:XSSFWorkbook => new XSSFFormulaEvaluator(workbook).asInstanceOf[FormulaEvaluator]
      }
	private def openWorkbook(file : File) : Workbook = {
        try {
          file.getName match {
            case f if f.matches(""".+\.xls$""")  => new HSSFWorkbook(new FileInputStream(file))
            case f if f.matches(""".+\.xlsx$""") => new XSSFWorkbook(new FileInputStream(file))
            case _ => throw new InvalidInputFileCannotReadXls(file.getName)
          }

        } catch {
            case e : FileNotFoundException =>
                logger.error("While reading Excel file " + file.getCanonicalPath, e)
                throw new NoInputFiles
            case e: Exception =>
                logger.error("While reading Excel file " + file.getCanonicalPath , e)
                throw new InvalidInputFileCannotReadXls(file.getName)
	   }
	}
  
	override val uri = file.getCanonicalPath()

  override def getValue(point : Point) : CellValue = {
	logger.trace("Getting value at " + point)
	try{
		val sheet = workbook.getSheet(point.tab)
		val cell = sheet.getRow(point.row).getCell(point.col )
	    return ExcelCellValue(cell, evaluator)
	}catch{
	  case e=>throw new IndexOutOfBounds(point)
	}
  }
  
  override def getTabs() : Seq[String] = {
    val sheetNames = (0 to workbook.getNumberOfSheets-1).map(index => workbook.getSheetName(index))
    return sheetNames
  }
  
  override def getRows(tabName : String) : Int = {
    val sheet = workbook.getSheet(tabName)
    sheet.getLastRowNum + 1
  }
  
  override def getCols(tabName : String) : Int = {
    val sheet = workbook.getSheet(tabName)
    //Iterate through files looking for the one with the biggest number of columns
    sheet.rowIterator().seq.foldLeft(0)((max,row) => if (row.getLastCellNum > max) row.getLastCellNum else max )
  }

}

case class ExcelCellValue (cell:Cell, evaluator: FormulaEvaluator) extends CellValue with Logging {
    
  val decimalFormatPattern = """^(?:#,##)?0(?:\.0+)?(?:;.*)?$""".r

  override def getContent : Literal ={
    logger.trace("Actual cell type is: " + cell.getCellType() + " and its value is: "+cell)
    cell.getCellType() match{
	  case Cell.CELL_TYPE_NUMERIC =>
                if (DateUtil.isCellDateFormatted(cell)){
                  val xsdDateFormater = new SimpleDateFormat("yyyy-MM-dd")
                  val value = xsdDateFormater.format(cell.getDateCellValue)
                  Literal(value, XSD_DATE)

                } else{
                  val value = cell.getNumericCellValue
                  if(value == value.toInt)
                    Literal(value.toInt, XSD_INTEGER)
                  else Literal(value.toString, XSD_DOUBLE)
                }
	  case Cell.CELL_TYPE_STRING =>Literal(cell.getRichStringCellValue, XSD_STRING)
	  case Cell.CELL_TYPE_BOOLEAN => Literal(cell.getBooleanCellValue().toString(), XSD_BOOLEAN)
	  case Cell.CELL_TYPE_FORMULA =>{
        val evaluatedCell=  evaluator.evaluate(cell)
        evaluatedCell.getCellType match{
           case Cell.CELL_TYPE_NUMERIC =>
             //FIXME date formula not recogniced as date
            /* if (DateUtil.isCellDateFormatted(new HSSFCell(cell.getSheet, evaluatedCell))){
               val xsdDateFormater = new SimpleDateFormat("yyyy-MM-dd")
               //val value = xsdDateFormater.format(evaluatedCell.getJavaDate)
               Literal(evaluatedCell.toString, XSD_DATE)
             }else{ */
               val value = evaluatedCell.getNumberValue
               if(value == value.toInt)
                 Literal(value.toInt, XSD_INTEGER)
               else Literal(value.toString, XSD_DOUBLE)
             //}
           case Cell.CELL_TYPE_STRING => Literal(evaluatedCell.getStringValue, XSD_STRING)
           case Cell.CELL_TYPE_BOOLEAN => Literal(evaluatedCell.getBooleanValue().toString(), XSD_BOOLEAN)
           case Cell.CELL_TYPE_BLANK => Literal("", XSD_STRING)
           case Cell.CELL_TYPE_ERROR => Literal("", XSD_STRING)
           }
         }
    case Cell.CELL_TYPE_BLANK => Literal("", XSD_STRING)
    case Cell.CELL_TYPE_ERROR => Literal("", XSD_STRING)
	}
    }

}
