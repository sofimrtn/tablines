package es.ctic.tabels

import es.ctic.tabels.Dimension._
import grizzled.slf4j.Logging
import collection.JavaConversions._
import au.com.bytecode.opencsv.CSVReader
import java.io.{File,FileReader}
import scala.collection.mutable.HashMap

class CSVDataSource(fl : Seq[File]) extends DataSource with Logging {

    private val files : Seq[File] = fl
	
	val filenames : Seq[String] = files.map(_.getName())
	
	private val tables = new HashMap[File, CSVTable]
	
	private def getTable(file : File) : CSVTable = {
        tables.get(file) match {
            case Some(reader) => return reader
            case None =>
                logger.info("Reading CSV file " + file)
                val reader = new CSVReader(new FileReader(file))
                val table = CSVTable(reader.readAll())
        	    tables.put(file, table)
    	        return table
        }
	}
	
    override def getValue(point : Point) : CellValue = getTable(new File(point.path)).getCSVCellValue(point.row, point.col)
  
    override def getTabs(filename : String) : Seq[String] = Seq("")

    override def getRows(filename : String, tabName : String) : Int = getTable(new File(filename)).rows
    
    override def getCols(filename : String, tabName : String) : Int = getTable(new File(filename)).cols
  
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
  
    override def getContent : Literal = Literal(value) // FIXME: types
  
}