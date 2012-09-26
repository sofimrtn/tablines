package es.ctic.tabels

import org.scalatest.junit.JUnitSuite
import org.junit.{BeforeClass, Test, Before}
import java.io.File
import org.junit.Assert._

/**
 * Created with IntelliJ IDEA.
 * User: Guillermo Gonzalez-Moriyon
 * Date: 9/21/12
 * Time: 8:31 AM
 */
/*
class SHPMaplabDataAdapterNLTest extends JUnitSuite {

  var dataAdapter : SHPMaplabDataAdapter = null
  val filename1 : String = this.getClass.getResource("/es/ctic/tabels/2012-06-Imergis-OOV-POI-all-SHP.shp.zip").getFile.replace("%20"," ")
  val sheet1 = "dbf"
  val sheet2 = "sld"

  @Before def setUp {
    val file1 = new File(filename1)
    dataAdapter = new SHPMaplabDataAdapter(file1)
  }

  @Test def getTabs {
    assertEquals(Seq("dbf","sld"), dataAdapter.getTabs())
  }

  @Test def getCols {
    assertEquals(23+2, dataAdapter.getCols(sheet1))
    assertEquals(3, dataAdapter.getCols(sheet2))
  }

  @Test def getRows {
    assertEquals(7604, dataAdapter.getRows(sheet1))
    assertEquals(7604, dataAdapter.getRows(sheet2))
  }

  @Test def getValue {
    // header
    assertNotSame(Literal("X,N,10,0"), dataAdapter.getValue(Point(filename1, sheet1, row = 0, col = 1)).getContent)
    assertEquals(Literal("X"), dataAdapter.getValue(Point(filename1, sheet1, row = 0, col = 1)).getContent)

    // one for each column but picking random row
    assertEquals(Literal(3, XSD_INT), dataAdapter.getValue(Point(filename1, sheet1, row = 3, col = 0)).getContent)
    assertEquals(Literal(230180, XSD_INT), dataAdapter.getValue(Point(filename1, sheet1, row = 1, col = 1)).getContent)
    assertEquals(Literal("Winsum", XSD_STRING), dataAdapter.getValue(Point(filename1, sheet1, row = 2, col = 10)).getContent)

    assertEquals(Literal("http://www.w3.org/2003/01/geo/wgs84_pos#Point", XSD_STRING), dataAdapter.getValue(Point(filename1, sheet1, row = 1, col = 23)).getContent)
    assertEquals(Literal("1.kml", XSD_STRING), dataAdapter.getValue(Point(filename1, sheet1, row = 1, col = 24)).getContent)


    // last row
    assertEquals(Literal(7608, XSD_INT), dataAdapter.getValue(Point(filename1, sheet1, row = 7603, col = 0)).getContent)
  }     */

  class SHPMaplabDataAdapterBotanicTest extends JUnitSuite {

    var dataAdapter : SHPMaplabDataAdapter = null
    val filename1 : String = this.getClass.getResource("/es/ctic/tabels/POL_56-2-2_JBA.shp.zip").getFile.replace("%20"," ")
    val sheet1 = "dbf"
    val sheet2 = "sld"

    @Before def setUp {

      System.setProperty("tabels.publicTomcatWritablePath","http://www.tabels.com")
      val file1 = new File(filename1)
      dataAdapter = new SHPMaplabDataAdapter(file1)
    }
        /*
    @Test def getTabs {
      assertEquals(Seq("dbf","sld"), dataAdapter.getTabs())
    }

    @Test def getCols {
      assertEquals(10+2, dataAdapter.getCols(sheet1))
      assertEquals(3, dataAdapter.getCols(sheet2))
    }

    @Test def getRows {
      assertEquals(425, dataAdapter.getRows(sheet1))
      assertEquals(16, dataAdapter.getRows(sheet2))
    }           */

    @Test def getValue {
      // header
      assertNotSame(Literal("COD,C,10"), dataAdapter.getValue(Point(filename1, sheet1, row = 0, col = 1)).getContent)
      assertEquals(Literal("COD"), dataAdapter.getValue(Point(filename1, sheet1, row = 0, col = 1)).getContent)

      // one for each column but picking random row
      assertEquals(Literal(3, XSD_INT), dataAdapter.getValue(Point(filename1, sheet1, row = 3, col = 0)).getContent)
      assertEquals(Literal("13.1b", XSD_STRING), dataAdapter.getValue(Point(filename1, sheet1, row = 1, col = 1)).getContent)
      assertEquals(Literal("Comunidades casmofiticas con S. trifurcata y S. paniculata", XSD_STRING), dataAdapter.getValue(Point(filename1, sheet1, row = 1, col = 9)).getContent)

      assertEquals(Literal("http://geovocab.org/geometry#Polygon", XSD_STRING), dataAdapter.getValue(Point(filename1, sheet1, row = 1, col = 10)).getContent)
      // assertEquals(Literal("1.kml", XSD_STRING), dataAdapter.getValue(Point(filename1, sheet1, row = 1, col = 11)).getContent)
      assertTrue(dataAdapter.getValue(Point(filename1, sheet1, row = 1, col = 11)).getContent.asString.endsWith("1.kml"))


      // last row
      assertEquals(Literal(424, XSD_INT), dataAdapter.getValue(Point(filename1, sheet1, row = 424, col = 0)).getContent)


      // sld sheet: attr index
      assertEquals(Literal(1, XSD_INT), dataAdapter.getValue(Point(filename1, sheet2, row = 0, col = 0)).getContent)
      // sld sheet: attr value
      assertEquals(Literal("6.2a", XSD_STRING), dataAdapter.getValue(Point(filename1, sheet2, row = 0, col = 1)).getContent)
      // sld sheet: style in json public path
      assertEquals(Literal("http://www.tabels.com/ctic/json/leyenda_no_validated-1-1.json", XSD_STRING), dataAdapter.getValue(Point(filename1, sheet2, row = 0, col = 2)).getContent)
    }



}
