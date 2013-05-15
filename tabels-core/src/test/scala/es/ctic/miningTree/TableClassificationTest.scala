package es.ctic.miningTree

import org.scalatest.junit.JUnitSuite
import org.junit.{Test, Before}
import org.junit.Assert._
import es.ctic.tabels.{Point, DataAdapter}
import java.io.File

/**
 * Created with IntelliJ IDEA.
 * User: alfonso
 * Date: 19/02/13
 * Time: 10:51
 * To change this template use File | Settings | File Templates.
 */
class TableClassificationTest extends JUnitSuite {

  val trainingPath : File = new File(this.getClass.getResource("/es/ctic/miningTree/").getFile)
  val filename1 : String = this.getClass.getResource("/es/ctic/miningTree/Test1.xls").getFile.replace("%20"," ")
  val tableClassifier = new TableClassification(trainingPath)

  var dataAdapter : DataAdapter = null
  val sheet1 = "Hoja1"

  @Before def setUp {

    dataAdapter = DataAdapter.createAdapter(filename1)
   // tableClassifier.classifyAdapter(dataAdapter)
  }
  @Test def classifyAdapter{

    assertEquals("BOX_HEAD",dataAdapter.getValue(new Point(filename1, sheet1, row = 0, col = 1)).getClassification(dataAdapter,new Point(filename1, sheet1, row = 0, col = 1),trainingPath))
  }

  @Test def classifyCell{

    assertEquals("DATA_MATRIX",tableClassifier.classifyCell(dataAdapter,dataAdapter.getValue(new Point(filename1, sheet1, row = 2, col = 1)),new Point(filename1, sheet1, row = 0, col = 1)))
  }

}
