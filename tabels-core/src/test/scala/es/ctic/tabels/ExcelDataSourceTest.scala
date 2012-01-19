package es.ctic.tabels

import org.scalatest.junit.JUnitSuite
import org.junit.{Test,Before}
import org.junit.Assert._
import java.io.File

class ExcelDataAdapterIntegrationTest extends JUnitSuite {
	
	var dataAdapter : ExcelDataAdapter = null
	val filename1 : String = this.getClass.getResource("/es/ctic/tabels/Test1.xls").getFile.replace("%20"," ")
	val sheet1 = "Hoja1"
	
	@Before def setUp {
		val file1 = new File(filename1)
		dataAdapter = new ExcelDataAdapter(file1)
	}
	
	@Test def getTabs {
		assertEquals(Seq("Hoja1", "Hoja2", "Hoja3"), dataAdapter.getTabs())
	}
	
	@Test def getCols {
		assertEquals(2, dataAdapter.getCols(sheet1))
	}
	
	@Test def getRows {
		assertEquals(12, dataAdapter.getRows(sheet1))
	}
	
	@Test def getValue {
		assertEquals(Literal("Formatted"), dataAdapter.getValue(Point(filename1, sheet1, row = 0, col = 1)).getContent)

		assertEquals(Literal("3.1415", XSD_DECIMAL), dataAdapter.getValue(Point(filename1, sheet1, row = 1, col = 1)).getContent)
	//	assertEquals(Literal("3", XSD_INT), dataAdapter.getValue(Point(filename1, sheet1, row = 2, col = 1)).getContent)
		assertEquals(Literal("3.01", XSD_DECIMAL), dataAdapter.getValue(Point(filename1, sheet1, row = 3, col = 1)).getContent)
	//	assertEquals(Literal("-5", XSD_INT), dataAdapter.getValue(Point(filename1, sheet1, row = 4, col = 1)).getContent)
	//	assertEquals(Literal("38281827", XSD_INT), dataAdapter.getValue(Point(filename1, sheet1, row = 5, col = 1)).getContent)

	//	assertEquals(Literal("6", XSD_INT), dataAdapter.getValue(Point(filename1, sheet1, row = 6, col = 1)).getContent)
		assertEquals(Literal("0.31", XSD_DECIMAL), dataAdapter.getValue(Point(filename1, sheet1, row = 7, col = 1)).getContent)
//FIX ME: TimeZone 
//		assertEquals(Literal("2011-10-13", XSD_DATE), dataAdapter.getValue(Point(filename1, sheet1, row = 8, col = 1)).getContent)

	//	assertEquals(Literal("6", XSD_INT), dataAdapter.getValue(Point(filename1, sheet1, row = 9, col = 1)).getContent)
		assertEquals(Literal("9", XSD_STRING), dataAdapter.getValue(Point(filename1, sheet1, row = 10, col = 1)).getContent)
		assertEquals(Literal("zocalo", XSD_STRING), dataAdapter.getValue(Point(filename1, sheet1, row = 11, col = 1)).getContent)
	}
	
}

class ExceldataAdapterTest extends JUnitSuite {

	@Test def decimalFormatPattern {
	     assertFalse( "Testing '0'",ExcelCellValue(null).decimalFormatPattern.findFirstIn("0").isEmpty)
	     assertFalse( "Testing '0.00'",ExcelCellValue(null).decimalFormatPattern.findFirstIn("0.00").isEmpty)
	     assertFalse( "Testing '#,##0'",ExcelCellValue(null).decimalFormatPattern.findFirstIn("#,##0").isEmpty)
	     assertFalse( "Testing '#,##0.00'",ExcelCellValue(null).decimalFormatPattern.findFirstIn("#,##0.00").isEmpty)
	     assertFalse( "Testing '0.00;[Red]0.00'",ExcelCellValue(null).decimalFormatPattern.findFirstIn("0.00;[Red]0.00").isEmpty)
	     assertTrue( "Testing '1.00;[Red]0.00'",ExcelCellValue(null).decimalFormatPattern.findFirstIn("1.00;[Red]0.00").isEmpty)
	    
	  }

}