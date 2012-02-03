package es.ctic.tabels

import java.io.{File,FileNotFoundException}
import scala.collection.mutable.HashMap
import collection.JavaConversions._
import org.jopendocument.dom._
import java.util.Arrays
import es.ctic.tabels.Dimension._
import grizzled.slf4j.Logging
import org.jopendocument.dom.spreadsheet.{SpreadSheet,Sheet,Cell,MutableCell}
import org.jopendocument.dom.ODValueType
import java.math.BigDecimal


class ODFDataAdapter(file : File) extends DataAdapter with Logging {
    //FIXME
	val ws =try {
				SpreadSheet.createFromFile(file)
			} catch {
            case e : FileNotFoundException =>
                logger.error("While reading Excel file " + file.getCanonicalPath, e)
                throw new NoInputFiles
            
	   }
	
	
 
  private val workbook = ws/*openWorkbook(file)
	
	
	private def openWorkbook(file : File) : Workbook = {
        try {
            return Workbook.getWorkbook(file,ws)
        } catch {
            case e : FileNotFoundException =>
                logger.error("While reading Excel file " + file.getCanonicalPath, e)
                throw new NoInputFiles
            case e : BiffException =>
                logger.error("While reading Excel file " + file.getCanonicalPath, e)
                throw new InvalidInputFile(file.getName)
	   }
	}*/
  
  override val uri = file.getCanonicalPath()

  override def getValue(point : Point) : CellValue = {
	logger.trace("Getting value at " + point)
	try{
		val sheet  = workbook.getSheet(point.tab)
		val cell  : MutableCell[SpreadSheet]= sheet.getCellAt(point.col, point.row)
	    return ODFCellValue(cell)
	}catch{
	  case e=>throw new IndexOutOfBounds(point)
	}
  }
  
  override def getTabs() : Seq[String] = {
    
    val sheetCount = workbook.getSheetCount()
    val sheetNames = (Array.iterate("0",sheetCount)(start => (start.toInt+1).toString())) map(s => workbook.getSheet(s.toInt).getName )
    return sheetNames
  }
  
  override def getRows(tabName : String) : Int = {
    val sheet : Sheet = workbook.getSheet(tabName)
    return sheet.getRowCount()
  }
  
  override def getCols(tabName : String) : Int = {
    val sheet : Sheet = workbook.getSheet(tabName)
    return sheet.getColumnCount()
  }

}
import java.math.BigDecimal
case class ODFCellValue (cell : MutableCell[SpreadSheet]) extends CellValue with Logging {
    
    val decimalFormatPattern = """^(?:#,##)?0(?:\.0+)?(?:;.*)?$""".r
    
  override def getContent : Literal ={
    logger.trace("Actual cell type is: " + cell.getValueType().toString() + " and its value is: "+cell.getTextValue())
    cell.getValueType() match{
	  case ODValueType.FLOAT  => val value = cell.getValue()
	  						if(value.asInstanceOf[BigDecimal].doubleValue.ceil.toInt == value.asInstanceOf[BigDecimal].intValue)
	  							Literal(value.asInstanceOf[BigDecimal].intValue, XSD_INT)
	  						else Literal(value.toString, XSD_DECIMAL)
	  case ODValueType.STRING =>Literal(cell.getTextValue(), XSD_STRING)
	  case ODValueType.BOOLEAN => Literal(cell.getValue().asInstanceOf[Boolean], XSD_BOOLEAN)
	  case ODValueType.DATE =>Literal(cell.getTextValue(), XSD_DATE)
	  case _ => autodetectFormat(cell.getTextValue())
	
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

