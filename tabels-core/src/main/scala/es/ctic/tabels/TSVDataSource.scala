package es.ctic.tabels

import grizzled.slf4j.Logging
import collection.JavaConversions._
import au.com.bytecode.opencsv.CSVReader
import java.io.{File,FileReader,FileNotFoundException}

/**
 * Project: tabels-core
 * User: Alfonso Noriega Meneses <alfonso.noriega@fundacionctic.org>
 * Date: 17/12/13
 */



class TSVDataAdapter(file : File) extends DataAdapter with Logging {

  private val table : TSVTable = try
  {
    readTable(file)
  }catch {
    case e : FileNotFoundException =>
      logger.error("While reading TSV file " + file.getCanonicalPath, e)
      throw new NoInputFiles
    case e : Exception =>
      logger.error("While reading TSV file " + file.getCanonicalPath, e)
      throw new InvalidInputFileCannotReadTSV(file.getName)
  }

  private def readTable(file : File) : TSVTable = {
    logger.info("Reading TSV file " + file)
    val reader = new CSVReader(new FileReader(file),'\t')
    return TSVTable(reader.readAll())
  }

  override val uri = file.getCanonicalPath()
  override def getValue(point : Point) : CellValue = table.getTSVCellValue(point.row, point.col)
  override def getTabs() : Seq[String] = Seq("")
  override def getRows(tabName : String) : Int = table.rows
  override def getCols(tabName : String) : Int = table.cols

}

case class TSVTable(matrix : Seq[Array[String]]) {

  val rows : Int = matrix.size

  val cols : Int = matrix map (_.size) max

  def getTSVCellValue(row : Int, col : Int) : TSVCellValue =
    if (col < matrix(row).size)
      return TSVCellValue(matrix(row)(col))
    else
      return TSVCellValue("")

}

case class TSVCellValue(value : String) extends CellValue {

  override def getContent : Literal = autodetectFormat(value)
}