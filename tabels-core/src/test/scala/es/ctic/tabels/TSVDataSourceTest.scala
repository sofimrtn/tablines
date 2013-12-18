package es.ctic.tabels


import org.scalatest.junit.JUnitSuite
import org.junit.{Test,Before}
import org.junit.Assert._
import java.io.File

/**
 * Project: tabels-core
 * User: Alfonso Noriega Meneses <alfonso.noriega@fundacionctic.org>
 * Date: 17/12/13
 */

class TSVDataAdapterIntegrationTest extends JUnitSuite {

  var dataAdapter : TSVDataAdapter = null
  val filename1 : String = this.getClass.getResource("/es/ctic/tabels/Test2.tsv").getFile.replace("%20"," ")
  val sheet1 = ""

  @Before def setUp {
    val file1 = new File(filename1)
    dataAdapter = new TSVDataAdapter(file1)
  }

  @Test def getTabs {
    assertEquals(Seq(""), dataAdapter.getTabs())
  }

  @Test def getCols {
    assertEquals(2, dataAdapter.getCols(sheet1))
  }

  @Test def getRows {
    assertEquals(12, dataAdapter.getRows(sheet1))
  }

  @Test def getValue {
    assertEquals(Literal("Formatted"), dataAdapter.getValue(Point(filename1, sheet1, row = 0, col = 1)).getContent)

    assertEquals(Literal(3.1415, XSD_DOUBLE), dataAdapter.getValue(Point(filename1, sheet1, row = 1, col = 0)).getContent)
    assertEquals(Literal(3, XSD_INTEGER), dataAdapter.getValue(Point(filename1, sheet1, row = 2, col = 1)).getContent)
    assertEquals(Literal(3.01, XSD_DOUBLE), dataAdapter.getValue(Point(filename1, sheet1, row = 3, col = 0)).getContent)
    assertEquals(Literal(-5, XSD_INTEGER), dataAdapter.getValue(Point(filename1, sheet1, row = 4, col = 0)).getContent)
    assertEquals(Literal(38281827, XSD_INTEGER), dataAdapter.getValue(Point(filename1, sheet1, row = 5, col = 0)).getContent)

    assertEquals(Literal("€ 6,23 ", XSD_STRING), dataAdapter.getValue(Point(filename1, sheet1, row = 6, col = 1)).getContent)
    assertEquals(Literal(0.31, XSD_DECIMAL), dataAdapter.getValue(Point(filename1, sheet1, row = 7, col = 1)).getContent)
    //FIXME: TimeZone
    //assertEquals(Literal("2011-10-13", XSD_DATE), dataAdapter.getValue(Point(filename1, sheet1, row = 8, col = 0)).getContent)

    assertEquals(Literal(6, XSD_INTEGER), dataAdapter.getValue(Point(filename1, sheet1, row = 9, col = 1)).getContent)
    assertEquals(Literal("\"9\"", XSD_STRING), dataAdapter.getValue(Point(filename1, sheet1, row = 10, col = 0)).getContent)
    assertEquals(Literal("zocalo", XSD_STRING), dataAdapter.getValue(Point(filename1, sheet1, row = 11, col = 1)).getContent)
  }


}
