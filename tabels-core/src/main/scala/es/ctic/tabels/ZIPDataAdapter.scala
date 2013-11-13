package es.ctic.tabels

import java.io._
import grizzled.slf4j.Logging
import scala.collection.JavaConversions._


/**
 * Created with IntelliJ IDEA.
 * User: Guillermo Gonzalez-Moriyon
 * Date: 7/30/12
 * Time: 12:51 PM
 */

/** Handles ZIP files. Looks for dbf inside zip and processes it using DBFDataAdapter. Useful for shp zipped files.
 *
 *
 * @author Guillermo Gonzalez-Moriyon
 *
 */
class ZIPDataAdapter(file: File) extends DataAdapter with Logging {

  logger.trace("Starting to process file: "+file.getAbsolutePath)

  val fileAsZip = new java.util.zip.ZipFile(file)
  val dbfFile = File.createTempFile("dbf-in-zip",".dbf")
  val zipContainsDBF = fileAsZip.entries.find(_.getName.endsWith(".dbf")).
    foreach(e => new ZipDeflater().stream(fileAsZip.getInputStream(e), new FileOutputStream(dbfFile)))

  // FIXME Return error when dbfFile is empty
  val adapter: DBFDataAdapter = new DBFDataAdapter(dbfFile)

  override val uri = file.getCanonicalPath()
  override def getTabs(): Seq[String] = adapter.getTabs()
  override def getRows(tabName: String = ""): Int = adapter.getRows(tabName)
  override def getCols(tabName: String = ""): Int = adapter.getCols(tabName)
  override def getValue(point: Point): CellValue = adapter.getValue(point)
}
