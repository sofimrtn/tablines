package es.ctic.tabels

import java.io.{File,FileNotFoundException,FileInputStream}
import scala.collection.mutable.HashMap
import collection.JavaConversions._
import com.linuxense.javadbf._

import java.util.Arrays
import es.ctic.tabels.Dimension._
import grizzled.slf4j.Logging
import javax.xml.xpath.XPathFactory
import javax.xml.xpath.XPathConstants

import org.w3c.dom._
import javax.xml.xpath._



class DBFDataAdapter(file : File) extends DataAdapter with Logging {
  

  val reader =try {	  
	  
				new DBFReader(new FileInputStream(file))			
				
			} catch {
            case e : FileNotFoundException =>
                logger.error("While reading DBF file " + file.getCanonicalPath, e)
                throw new NoInputFiles            
	   }
// TODO process header as the first row
  val headers = 0 until getFieldCount()-1.map(record => reader.getField(record).getName())
  val dataMatrix = 0 until getRows()-1.map(record => reader.nextRecord())
 

  

  override val uri = file.getCanonicalPath()
  override def getTabs() : Seq[String] = Seq("")
  override def getRows(tabName : String = "") : Int = reader.getRecordCount()+1
  override def getCols(tabName : String = "") : Int = reader.getFieldCount()

  override def getValue(point : Point) : CellValue = {
	logger.trace("Getting value at " + point)
	try{
	  
	  val sheet = reader.
	  
		val sheet  = workbook.getTableByName(point.tab)
		val field = reader.getField(point.col)
		val cell  = readersheet.getCellByPosition(point.col, point.row)
	    return DBFCellValue(cell,field)
	}catch{
	  case e=>throw new IndexOutOfBounds(point)
	}
  }
//  
//  override def getTabs() : Seq[String] = {
//    
//    
//    
//    val sheetFieldObjects =0 until reader.getFieldCount() map(tabIndex => reader.getField(tabIndex))
//    
//    val sheetNames = sheetFieldObjects map (tabObject => tabObject.getName())
//    return sheetNames
//  }
//  
  

}

case class DBFCellValue (cell : Object, field : Some[DBFField]) extends CellValue with Logging {
    
    val decimalFormatPattern = """^(?:#,##)?0(?:\.0+)?(?:;.*)?$""".r
    
  override def getContent : Literal ={
    logger.trace("Actual cell type is: " + cell.getValueType().toString() + " and its value is: "+cell.getStringValue)
    if(field.isEmpty){
      Literal(cell.toString, XSD_STRING)
    }
    else{
      field.get.getDataType match{
        // char
        case FIELD_TYPE_C =>
        // double
		case FIELD_TYPE_D =>
		// float 
		case FIELD_TYPE_F =>
		// long
		case FIELD_TYPE_L =>
		// 
		case FIELD_TYPE_M =>
		// number
		case FIELD_TYPE_N =>
		case x => logger.info("Unrecognized cell format: '" + x + "'")
	    		autodetectFormat(cell.getStringValue)  
      }
    }
    cell.getValueType() match{
	  case "float"  => val value = cell.getDoubleValue
	  						if(value.toInt == value)
	  							Literal(value.toInt, XSD_INT)
	  						else Literal(value.toString, XSD_DECIMAL)
	  case "string" =>Literal(cell.getStringValue, XSD_STRING)
	  case "boolean" => Literal(cell.getBooleanValue, XSD_BOOLEAN)
	  case "date" =>Literal(cell.getStringValue, XSD_DATE)
	  case "percentage" =>Literal(cell.getPercentageValue, XSD_DECIMAL)
	  case "currency" =>Literal(cell.getCurrencyValue, XSD_DECIMAL)
	  case x => logger.info("Unrecognized cell format: '" + x + "'")
	    		autodetectFormat(cell.getStringValue)
	
	}
    }
}

