package es.ctic.tabels

import org.scalatest.junit.JUnitSuite
import org.junit.{Test,Before}
import org.junit.Assert._
import java.io.File

/**
 * Created with IntelliJ IDEA.
 * User: Guillermo Gonzalez-Moriyon
 * Date: 7/30/12
 * Time: 1:15 PM
 */
   /*  This is commented until we find a better DBFReader (current javadbf does now work with this file
class DBFDataAdapterNLWaterIntegrationTest extends JUnitSuite {

  var dataAdapter : DBFDataAdapter = null
  val filename1 : String = this.getClass.getResource("/es/ctic/tabels/2005-NL-water.dbf").getFile.replace("%20"," ")
  val sheet1 = ""

  @Before def setUp {
    val file1 = new File(filename1)
    dataAdapter = new DBFDataAdapter(file1)
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
    // header
    assertNotSame(Literal("COUNT,N,11,0"), dataAdapter.getValue(Point(filename1, sheet1, row = 0, col = 1)).getContent)
    assertEquals(Literal("COUNT"), dataAdapter.getValue(Point(filename1, sheet1, row = 0, col = 1)).getContent)

    // one for each column but picking random row
    assertEquals(Literal(6113, XSD_INT), dataAdapter.getValue(Point(filename1, sheet1, row = 3, col = 0)).getContent)
    assertEquals(Literal(903, XSD_INT), dataAdapter.getValue(Point(filename1, sheet1, row = 1, col = 1)).getContent)
    assertEquals(Literal("Wateren2.shp", XSD_STRING), dataAdapter.getValue(Point(filename1, sheet1, row = 4, col = 2)).getContent)
    assertEquals(Literal("32549.478", XSD_DECIMAL), dataAdapter.getValue(Point(filename1, sheet1, row = 5, col = 3)).getContent)
    assertEquals(Literal("470.339", XSD_DECIMAL), dataAdapter.getValue(Point(filename1, sheet1, row = 6, col = 4)).getContent)
    assertEquals(Literal("2.195"   , XSD_DECIMAL), dataAdapter.getValue(Point(filename1, sheet1, row = 8, col = 5)).getContent)

    // last row
    assertEquals(Literal(458, XSD_INT), dataAdapter.getValue(Point(filename1, sheet1, row = 2624, col = 1)).getContent)
  }

}*/

class DBFDataAdapterGemeentegrenzenIntegrationTest extends JUnitSuite {

  var dataAdapter : DBFDataAdapter = null
  val filename1 : String = this.getClass.getResource("/es/ctic/tabels/2012-Gemeentegrenzen.dbf").getFile.replace("%20"," ")
  val sheet1 = ""

  @Before def setUp {
    val file1 = new File(filename1)
    dataAdapter = new DBFDataAdapter(file1)
  }

  @Test def getTabs {
    assertEquals(Seq(""), dataAdapter.getTabs())
  }

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
    assertEquals(Literal(603.0, XSD_DECIMAL), dataAdapter.getValue(Point(filename1, sheet1, row = 3, col = 0)).getContent)
    assertEquals(Literal("Eemsmond", XSD_STRING), dataAdapter.getValue(Point(filename1, sheet1, row = 1, col = 1)).getContent)
    assertEquals(Literal(30123.0, XSD_DECIMAL), dataAdapter.getValue(Point(filename1, sheet1, row = 2, col = 2)).getContent)
    assertEquals(Literal("Fryslân", XSD_STRING), dataAdapter.getValue(Point(filename1, sheet1, row = 5, col = 3)).getContent)


    // last row
    assertEquals(Literal(4723.0, XSD_DECIMAL), dataAdapter.getValue(Point(filename1, sheet1, row = 418, col = 0)).getContent)
  }

}


class DBFDataAdapterTest extends JUnitSuite {

  @Test def decimalFormatPattern {
    assertFalse( "Testing '0'",ODFCellValue(null).decimalFormatPattern.findFirstIn("0").isEmpty)
    assertFalse( "Testing '0.00'",ODFCellValue(null).decimalFormatPattern.findFirstIn("0.00").isEmpty)
    assertFalse( "Testing '#,##0'",ODFCellValue(null).decimalFormatPattern.findFirstIn("#,##0").isEmpty)
    assertFalse( "Testing '#,##0.00'",ODFCellValue(null).decimalFormatPattern.findFirstIn("#,##0.00").isEmpty)
    assertFalse( "Testing '0.00;[Red]0.00'",ODFCellValue(null).decimalFormatPattern.findFirstIn("0.00;[Red]0.00").isEmpty)
    assertTrue( "Testing '1.00;[Red]0.00'",ODFCellValue(null).decimalFormatPattern.findFirstIn("1.00;[Red]0.00").isEmpty)

  }

}

