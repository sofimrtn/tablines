package es.ctic.tabels

import java.io.{File,FileNotFoundException}
import jxl._
import jxl.read.biff.BiffException
import grizzled.slf4j.Logging
import java.text.SimpleDateFormat


class ExcelDataAdapter(file : File) extends DataAdapter with Logging {

	val ws = new WorkbookSettings()
	ws.setEncoding("CP1252")
	
	
  private val workbook = openWorkbook(file)
	
	
	private def openWorkbook(file : File) : Workbook = {
        try {
            return Workbook.getWorkbook(file,ws)
        } catch {
            case e : FileNotFoundException =>
                logger.error("While reading Excel file " + file.getCanonicalPath, e)
                throw new NoInputFiles
            case e : BiffException =>
                logger.error("While reading Excel file " + file.getCanonicalPath, e)
                throw new InvalidInputFileCannotReadXls(file.getName)
	   }
	}
  
	override val uri = file.getCanonicalPath()

  override def getValue(point : Point) : CellValue = {
	logger.trace("Getting value at " + point)
	try{
		val sheet : Sheet = workbook.getSheet(point.tab)
		val cell : Cell = sheet.getCell(point.col, point.row)
	    return ExcelCellValue(cell)
	}catch{
	  case e=>throw new IndexOutOfBounds(point)
	}
  }
  
  override def getTabs() : Seq[String] = {
    val sheetNames : Array[String] = workbook.getSheetNames()
    return sheetNames
  }
  
  override def getRows(tabName : String) : Int = {
    val sheet : Sheet = workbook.getSheet(tabName)
    return sheet.getRows()
  }
  
  override def getCols(tabName : String) : Int = {
    val sheet : Sheet = workbook.getSheet(tabName)
    return sheet.getColumns()
  }

}

case class ExcelCellValue (cell : Cell) extends CellValue with Logging {
    
    val decimalFormatPattern = """^(?:#,##)?0(?:\.0+)?(?:;.*)?$""".r
    
  override def getContent : Literal ={
    logger.trace("Actual cell type is: " + cell.getType() + " and its value is: "+cell.getContents())
    cell.getType() match{
	  case CellType.NUMBER => val value = cell.asInstanceOf[NumberCell].getValue()
	  						if(value == value.toInt)
	  							Literal(value.toInt, XSD_INTEGER)
	  						else Literal(value.toString, XSD_DOUBLE)
	  case CellType.LABEL =>Literal(cell.asInstanceOf[LabelCell].getString(), XSD_STRING)
	  case CellType.BOOLEAN => Literal(cell.asInstanceOf[BooleanCell].getValue().toString, XSD_BOOLEAN)
	  case CellType.NUMBER_FORMULA =>val value = cell.asInstanceOf[NumberFormulaCell].getValue()
	  						if(value == value.toInt)
	  							Literal(value.toInt, XSD_INTEGER)
	  						else Literal(value.toFloat, XSD_DOUBLE)
	  case CellType.BOOLEAN_FORMULA => Literal(cell.asInstanceOf[BooleanFormulaCell].getValue().toString, XSD_BOOLEAN)
	  case CellType.STRING_FORMULA =>Literal(cell.asInstanceOf[StringFormulaCell].getString(), XSD_STRING)
	  case CellType.DATE => val xsdDateFormater = new SimpleDateFormat("yyyy-MM-dd")
                         Literal(xsdDateFormater.format(cell.asInstanceOf[DateCell].getDate()), XSD_DATE)
	  case CellType.DATE_FORMULA =>val xsdDateFormater = new SimpleDateFormat("yyyy-MM-dd")
                               Literal(xsdDateFormater.format(cell.asInstanceOf[DateFormulaCell].getDate()), XSD_DATE)
    case CellType.EMPTY => autodetectFormat(cell.getContents)
	
	}
    }

  override def getStyle : CellStyle ={

    val format = cell.getCellFormat

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
  }

    
}
