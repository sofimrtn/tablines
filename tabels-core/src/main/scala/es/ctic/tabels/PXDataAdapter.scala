package es.ctic.tabels

import es.ctic.tabels.Dimension._
import grizzled.slf4j.Logging
import collection.JavaConversions._

import java.io.{File,FileReader}
import scala.collection.mutable.HashMap

class PXDataAdapter(file : File) extends DataAdapter with Logging {

	private val table : PXTable = readTable(file)
	
	private def readTable(file : File) : PXTable = {
        logger.info("Reading PX file " + file)
        val reader = new PXReader(file)
        return PXTable(reader.readAll)
	}
	
	override val uri = file.getCanonicalPath()
    override def getValue(point : Point) : CellValue = table.getPXCellValue(point.row, point.col)
    override def getTabs() : Seq[String] = Seq("")
    override def getRows(tabName : String) : Int = table.rows
    override def getCols(tabName : String) : Int = table.cols
  
}

case class PXTable(matrix : Seq[Array[String]]) {
    
    val rows : Int = matrix.size
    
    val cols : Int = matrix map (_.size) max
    
    def getPXCellValue(row : Int, col : Int) : PXCellValue =
        if (col < matrix(row).size)
            return PXCellValue(matrix(row)(col))
        else
            return PXCellValue("")
    
}

case class PXCellValue(value : String) extends CellValue {
  
    override def getContent : Literal = autodetectFormat(value)
  
}