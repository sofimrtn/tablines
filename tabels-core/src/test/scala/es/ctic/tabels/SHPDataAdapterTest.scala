package es.ctic.tabels

import org.scalatest.junit.JUnitSuite
import org.junit.{Test, Before}
import java.io.File
import org.junit.Assert._

/**
 * Created with IntelliJ IDEA.
 * User: Guillermo Gonzalez-Moriyon
 * Date: 8/2/12
 * Time: 8:25 AM
 */


class SHPDataAdapterNLWaterIntegrationTest extends JUnitSuite {

  var dataAdapter : SHPDataAdapter = null
  val filename1 : String = this.getClass.getResource("/es/ctic/tabels/NL-water-simple-Shp.zip").getFile.replace("%20"," ")
  val sheet1 = ""

  @Before def setUp {
    val file1 = new File(filename1)
    dataAdapter = new SHPDataAdapter(file1)
  }

  @Test def getTabs {
    assertEquals(Seq(""), dataAdapter.getTabs())
  }

  @Test def getCols {
    assertEquals(6, dataAdapter.getCols(sheet1))
  }

  @Test def getRows {
    assertEquals(2625, dataAdapter.getRows(sheet1))
  }

  @Test def getValue {
    assertEquals(Literal(0.0, XSD_DOUBLE), dataAdapter.getValue(Point(filename1, sheet1, row = 1, col = 3)).getContent)
    // header
    assertNotSame(Literal("COUNT,N,11,0"), dataAdapter.getValue(Point(filename1, sheet1, row = 0, col = 1)).getContent)
    assertEquals(Literal("COUNT"), dataAdapter.getValue(Point(filename1, sheet1, row = 0, col = 1)).getContent)

    // row 1 is problematic
    assertEquals(Literal(6213, XSD_INTEGER), dataAdapter.getValue(Point(filename1, sheet1, row = 1, col = 0)).getContent)
    assertEquals(Literal(903, XSD_INTEGER), dataAdapter.getValue(Point(filename1, sheet1, row = 1, col = 1)).getContent)
    assertEquals(Literal("", XSD_STRING), dataAdapter.getValue(Point(filename1, sheet1, row = 1, col = 2)).getContent)
    assertEquals(Literal(0.0, XSD_DOUBLE), dataAdapter.getValue(Point(filename1, sheet1, row = 1, col = 3)).getContent)
    assertEquals(Literal(0.0, XSD_DOUBLE), dataAdapter.getValue(Point(filename1, sheet1, row = 1, col = 4)).getContent)
    assertEquals(Literal(0.0, XSD_DOUBLE), dataAdapter.getValue(Point(filename1, sheet1, row = 1, col = 5)).getContent)

    // one for each column but picking random row
    assertEquals(Literal(6113, XSD_INTEGER), dataAdapter.getValue(Point(filename1, sheet1, row = 3, col = 0)).getContent)
    assertEquals(Literal(903, XSD_INTEGER), dataAdapter.getValue(Point(filename1, sheet1, row = 1, col = 1)).getContent)
    assertEquals(Literal("Wateren2.shp", XSD_STRING), dataAdapter.getValue(Point(filename1, sheet1, row = 4, col = 2)).getContent)
    assertEquals(Literal(32549.478, XSD_DOUBLE), dataAdapter.getValue(Point(filename1, sheet1, row = 5, col = 3)).getContent)
    assertEquals(Literal(470.339, XSD_DOUBLE), dataAdapter.getValue(Point(filename1, sheet1, row = 6, col = 4)).getContent)
    assertEquals(Literal(2.195   , XSD_DOUBLE), dataAdapter.getValue(Point(filename1, sheet1, row = 8, col = 5)).getContent)

    // last row
    assertEquals(Literal(458, XSD_INTEGER), dataAdapter.getValue(Point(filename1, sheet1, row = 2624, col = 1)).getContent)
  }

}

class SHPDataAdapterGemeentegrenzenTest extends JUnitSuite {

  var dataAdapter : SHPDataAdapter = null
  val filename1 : String = this.getClass.getResource("/es/ctic/tabels/2012-NL-Gemeentegrenzen.zip").getFile.replace("%20"," ")
  val sheet1 = ""

  @Before def setUp {
    val file1 = new File(filename1)
    dataAdapter = new SHPDataAdapter(file1)
  }

  @Test def getTabs {
    assertEquals(Seq(""), dataAdapter.getTabs())
  }

  /*       */

  @Test def getCols {
    assertEquals(4, dataAdapter.getCols(sheet1))
  }

  @Test def getRows {
    assertEquals(419, dataAdapter.getRows(sheet1))
  }

  @Test def getValue {
    // header
    assertNotSame(Literal("OMSCHRIJVI,C,28"), dataAdapter.getValue(Point(filename1, sheet1, row = 0, col = 1)).getContent)
    assertEquals(Literal("OMSCHRIJVI"), dataAdapter.getValue(Point(filename1, sheet1, row = 0, col = 1)).getContent)

    // one for each column but picking random row
    assertEquals(Literal(603, XSD_INTEGER), dataAdapter.getValue(Point(filename1, sheet1, row = 3, col = 0)).getContent)
    assertEquals(Literal("Eemsmond", XSD_STRING), dataAdapter.getValue(Point(filename1, sheet1, row = 1, col = 1)).getContent)
    assertEquals(Literal(30123, XSD_INTEGER), dataAdapter.getValue(Point(filename1, sheet1, row = 2, col = 2)).getContent)
    assertEquals(Literal("Fryslân", XSD_STRING), dataAdapter.getValue(Point(filename1, sheet1, row = 5, col = 3)).getContent)


    // last row
    assertEquals(Literal(4723, XSD_INTEGER), dataAdapter.getValue(Point(filename1, sheet1, row = 418, col = 0)).getContent)
  }
}

  class SHPDataAdapterSafetyRegionsTest extends JUnitSuite {

    var dataAdapter : SHPDataAdapter = null
    val filename1 : String = this.getClass.getResource("/es/ctic/tabels/2012-NL-Safety-regions-borders-incl-water.zip").getFile.replace("%20"," ")
    val sheet1 = ""

    @Before def setUp {
      val file1 = new File(filename1)
      dataAdapter = new SHPDataAdapter(file1)
    }

    @Test def getTabs {
      assertEquals(Seq(""), dataAdapter.getTabs())
    }

    /*       */

    @Test def getCols {
      assertEquals(4, dataAdapter.getCols(sheet1))
    }

    @Test def getRows {
      assertEquals(26, dataAdapter.getRows(sheet1))
    }

    @Test def getValue {
      // header
      assertNotSame(Literal("OMSCHRIJVI,C,28"), dataAdapter.getValue(Point(filename1, sheet1, row = 0, col = 1)).getContent)
      assertEquals(Literal("OMSCHRIJVI"), dataAdapter.getValue(Point(filename1, sheet1, row = 0, col = 1)).getContent)

      // one for each column but picking random row
      assertEquals(Literal(3, XSD_INTEGER), dataAdapter.getValue(Point(filename1, sheet1, row = 3, col = 0)).getContent)
      assertEquals(Literal("Fryslân", XSD_STRING), dataAdapter.getValue(Point(filename1, sheet1, row = 1, col = 1)).getContent)
      assertEquals(Literal(30113, XSD_INTEGER), dataAdapter.getValue(Point(filename1, sheet1, row = 2, col = 2)).getContent)
      assertEquals(Literal("Overijssel", XSD_STRING), dataAdapter.getValue(Point(filename1, sheet1, row = 5, col = 3)).getContent)


      // last row
      assertEquals(Literal(20, XSD_INTEGER), dataAdapter.getValue(Point(filename1, sheet1, row = 25, col = 0)).getContent)
    }
  }

