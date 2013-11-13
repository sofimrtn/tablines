package es.ctic.tabels

import java.io.{File, FileNotFoundException, FileInputStream}
import com.linuxense.javadbf._

import grizzled.slf4j.Logging


/** Handles DBF files. This class loads the complete DBF on memory.
  * 
  * @author Alfonso Noriega
  * @author Guillermo Gonzalez-Moriyon
  * 
  */
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
  val dataMatrix = (1 until getRows()).map(record =>{logger.trace("record" + record);reader.nextRecord()})
  logger.trace("Number of rows in dataMatrix: "+ dataMatrix length)

  override val uri = file.getCanonicalPath()
  override def getTabs(): Seq[String] = Seq("")
  override def getRows(tabName: String = ""): Int = reader.getRecordCount() + 1
  override def getCols(tabName: String = ""): Int = reader.getFieldCount()

  /** Gets the value of a cell as a CellValue.
    * @param point current cell coordinates
    * @return the cell as CellValue
    */
  override def getValue(point: Point): CellValue = {
    logger.trace("Getting value at " + point)
    try {

      // If header use the header information
      if (point.row == 0) {
        return DBFCellValue(headers(point.col))
      } else {
        
        val cell = dataMatrix(point.row - 1) apply point.col
        logger.trace ("cell type is "+cell.getClass.getCanonicalName+" but field is "+reader.getField(point.col).getDataType)
        return DBFCellValue(cell)
      }
    } catch {
      case e => throw new IndexOutOfBounds(point)
    }
  }
}

/** Contains the value of a cell for a DBF file.
  * 
  *
  */
case class DBFCellValue(cell: Object) extends CellValue with Logging {

  override def getContent: Literal = {
    
    logger.trace("Parsing cell "+cell)
    cell match {
      
      case cell:java.lang.String => Literal(cell.trim, XSD_STRING)
      case cell:java.lang.Integer => Literal(cell, XSD_INT)
      case cell:java.lang.Double => Literal(cell, XSD_DOUBLE)
      case cell:java.util.Date => Literal(cell.toString, XSD_DATE)
      case cell:java.lang.Boolean => Literal(cell, XSD_BOOLEAN)
      case x =>
        logger.info("Unrecognized cell format: '" + x + "'")
          autodetectFormat(cell.toString)
    }
  }
}