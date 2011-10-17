package es.ctic.tabels

import java.io.File
import scala.collection.mutable.HashMap
import jxl._
import java.util.Arrays

import es.ctic.tabels.Dimension._
import grizzled.slf4j.Logging


class ExcelDataSource(fl : Seq[File]) extends DataSource with Logging {
  
	private val files : Seq[File] = fl
	
	val filenames : Seq[String] = files.map(_.getName())
	
	private val workbooks = new HashMap[File, Workbook]()
	
	private def getWorkbook(file : File) : Workbook = {
	   workbooks.get(file) match {
	     case Some(workbook) => return workbook
	     case None => val workbook = Workbook.getWorkbook(file)
	                  workbooks.put(file, workbook)
	                  return workbook
	   }
	}
  
  override def getValue(point : Point) : CellValue = {
	logger.debug("Getting value at " + point)
    val workbook : Workbook = getWorkbook(new File (point.path) )
	val sheet : Sheet = workbook.getSheet(point.tab)
	val cell : Cell = sheet.getCell(point.col, point.row)
    return ExcelCellValue(cell)
  }
  
  override def getTabs(filename : String) : Seq[String] = {
	 
    val workbook : Workbook = getWorkbook(new File (filename) )
    val sheetNames : Array[String] = workbook.getSheetNames()
    val listSheets : java.util.List[String] = new java.util.LinkedList()
    sheetNames.foreach(sheet => listSheets.add(sheet))
    return scala.collection.JavaConversions.asScalaBuffer(listSheets)
}
  
  override def getRows(filename : String, tabName : String) : Int = {
    
    val workbook : Workbook = getWorkbook(new File (filename))
    val sheet : Sheet = workbook.getSheet(tabName)

    return sheet.getRows()
}
  override def getCols(filename : String, tabName : String) : Int = {
    
    val workbook : Workbook = getWorkbook(new File (filename))
    val sheet : Sheet = workbook.getSheet(tabName)

    return sheet.getColumns()
}
  override def getDimensionRange(dimension : Dimension, evaluationContext:EvaluationContext):Seq[String] = {
	
	
    dimension match{
      case Dimension.files => filenames
      
      case _ => val workbook : Workbook = getWorkbook(new File (evaluationContext.dimensionMap(Dimension.files)) )
      			val sheetNames : Array[String] = workbook.getSheetNames()
      				
      			var dim: scala.collection.mutable.Seq[String] = scala.collection.mutable.Seq()
      			return dimension match{
      
				  case Dimension.sheets => val listSheets : java.util.List[String] = new java.util.LinkedList()
				      							sheetNames.foreach(sheet => listSheets.add(sheet))
				      							return scala.collection.JavaConversions.asScalaBuffer(listSheets)
				  case Dimension.rows =>val sheet : Sheet = workbook.getSheet(evaluationContext.dimensionMap(Dimension.sheets)) 
				        					for( x <- 0 until sheet.getRows()){
				    	  						dim = dim :+ x.toString()
				    	  						}
				      						return dim
				  case Dimension.cols =>val sheet : Sheet = workbook.getSheet(evaluationContext.dimensionMap(Dimension.sheets)) 
				      						for( x <- 0 until sheet.getColumns()){
				    	  						dim = dim :+ x.toString()
				    	  						}
				      						return dim
					}
    }
  }

}
  
case class ExcelCellValue (cell : Cell) extends CellValue with Logging {
    
    val decimalPattern = """[0-9]*\.[0-9]+""".r
    val intPattern = """[0-9]+""".r
    val decimalFormatPattern = """^(?:#,##)?0(?:\.0+)?(?:;.*)?$""".r
    
  override def getContent : Literal =
    if (cell.getCellFormat == null) Literal(cell.getContents)      
    else cell.getCellFormat.getFormat.getFormatString match {
        case "" => autodetectFormat
        case "@" => Literal(cell.getContents, XSD_STRING)
        case "0" => Literal(cell.getContents, XSD_INT)
        case decimalFormatPattern() => Literal(cell.getContents, XSD_DECIMAL)
        
        case str if str endsWith "%" => Literal(cell.getContents.dropRight(1), XSD_DECIMAL)
        case str if str contains "$" => Literal(cell.getContents.drop(1), XSD_DECIMAL)
        case "d-mmm-yy" => Literal(cell.getContents, XSD_DATE) // FIXME: parse date
        case x => logger.info("Unrecognized cell format: '" + x + "'")
                  return Literal(cell.getContents, XSD_STRING)
    }
    
    /**
     * When there is no formatting information, this method does it
     * best to parse the cell value
     *
     */
    def autodetectFormat : Literal = cell.getContents match {
        case decimalPattern() => Literal(cell.getContents, XSD_DECIMAL)
        case intPattern() => Literal(cell.getContents, XSD_INT)
        case x => Literal(cell.getContents, XSD_STRING)
    }
    
}
