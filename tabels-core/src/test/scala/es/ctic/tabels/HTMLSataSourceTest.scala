package es.ctic.tabels

import org.scalatest.junit.JUnitSuite
import org.junit.{Test,Before}
import org.junit.Assert._
import java.io.{FileReader, File}
import org.xml.sax.InputSource
import javax.xml.parsers.DocumentBuilderFactory

class HTMLDataAdapterIntegrationTest extends JUnitSuite {

  val filename1 : String = this.getClass.getResource("/es/ctic/tabels/html_test.html").getFile.replace("%20"," ")
  var dataAdapter : HTMLDataAdapter = null
  val sheet1 = "0"
	//val sheet2 = "Hoja2"
	
	@Before def setUp {
		val file1 = new File(filename1)
		dataAdapter = new HTMLDataAdapter(file1)


	}
	
	@Test def getTabs {
		assertEquals(Seq("0"), dataAdapter.getTabs())
	}
	
	@Test def getCols {
		assertEquals(4, dataAdapter.getCols(sheet1))
	}
	
	@Test def getRows {
		assertEquals(251, dataAdapter.getRows(sheet1))
	}
	
	@Test def getValue {
		//assertEquals(Literal("Rating"), dataAdapter.getValue(Point(filename1, sheet1, row = 0, col = 1)).getContent)

		/*assertEquals(Literal("3.1415", XSD_DOUBLE), dataAdapter.getValue(Point(filename1, sheet1, row = 1, col = 1)).getContent)
		assertEquals(Literal(3, XSD_INTEGER), dataAdapter.getValue(Point(filename1, sheet1, row = 2, col = 1)).getContent)
		assertEquals(Literal("3.01", XSD_DOUBLE), dataAdapter.getValue(Point(filename1, sheet1, row = 3, col = 1)).getContent)
		assertEquals(Literal(-5, XSD_INTEGER), dataAdapter.getValue(Point(filename1, sheet1, row = 4, col = 1)).getContent)
		assertEquals(Literal(38281827, XSD_INTEGER), dataAdapter.getValue(Point(filename1, sheet1, row = 5, col = 1)).getContent)

		assertEquals(Literal(6.23, XSD_DOUBLE), dataAdapter.getValue(Point(filename1, sheet1, row = 6, col = 1)).getContent)
		assertEquals(Literal(0.31, XSD_DECIMAL), dataAdapter.getValue(Point(filename1, sheet1, row = 7, col = 1)).getContent)
//FIX ME: TimeZone
		assertEquals(Literal("2011-10-13", XSD_DATE), dataAdapter.getValue(Point(filename1, sheet1, row = 8, col = 0)).getContent)

		assertEquals(Literal(6, XSD_INTEGER), dataAdapter.getValue(Point(filename1, sheet1, row = 9, col = 1)).getContent)
		assertEquals(Literal("9", XSD_STRING), dataAdapter.getValue(Point(filename1, sheet1, row = 10, col = 1)).getContent)
		assertEquals(Literal("zocalo", XSD_STRING), dataAdapter.getValue(Point(filename1, sheet1, row = 11, col = 1)).getContent)   */
	}

  @Test def getStyle {
    //assertEquals(Literal("Rating"), dataAdapter.getValue(Point(filename1, sheet1, row = 0, col = 1)).getStyle)

  }
}

class HTMLDataAdapterTest extends JUnitSuite {

	@Test def decimalFormatPattern {
	     assertFalse( "Testing '0'",ODFCellValue(null).decimalFormatPattern.findFirstIn("0").isEmpty)
	     assertFalse( "Testing '0.00'",ODFCellValue(null).decimalFormatPattern.findFirstIn("0.00").isEmpty)
	     assertFalse( "Testing '#,##0'",ODFCellValue(null).decimalFormatPattern.findFirstIn("#,##0").isEmpty)
	     assertFalse( "Testing '#,##0.00'",ODFCellValue(null).decimalFormatPattern.findFirstIn("#,##0.00").isEmpty)
	     assertFalse( "Testing '0.00;[Red]0.00'",ODFCellValue(null).decimalFormatPattern.findFirstIn("0.00;[Red]0.00").isEmpty)
	     assertTrue( "Testing '1.00;[Red]0.00'",ODFCellValue(null).decimalFormatPattern.findFirstIn("1.00;[Red]0.00").isEmpty)
	    
	  }

}