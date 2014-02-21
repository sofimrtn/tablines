package es.ctic.tabels

/**
 * Project: tabels-core
 * User: Alfonso Noriega Meneses <alfonso.noriega@fundacionctic.org>
 * Date: 20/02/14
 */

import org.scalatest.junit.JUnitSuite
import org.junit.{Test,Before}
import org.junit.Assert._
import java.io.File

class ODFMarinePollutantIntegrationTest extends JUnitSuite {

  var dataAdapter : ODFDataAdapter = null
  val filename1 : String = this.getClass.getResource("/es/ctic/tabels/Marine-pollutants.ods").getFile.replace("%20"," ")
  val sheet1 = "Hoja1"


  @Before def setUp {
    val file1 = new File(filename1)
    dataAdapter = new ODFDataAdapter(file1)
  }

  @Test def getTabs {
    assertEquals(Seq("Hoja1"), dataAdapter.getTabs())
  }

  @Test def getCols {
    assertEquals(2, dataAdapter.getCols(sheet1))

  }

  @Test def getRows {
    assertEquals(778, dataAdapter.getRows(sheet1))

  }

  @Test def getValue {
    assertEquals(Literal("Acetone cyanohydrin, stabilized"), dataAdapter.getValue(Point(filename1, sheet1, row = 0, col = 1)).getContent)

    assertEquals(Literal("Acetylene tetrabromide"), dataAdapter.getValue(Point(filename1, sheet1, row = 1, col = 1)).getContent)
    assertEquals(Literal("Acetylene tetrachloride"), dataAdapter.getValue(Point(filename1, sheet1, row = 2, col = 1)).getContent)
    assertEquals(Literal("Acraldehyde, inhibited"), dataAdapter.getValue(Point(filename1, sheet1, row = 3, col = 1)).getContent)

  }

}