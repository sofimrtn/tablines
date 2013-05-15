package es.ctic.miningTree

import org.scalatest.junit.JUnitSuite
import org.junit.{Test, Before}
import org.junit.Assert._
import es.ctic.tabels._
import java.io.File
import es.ctic.tabels.CellStyle
import es.ctic.tabels.Point


/**
 * Created with IntelliJ IDEA.
 * User: alfonso
 * Date: 14/05/13
 * Time: 11:46
 * To change this template use File | Settings | File Templates.
 */
class clasificationModelTest extends JUnitSuite {

  var dataAdapter : DataAdapter = null
  val filename1 : String = this.getClass.getResource("/es/ctic/miningTree/Test1.xls").getFile.replace("%20"," ")
  val sheet1 = "Hoja1"
  val point = new Point(filename1,sheet1,0,0)

  @Before def setUp {
    dataAdapter = DataAdapter.createAdapter(filename1)
    ClassificationModel.createModel(new File (this.getClass.getResource("/es/ctic/miningTree/").getFile.replace("%20"," ")))
  }
  @Test def classifyCell{
    assertEquals("BOX_HEAD",ClassificationModel.classifyCell(dataAdapter,point))
  }
  @Test def classifyAdapter{
    assertEquals(("Unformatted","BOX_HEAD",XSD_STRING,new CellStyle((255,255,255),new CellFont((1,0,0),FontStyle.bold,12))),ClassificationModel.classifyAdapter(dataAdapter)(sheet1)(point.row)(point.col))
  }
}
