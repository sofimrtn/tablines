package es.ctic.tabels

import java.io.{FileInputStream, File, FileNotFoundException}
import es.ctic.tabels.Point
import scala.Cell
import es.ctic.tabels.ExcelCellValue

import scala.collection.JavaConversions._

import org.apache.poi.hssf.usermodel.HSSFDateUtil
import org.apache.poi.hssf.usermodel._
import grizzled.slf4j.Logging
import java.text.SimpleDateFormat
import org.apache.poi.ss.usermodel.DateUtil
import org.apache.poi.ss.usermodel


class ExcelDataAdapter(file : File) extends DataAdapter with Logging {

	//val ws = new WorkbookSettings()
	//ws.setEncoding("CP1252")

  private val workbook = openWorkbook(file)
	
	
	private def openWorkbook(file : File) : HSSFWorkbook = {
        try {
            return new HSSFWorkbook(new FileInputStream(file))
            //return Workbook.getWorkbook(file,ws)
        } catch {
            case e : FileNotFoundException =>
                logger.error("While reading Excel file " + file.getCanonicalPath, e)
                throw new NoInputFiles
            case e: Exception =>
                logger.error("While reading Excel file " + file.getCanonicalPath, e)
                throw new InvalidInputFileCannotReadXls(file.getName)
	   }
	}
  
	override val uri = file.getCanonicalPath()

  override def getValue(point : Point) : CellValue = {
	logger.trace("Getting value at " + point)
	try{
		val sheet : HSSFSheet = workbook.getSheet(point.tab)
		val cell = sheet.getRow(point.row).getCell(point.col )
    val evaluator = new HSSFFormulaEvaluator(workbook) // needed if cell is a formula
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
    sheet.rowIterator().seq.foldLeft(0)((max,row) => if (row.asInstanceOf[HSSFRow].getLastCellNum > max) row.asInstanceOf[HSSFRow].getLastCellNum else max )
  }

}

case class ExcelCellValue (cell:HSSFCell, evaluator: HSSFFormulaEvaluator) extends CellValue with Logging {
    
  val decimalFormatPattern = """^(?:#,##)?0(?:\.0+)?(?:;.*)?$""".r

  override def getContent : Literal ={
    logger.trace("Actual cell type is: " + cell.getCellType() + " and its value is: "+cell)
    cell.getCellType() match{
	  case HSSFCell.CELL_TYPE_NUMERIC =>
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
	  case HSSFCell.CELL_TYPE_STRING =>Literal(cell.getRichStringCellValue, XSD_STRING)
	  case HSSFCell.CELL_TYPE_BOOLEAN => Literal(cell.getBooleanCellValue().toString(), XSD_BOOLEAN)
	  case HSSFCell.CELL_TYPE_FORMULA =>{
        val evaluatedCell=  evaluator.evaluate(cell)
        evaluatedCell.getCellType match{
           case HSSFCell.CELL_TYPE_NUMERIC =>

            /* if (DateUtil.isCellDateFormatted(new HSSFCell(cell.getSheet, evaluatedCell))){
               val xsdDateFormater = new SimpleDateFormat("yyyy-MM-dd")
               //FIXME date formula result problem
               //val value = xsdDateFormater.format(evaluatedCell.getJavaDate)
               Literal(evaluatedCell.toString, XSD_DATE)
             }else{ */
               val value = evaluatedCell.getNumberValue
               if(value == value.toInt)
                 Literal(value.toInt, XSD_INTEGER)
               else Literal(value.toString, XSD_DOUBLE)
             //}
           case HSSFCell.CELL_TYPE_STRING => Literal(evaluatedCell.getStringValue, XSD_STRING)
           case HSSFCell.CELL_TYPE_BOOLEAN => Literal(evaluatedCell.getBooleanValue().toString(), XSD_BOOLEAN)
           case HSSFCell.CELL_TYPE_BLANK => Literal("", XSD_STRING)
           case HSSFCell.CELL_TYPE_ERROR => Literal("", XSD_STRING)
           }
         }
    case HSSFCell.CELL_TYPE_BLANK => Literal("", XSD_STRING)
    case HSSFCell.CELL_TYPE_ERROR => Literal("", XSD_STRING)
	}
    }


  //FIXME Method needed for grider tool
 /* override def getStyle : CellStyle ={

    val format = cell.getCellStyle

    if (format!=null) {
      val fontStyle = if (cell.getType==CellType.EMPTY)
                        FontStyle.none
                      else if ((format.getFont.getBoldWeight!=0)&&format.getFont.isItalic)
                        FontStyle.italic_bold
                      else if(format.getFont.isItalic &&(format.getFont.getBoldWeight==0))
                        FontStyle.italic
                      else if(!format.getFont.isItalic && (format.getFont.getBoldWeight!=0))
                        FontStyle.bold
                      else FontStyle.none

      val fontColor = if (cell.getType==CellType.EMPTY)
                         (0,0,0)
                      else  (format.getFont.getColour.getDefaultRGB.getRed,format.getFont.getColour.getDefaultRGB.getGreen,format.getFont.getColour.getDefaultRGB.getBlue)

      val fontSize = if (cell.getType==CellType.EMPTY)
                      0.toFloat
                     else format.getFont.getPointSize

      CellStyle((cell.getCellFormat.getBackgroundColour.getDefaultRGB.getRed,cell.getCellFormat.getBackgroundColour.getDefaultRGB.getGreen,cell.getCellFormat.getBackgroundColour.getDefaultRGB.getBlue),CellFont(fontColor, fontStyle , fontSize),
            if(format.getBorderLine(jxl.format.Border.TOP).getDescription=="none") None else Some(CellBorder((format.getBorderColour(jxl.format.Border.TOP).getDefaultRGB.getRed,format.getBorderColour(jxl.format.Border.TOP).getDefaultRGB.getGreen,format.getBorderColour(jxl.format.Border.TOP).getDefaultRGB.getBlue),format.getBorderLine(jxl.format.Border.TOP).getDescription)),
            if(format.getBorderLine(jxl.format.Border.RIGHT).getDescription=="none") None else Some(CellBorder((format.getBorderColour(jxl.format.Border.RIGHT).getDefaultRGB.getRed,format.getBorderColour(jxl.format.Border.RIGHT).getDefaultRGB.getGreen,format.getBorderColour(jxl.format.Border.RIGHT).getDefaultRGB.getBlue),format.getBorderLine(jxl.format.Border.RIGHT).getDescription)),
            if(format.getBorderLine(jxl.format.Border.BOTTOM).getDescription=="none") None else Some(CellBorder((format.getBorderColour(jxl.format.Border.BOTTOM).getDefaultRGB.getRed,format.getBorderColour(jxl.format.Border.BOTTOM).getDefaultRGB.getGreen,format.getBorderColour(jxl.format.Border.BOTTOM).getDefaultRGB.getBlue),format.getBorderLine(jxl.format.Border.BOTTOM).getDescription)),
            if(format.getBorderLine(jxl.format.Border.LEFT).getDescription=="none")None else Some(CellBorder((format.getBorderColour(jxl.format.Border.LEFT).getDefaultRGB.getRed,format.getBorderColour(jxl.format.Border.LEFT).getDefaultRGB.getGreen,format.getBorderColour(jxl.format.Border.LEFT).getDefaultRGB.getBlue),format.getBorderLine(jxl.format.Border.LEFT).getDescription)))
    }
    else CellStyle()
  }        */

    
}
