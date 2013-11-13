package es.ctic.tabels

import grizzled.slf4j.Logging
import collection.JavaConversions._
import au.com.bytecode.opencsv.CSVReader
import java.io.{File,FileReader,FileNotFoundException}

class CSVDataAdapter(file : File) extends DataAdapter with Logging {
	
	private val table : CSVTable = try
							{
								readTable(file)
							}catch {
						            case e : FileNotFoundException =>
						                logger.error("While reading CSV file " + file.getCanonicalPath, e)
						                throw new NoInputFiles
						            case e : Exception =>
						                logger.error("While reading CSV file " + file.getCanonicalPath, e)
						                throw new InvalidInputFileCannotReadCSV(file.getName)
							   }
	
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