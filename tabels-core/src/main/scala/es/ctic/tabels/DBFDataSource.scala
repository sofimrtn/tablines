package es.ctic.tabels

import java.io.{ File, FileNotFoundException, FileInputStream }
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

class DBFDataAdapter(file: File) extends DataAdapter with Logging {

  val reader = try {

    new DBFReader(new FileInputStream(file))

  } catch {
    case e: FileNotFoundException =>
      logger.error("While reading DBF file " + file.getCanonicalPath, e)
      throw new NoInputFiles
  }
  // process header as the first row
  logger.trace("Number of fields: "+getCols())
  val headers = (0 until getCols()).map(record => reader.getField(record).getName())
  logger.trace("Fields: "+ headers)
  val dataMatrix = (1 until getRows()).map(record => reader.nextRecord())
  logger.trace("Number of rows in dataMatrix: "+ dataMatrix length)
  override val uri = file.getCanonicalPath()
  override def getTabs(): Seq[String] = Seq("")
  override def getRows(tabName: String = ""): Int = reader.getRecordCount() + 1
  override def getCols(tabName: String = ""): Int = reader.getFieldCount()

  override def getValue(point: Point): CellValue = {
    logger.trace("Getting value at " + point)
    try {

      // If header use the header information
      if (point.row == 0) {
        return DBFCellValue(headers(point.col))
      } else {
        
        val cell = dataMatrix(point.row - 1) apply point.col
        return DBFCellValue(cell)
      }
    } catch {
      case e => throw new IndexOutOfBounds(point)
    }
  }
}

case class DBFCellValue(cell: Object) extends CellValue with Logging {

  override def getContent: Literal = {
    
    logger.trace("Parsing cell "+cell)
    cell match {
      
      case cell:java.lang.String => Literal(cell, XSD_STRING)
      case cell:java.lang.Integer => Literal(cell, XSD_INT)
      case cell:java.lang.Double => Literal(cell, XSD_DECIMAL)
      case cell:java.util.Date => Literal(cell.toString, XSD_DATE)
      case cell:java.lang.Boolean => Literal(cell, XSD_BOOLEAN)
      case x =>
        logger.info("Unrecognized cell format: '" + x + "'")
          autodetectFormat(cell.toString)
    }
  }
}