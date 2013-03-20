package es.ctic.miningTree


import org.scalatest.junit.JUnitSuite
import org.junit.{Test, Before}
import org.junit.Assert._
import java.io.File
import es.ctic.tabels.{Point, DataAdapter, CellValue}

/**
 * Created with IntelliJ IDEA.
 * User: alfonso
 * Date: 19/02/13
 * Time: 10:51
 * To change this template use File | Settings | File Templates.
 */
class InputGeneratorTest extends JUnitSuite {
  var inputGenerator : InputGenerator = null
  val inputPath = this.getClass.getResource("/es/ctic/miningTree/").getFile.replace("%20"," ")

  var dataAdapter : DataAdapter = null
  val filename1 : String = this.getClass.getResource("/es/ctic/miningTree/Test1.xls").getFile.replace("%20"," ")
  val sheet1 = "Hoja1"

  @Before def setUp {
    inputGenerator = InputGenerator(inputPath)
    dataAdapter = DataAdapter.createAdapter(filename1)
  }
  @Test def generateInputDataTest{
    assertEquals(true,inputGenerator.generateInputData)
  }

  @Test def isTextTest{
    assertEquals(true,inputGenerator.isText(dataAdapter.getValue(Point(filename1,sheet1,col=0,row=0))))
    assertEquals(false,inputGenerator.isText(dataAdapter.getValue(Point(filename1,sheet1,col=0,row=1))))
    assertEquals(false,inputGenerator.isText(dataAdapter.getValue(Point(filename1,sheet1,col=0,row=7))))
    assertEquals(false,inputGenerator.isText(dataAdapter.getValue(Point(filename1,sheet1,col=1,row=9))))
  }

  @Test def hasBlankSurroundTest{
    assertEquals(true,inputGenerator.hasBlankSurround(dataAdapter,dataAdapter.getValue(Point(filename1,sheet1,col=0,row=7)),Point(filename1,sheet1,col=0,row=7)))
    assertEquals(true,inputGenerator.hasBlankSurround(dataAdapter,dataAdapter.getValue(Point(filename1,sheet1,col=0,row=7)),Point(filename1,sheet1,col=1,row=7)))
    assertEquals(false,inputGenerator.hasBlankSurround(dataAdapter,dataAdapter.getValue(Point(filename1,sheet1,col=0,row=7)),Point(filename1,sheet1,col=1,row=0)))
    assertEquals(true,inputGenerator.hasBlankSurround(dataAdapter,dataAdapter.getValue(Point(filename1,sheet1,col=0,row=7)),Point(filename1,sheet1,col=0,row=8)))
  }

  @Test def unmatchesStyleTest{
    assertEquals(false,inputGenerator.unmatchesStyle(dataAdapter,dataAdapter.getValue(Point(filename1,sheet1,col=1,row=3)),Point(filename1,sheet1,col=1,row=3)))
    assertEquals(true,inputGenerator.unmatchesStyle(dataAdapter,dataAdapter.getValue(Point(filename1,sheet1,col=1,row=0)),Point(filename1,sheet1,col=1,row=0)))
    //FIXME: Blank cells don't match in style
    assertEquals(false,inputGenerator.unmatchesStyle(dataAdapter,dataAdapter.getValue(Point(filename1,sheet1,col=1,row=7)),Point(filename1,sheet1,col=1,row=7)))
  }

  @Test def unmatchesTypeTest{
    assertEquals(true,inputGenerator.unmatchesType(dataAdapter,dataAdapter.getValue(Point(filename1,sheet1,col=0,row=7)),Point(filename1,sheet1,col=0,row=7)))
    assertEquals(true,inputGenerator.unmatchesType(dataAdapter,dataAdapter.getValue(Point(filename1,sheet1,col=1,row=0)),Point(filename1,sheet1,col=1,row=0)))
  }

  @Test def isBorderLineCellTest{
    assertEquals(true,inputGenerator.isBorderLineCell(dataAdapter,Point(filename1,sheet1,col=0,row=7)))
    assertEquals(true,inputGenerator.isBorderLineCell(dataAdapter,Point(filename1,sheet1,col=1,row=0)))
  }

  @Test def getContextCellsTest{
    assertEquals(Seq(dataAdapter.getValue(Point(filename1,sheet1,col=0,row=6)),dataAdapter.getValue(Point(filename1,sheet1,col=1,row=7)),dataAdapter.getValue(Point(filename1,sheet1,col=0,row=8))),inputGenerator.getContextCells(dataAdapter,Point(filename1,sheet1,col=0,row=7)))
    assertEquals(Seq(dataAdapter.getValue(Point(filename1,sheet1,col=1,row=0)),dataAdapter.getValue(Point(filename1,sheet1,col=0,row=1))),inputGenerator.getContextCells(dataAdapter,Point(filename1,sheet1,col=0,row=0)))
  }
}
