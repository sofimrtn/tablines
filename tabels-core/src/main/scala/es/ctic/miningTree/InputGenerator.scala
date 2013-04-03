package es.ctic.miningTree

import java.io.{FileWriter, File}
import grizzled.slf4j.Logging
import es.ctic.tabels._
import es.ctic.tabels.Point


case class InputGenerator(outputPath: Option[String] = None ) extends Logging{


  //FIXME: Just reading Excel files
  //Read files from input dir
  //val files = new File(sourcePath).listFiles.filter(_.getName.endsWith(".xls"))

  def generateInputData(dataAdapters:Seq[DataAdapter]) ={
    //Iterate through each cell of the data adapters running cell-test and writing results into the file
    dataAdapters.foreach(adapter =>
      adapter.getTabs.foreach(tab =>{
        //Open Results file and write variable names
        val writer = new FileWriter(outputPath.getOrElse("")+adapter.uri.replaceAll(".*/", "") + tab +"_" + adapter.getRows(tab)+"_"+ adapter.getCols(tab)+".txt")
        writer.append("unmatchesStyle, unmatchesType, isText, hasBlankSurround, isBorderLineCell, isBlank, isHeader\n")
        writer.flush
        (0 until adapter.getRows(tab)).foreach(row =>
          (0 until adapter.getCols(tab)).foreach(col=> runCellTest(adapter, adapter.getValue(Point(adapter.uri,tab,col,row)),Point(adapter.uri,tab,col,row),writer))
        )
        writer.close()/*writer.append("\n")*/}))
    //writer.close()
  }

  def runCellTest(adapter:DataAdapter, cell:CellValue, point:Point, writer:FileWriter) = {

    writer.append(unmatchesStyle(adapter, cell, point).toString)
    writer.append(", ")

    writer.append(unmatchesType(adapter, cell, point).toString)
    writer.append(", ")

    writer.append(isText(cell).toString)
    writer.append(", ")

    writer.append(hasBlankSurround(adapter, cell, point).toString)
    writer.append(", ")

    writer.append(isBorderLineCell(adapter, point).toString)
    writer.append(", ")

    writer.append(isBlank(cell).toString)
    /* writer.append(", ")

    writer.append(cell.getContent.value.toString)
    writer.append(", ")

    writer.append(point.row.toString +"_" +point.col.toString )
    writer.append(", ")

    writer.append("cgColor: "+cell.getStyle.backGColor+ "border" +cell.getStyle.border_top+"_"+cell.getStyle.border_right+"_"+cell.getStyle.border_bottom+"_"+cell.getStyle.border_left+ "font "+cell.getStyle.font  )
    */
    writer.append("\n")
    writer.flush
  }

  def isText(cell:CellValue) : Boolean= cell.getContent.rdfType == XSD_STRING  &&   cell.getContent.value != ""

  def hasBlankSurround(adapter:DataAdapter, cell:CellValue, point:Point) : Boolean= {

    val contextCells =  getContextCells(adapter:DataAdapter , point:Point).filter( cell => cell.getContent.value=="")
    contextCells.length>0
  }

  def unmatchesStyle(adapter:DataAdapter, cell:CellValue, point:Point) : Boolean= {

    //Build a "list" with the style of each surrounding cell
    val contextCellStyles = getContextCells(adapter:DataAdapter , point:Point).map(contextCell => contextCell.getStyle)
    val cellStyle = cell.getStyle

    contextCellStyles.fold(false)((diferentStyle,contextCellStyle)=> (cellStyle!=contextCellStyle)||diferentStyle.asInstanceOf[Boolean]).asInstanceOf[Boolean]
  }

  def unmatchesType (adapter:DataAdapter, cell:CellValue, point:Point) : Boolean= {

    //Build a "list" with the Type of each surrounding cell
    val contextCellTypes = getContextCells(adapter:DataAdapter , point:Point).map(contextCell => contextCell.getContent.rdfType)
    val cellType = cell.getContent.rdfType

    //Cheking if any surrounding cell has a different type
    contextCellTypes.fold(false)((diferentType,contextCellType)=> (cellType!=contextCellType)||diferentType.asInstanceOf[Boolean]).asInstanceOf[Boolean]
  }

  def isBorderLineCell(adapter:DataAdapter, point:Point):Boolean = (getContextCells(adapter, point).length<4)

  def getContextCells (adapter:DataAdapter, point:Point) : Seq[CellValue] = {

    val contexPoints = Seq(point.TopPoint, point.RightPoint, point.BottomPoint, point.LeftPoint)
    //Filter those context points out of bounds and get cellValue for each inbound point
    (contexPoints.filter(cPoint => (cPoint.col>=0 && cPoint.row>=0 && cPoint.row<adapter.getRows(point.tab) && cPoint.col<adapter.getCols(point.tab)))).map(validPoint => adapter.getValue(validPoint))
  }


  def isBlank(cell:CellValue) : Boolean =  cell.getContent.value==""
}