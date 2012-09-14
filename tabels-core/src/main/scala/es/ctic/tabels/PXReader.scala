package es.ctic.tabels

import java.io.{File,FileReader,BufferedReader}
import grizzled.slf4j.Logging
import scala.io._
import scala.io.Codec


class PXReader(file : File)/* with Logging */{
  
  private val s = Source.fromFile(file,"latin1"/*Codec.defaultCharsetCodec.name*/)
  private val stringSource = s.getLines.mkString("\n")
  
  private def readKeyWord(keyWord:String)={
   stringSource.drop(stringSource.indexOf(keyWord)).split("""; *\n*[A-Z\-]+ *=|; *\n*[A-Z]+\(.*\) *=""",2)(0).replace("=\n","=").drop(stringSource.drop(stringSource.indexOf(keyWord)).indexOf("=")+1).split("\" +\"|\" *, *\"|\" *,\n *\"|\" *\n, *\"").map(_.replaceAll("\"","")).toSeq
  }
 
  def readHeadings = {
	  readKeyWord("HEADING")
  }
  def readStub = {
	  readKeyWord("STUB")
     
  }
  def readUnits = {
	  Seq(readKeyWord("UNITS").foldRight("")((result,part)=>result+part).replaceAll("\"|\n|\t","").trim)
  }
  def readTitle = {
	  Seq(readKeyWord("TITLE").foldRight("")((result,part)=>result+part).replaceAll("\"|\n|\t","").trim)
  }
  def readSource = {
	  Seq(readKeyWord("SOURCE").foldRight("")((result,part)=>result+part).replaceAll("\"|\n|\t","").trim)
  }
  def readContent = {
	  Seq(readKeyWord("CONTENT").foldRight("")((result,part)=>result+part).replaceAll("\"|\n|\t","").trim)
  }
  
  def readValues(keyWord:String) = {
	  readKeyWord("VALUES(\""+keyWord+"\")")
  }
  
  def getDataColsSize : Int ={
	  return readHeadings.foldLeft(1)((result,head)=>result*readValues(head).size)
  }
  
  def getDataRowsSize : Int ={
	  return readStub.foldLeft(1)((result,stub)=>result*readValues(stub).size)
  }
  def readData:Seq[Array[String]] = {
	  val array = (stringSource.drop(stringSource.indexOf("DATA")).replaceAll(";","").replace("=\n","=").drop(stringSource.drop(stringSource.indexOf("DATA")).indexOf("=")+1).replaceAll(" *\n *| +"," ")).trim.split(" ")
	  return (0 until getDataRowsSize).map(element=>array.take((element+1)*getDataColsSize).drop(element*getDataColsSize))
  }
  
  def calculateHeaders(dim:Int) ={
	  val dimensions = readHeadings
	  if (dim < dimensions.length)
	  {
		  val headers = readValues(dimensions(dim))
		  val dimPrev = (0 until dim).foldLeft(1)((length, index) =>length * readValues(dimensions(index)).length)
		  val dimPost = (dim+1 until dimensions.length).foldLeft(1)((length, index) =>length * readValues(dimensions(index)).length)
		  ((0 until readStub.length).map(void => "")++((0 until dimPrev).map (element => 
        		headers.map( head => 
        			(0 until dimPost) map(x => head+"@"+dimensions(dim))).flatten).flatten)).toArray[String]
       
    }
  }
  
  def calculateStubs(elementIndex:Int) ={
	  val stubs = readStub
	  stubs.map(head =>if(stubs.indexOf(head)<stubs.length-1) readValues(head)(elementIndex / (stubs.indexOf(head)+1 until stubs.length).foldLeft(1)((result,index)=>result * readValues(stubs(index)).length))+"@"+head
        		 	   else readValues(head)(elementIndex % readValues(head).length)+"@"+head
        		).toArray[String]
  }
  
  def  readAll :Seq[Array[String]] ={
    val numRows = readHeadings.length + getDataRowsSize
    val numCols =readStub.length + getDataColsSize
    //println(".......DIMENSIONES->Filas: "+numRows+" Columnas: "+numCols)
    
    val pxHeadings: Seq[Array[String]] = readHeadings map(dimension => calculateHeaders(readHeadings.indexOf(dimension)).asInstanceOf[Array[String]])
    val pxContent: Seq[Array[String]] = ((0 until getDataRowsSize) map (stubIndex => calculateStubs(stubIndex))).toList.zip(readData).map(pair => pair._1++pair._2)
    val pxTable=pxHeadings++pxContent
    //pxTable.foreach(row => {row.foreach(pos=> print(" -"+pos+"- "));println("||")})
    return pxTable
    }
  
  def readMetaData:Seq[Array[String]]={return Seq(("Title"+:readTitle) toArray,("Contents"+:readContent) toArray,("Units"+:readUnits) toArray, ("Source"+:readSource) toArray)}
}