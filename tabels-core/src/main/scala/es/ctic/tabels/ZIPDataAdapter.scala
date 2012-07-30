package es.ctic.tabels

import java.io.File
import grizzled.slf4j.Logging

/**
 * Created with IntelliJ IDEA.
 * User: Guillermo Gonzalez-Moriyon
 * Date: 7/30/12
 * Time: 12:51 PM
 */

/** Handles ZIP files. Useful for shp zipped files.
 *
 *
 * @author Guillermo Gonzalez-Moriyon
 *
 */
class ZIPDataAdapter(file: File) extends DataAdapter with Logging {


  override val uri = file.getCanonicalPath()
  override def getTabs(): Seq[String] = Seq("")
  override def getRows(tabName: String = ""): Int = 1
  override def getCols(tabName: String = ""): Int = 1
  override def getValue(point: Point): CellValue = null


}
