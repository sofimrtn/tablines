package es.ctic.tabels

import java.io.{File,FileNotFoundException}
import scala.collection.mutable.HashMap
import collection.JavaConversions._
import org.odftoolkit.odfdom.doc.{OdfSpreadsheetDocument, table, OdfDocument}

import org.odftoolkit.odfdom.doc.table._
import java.util.Arrays
import es.ctic.tabels.Dimension._
import grizzled.slf4j.Logging
import javax.xml.xpath.XPathFactory
import javax.xml.xpath.XPathConstants

import org.w3c.dom._
import javax.xml.xpath._



class ODFDataAdapter(file : File) extends DataAdapter with Logging {
    //FIXME
	val ws =try {
				OdfSpreadsheetDocument.loadDocument(file)
				
			} catch {
            case e : FileNotFoundException =>
                logger.error("While reading Excel file " + file.getCanonicalPath, e)
                throw new NoInputFiles
            case e : Exception =>
              logger.error("While reading Excel file " + file.getCanonicalPath, e)
              throw new InvalidInputFileCannotReadOds(file.getName)
	   }
	
	
 
  private val workbook = ws
  
  override val uri = file.getCanonicalPath()

  override def getValue(point : Point) : CellValue = {
	logger.trace("Getting value at " + point)
	try{
		val sheet  = workbook.getTableByName(point.tab)
		val cell  = sheet.getCellByPosition(point.col, point.row)
	    return ODFCellValue(cell)
	}catch{
	  case e=>throw new IndexOutOfBounds(point)
	}
  }
  
  override def getTabs() : Seq[String] = {
    
    val sheetCount = workbook.getTableList()
    val sheetNames = sheetCount map(s => s.getTableName)
    return sheetNames
  }
  
  override def getRows(tabName : String) : Int = {
    
    val odt =  OdfDocument.loadDocument(uri);
    val dom = odt.getContentDom();
    val xpath = dom.getXPath
  	val sheet = workbook.getTableByName(tabName)
  	val xExpression = xpath.compile("//table:table[@table:name = '"+ tabName +"']/table:table-row/@table:number-rows-repeated")
 	
  	return sheet.getRowCount - xExpression.evaluate(dom,XPathConstants.NODESET).asInstanceOf[NodeList].item(0).getNodeValue.toInt - 1
  }
  
  override def getCols(tabName : String) : Int = {
    val odt =  OdfDocument.loadDocument(uri);
    val dom = odt.getContentDom();
    val xpath = dom.getXPath
  	val sheet = workbook.getTableByName(tabName)
  	val xExpression = xpath.compile("//table:table[@table:name = '"+ tabName +"']/table:table-column/@table:number-columns-repeated")
 	
  	return sheet.getColumnCount - xExpression.evaluate(dom,XPathConstants.NODESET).asInstanceOf[NodeList].item(0).getNodeValue.toInt
  }

}

case class ODFCellValue (cell : OdfTableCell) extends CellValue with Logging {
    
    val decimalFormatPattern = """^(?:#,##)?0(?:\.0+)?(?:;.*)?$""".r
    
  override def getContent : Literal ={
    logger.trace("Actual cell type is: " + cell.getValueType().toString() + " and its value is: "+cell.getStringValue)
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

