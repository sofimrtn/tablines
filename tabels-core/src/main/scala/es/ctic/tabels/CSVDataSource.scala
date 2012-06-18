package es.ctic.tabels

import es.ctic.tabels.Dimension._
import grizzled.slf4j.Logging
import collection.JavaConversions._
import au.com.bytecode.opencsv.CSVReader
import java.io.{File,FileReader}
import scala.collection.mutable.HashMap

class CSVDataAdapter(file : File) extends DataAdapter with Logging {

	private val table : CSVTable = readTable(file)
	
	private def readTable(file : File) : CSVTable = {
        logger.info("Reading CSV file " + file)
        val reader = new CSVReader(new FileReader(file))
        return CSVTable(reader.readAll())
	}
	
	override val uri = file.getCanonicalPath()
    override def getValue(point : Point) : CellValue = table.getCSVCellValue(point.row, point.col)
    override def getTabs() : Seq[String] = Seq("")
    override def getRows(tabName : String) : Int = table.rows
    override def getCols(tabName : String) : Int = table.cols
  
}

case class CSVTable(matrix : Seq[Array[String]]) {
    
    val rows : Int = matrix.size
    
    val cols : Int = matrix map (_.size) max
    
    def getCSVCellValue(row : Int, col : Int) : CSVCellValue =
        if (col < matrix(row).size)
            return CSVCellValue(matrix(row)(col))
        else
            return CSVCellValue("")
    
}

case class CSVCellValue(value : String) extends CellValue {
  
    override def getContent : Literal = autodetectFormat(value)
  
}