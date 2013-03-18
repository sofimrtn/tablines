package es.ctic.tabels

import es.ctic.tabels.Dimension._
import grizzled.slf4j.Logging
import collection.JavaConversions._

import java.io.{File,FileReader}
import scala.collection.mutable.HashMap

class PXDataAdapter(file : File) extends DataAdapter with Logging {

	private val tables : (PXTable,PXTable) = readTables(file)
	
	private def readTables(file : File) : (PXTable,PXTable) = {
        logger.info("Reading PX file " + file)
        val reader = new PXReader(file)
        return (PXTable(reader.readAll),PXTable(reader.readMetaData))
	}
	
	override val uri = file.getCanonicalPath()
    override def getValue(point : Point) : CellValue ={point.tab match{
      																case "Contents" => tables._1 getPXCellValue(point)
      																case "MetaData" => tables._2 getPXCellValue(point)
      																case _ => throw new InvalidInputTab(point.tab)
      																  }
    }
    override def getTabs() : Seq[String] = Seq("Contents","MetaData")
    override def getRows(tabName : String) : Int ={tabName match{
      																case "Contents" => tables._1 rows
      																case "MetaData" => tables._2 rows
      																case _ => throw new InvalidInputTab(tabName)
      																  }
    }
      
    override def getCols(tabName : String) : Int = {tabName match{
      																case "Contents" => tables._1 cols
      																case "MetaData" => tables._2 cols
      																case _ => throw new InvalidInputTab(tabName)
      																  }
    												}
}
case class PXTable(matrix : Seq[Array[String]]) {
    
    val rows : Int = matrix.size
    
    val cols : Int = matrix map (_.size) max
    
    def getPXCellValue(point:Point) : PXCellValue =
        if (point.col < matrix(point.row).size)
            return PXCellValue(matrix(point.row)(point.col))
        else{
            throw new IndexOutOfBounds(point)
            }
    
}

case class PXCellValue(value : String) extends CellValue {
  
    override def getContent : Literal = autodetectFormat(value)
    override def getStyle : CellStyle = CellStyle()
}