package es.ctic.tabels

import java.io.{File,FileNotFoundException}
import collection.JavaConversions._
import org.odftoolkit.odfdom.doc.OdfDocument

import org.odftoolkit.simple.SpreadsheetDocument
import org.odftoolkit.simple.table.Cell
import grizzled.slf4j.Logging

import org.w3c.dom._
import javax.xml.xpath._
import java.text.SimpleDateFormat


class ODFDataAdapter(file : File) extends DataAdapter with Logging {
	val ws =try {
        logger.trace("---Loaded ODF document")
				//OdfSpreadsheetDocument.loadDocument(file)
        SpreadsheetDocument.loadDocument(file)

			} catch {
            case e : FileNotFoundException =>
                logger.trace("While reading ods file " + file.getCanonicalPath, e)
                throw new NoInputFiles
            case e : Exception =>
              logger.trace("While reading ods file " + file.getCanonicalPath, e)
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
    logger.trace("Getting Tabs: " + sheetCount )
    return sheetNames
  }
  
  override def getRows(tabName : String) : Int = {
    
    val odt =  OdfDocument.loadDocument(uri)
    val dom = odt.getContentDom()
    val xpath = dom.getXPath
    val xExpression = xpath.compile("//table:table[@table:name = '"+ tabName +"']/table:table-row/@table:number-rows-repeated")
    val domRowNode = xExpression.evaluate(dom,XPathConstants.NODESET).asInstanceOf[NodeList].item(0)
    val rowRepeated = if (domRowNode != null) domRowNode.getNodeValue.toInt else 0

    val sheet = workbook.getTableByName(tabName)
    val rowCount = sheet.getRowCount

    logger.trace("row count " + rowCount  + " row repeated " + rowRepeated)

    rowCount - rowRepeated
  }
  
  override def getCols(tabName : String) : Int = {
    val odt =  OdfDocument.loadDocument(uri)
    val dom = odt.getContentDom()
    val xpath = dom.getXPath
  	val xExpression = xpath.compile("//table:table[@table:name = '"+ tabName +"']/table:table-column/@table:number-columns-repeated")
    val domRowNode = xExpression.evaluate(dom,XPathConstants.NODESET).asInstanceOf[NodeList].item(0)
    val columnRepeated = if (domRowNode != null) domRowNode.getNodeValue.toInt else 0

    val sheet = workbook.getTableByName(tabName)
    val columnCount = sheet.getColumnCount

    logger.trace("col count " + columnCount + " col repeated " +  columnRepeated)

    columnCount - columnRepeated
  }

}

case class ODFCellValue (cell : Cell) extends CellValue with Logging {
    
    val decimalFormatPattern = """^(?:#,##)?0(?:\.0+)?(?:;.*)?$""".r
    
  override def getContent : Literal ={
    logger.trace("Actual cell type is: " + cell.getValueType.toString() + " and its value is: "+cell.getStringValue)
    cell.getValueType match{
	  case "float"  => val value = cell.getDoubleValue
	  						if(value.toInt == value)
	  							Literal(value.toInt, XSD_INTEGER)
	  						else Literal(value.toString, XSD_DOUBLE)
	  case "string" =>Literal(cell.getStringValue, XSD_STRING)
	  case "boolean" => Literal(cell.getBooleanValue, XSD_BOOLEAN)
	  case "date" =>val xsdDateFormater = new SimpleDateFormat("yyyy-MM-dd")
                Literal(xsdDateFormater.format(cell.getDateValue.getTime), XSD_DATE)
	  case "percentage" =>Literal(cell.getPercentageValue, XSD_DECIMAL)
	  case "currency" =>Literal(cell.getCurrencyValue, XSD_DOUBLE)
	  case x => logger.info("Unrecognized cell format: '" + x + "'")
	    		autodetectFormat(cell.getStringValue)

	  }
  }

}

