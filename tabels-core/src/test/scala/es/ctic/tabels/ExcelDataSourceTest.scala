package es.ctic.tabels

import org.scalatest.junit.JUnitSuite
import org.junit.{Test,Before}
import org.junit.Assert._
import java.io.File

class ExcelDataSourceIntegrationTest extends JUnitSuite {
	
	var dataSource : ExcelDataSource = null
	val filename1 = "src/test/resources/es/ctic/tabels/Test1.xls"
	val sheet1 = "Hoja1"
	
	@Before def setUp {
		val file1 = new File(filename1)
		val files = Seq(file1)
		dataSource = new ExcelDataSource(files)
	}
	
	@Test def getTabs {
		assertEquals(Seq("Hoja1", "Hoja2", "Hoja3"), dataSource.getTabs(filename1))
	}
	
	@Test def getCols {
		assertEquals(2, dataSource.getCols(filename1, sheet1))
	}
	
	@Test def getRows {
		assertEquals(11, dataSource.getRows(filename1, sheet1))
	}
	
	@Test def getValue {
		assertEquals(Literal("Formatted"), dataSource.getValue(Point(filename1, sheet1, row = 0, col = 1)).getContent)

		assertEquals(Literal("3.1415", XSD_DECIMAL), dataSource.getValue(Point(filename1, sheet1, row = 1, col = 1)).getContent)
		assertEquals(Literal("3", XSD_INT), dataSource.getValue(Point(filename1, sheet1, row = 2, col = 1)).getContent)
		assertEquals(Literal("3.01", XSD_DECIMAL), dataSource.getValue(Point(filename1, sheet1, row = 3, col = 1)).getContent)
		assertEquals(Literal("-5", XSD_INT), dataSource.getValue(Point(filename1, sheet1, row = 4, col = 1)).getContent)
		assertEquals(Literal("38281827", XSD_INT), dataSource.getValue(Point(filename1, sheet1, row = 5, col = 1)).getContent)

		assertEquals(Literal("6", XSD_INT), dataSource.getValue(Point(filename1, sheet1, row = 6, col = 1)).getContent)
		assertEquals(Literal("0.31", XSD_DECIMAL), dataSource.getValue(Point(filename1, sheet1, row = 7, col = 1)).getContent)
//FIX ME: TimeZone 
//		assertEquals(Literal("2011-10-13", XSD_DATE), dataSource.getValue(Point(filename1, sheet1, row = 8, col = 1)).getContent)

		assertEquals(Literal("6", XSD_INT), dataSource.getValue(Point(filename1, sheet1, row = 9, col = 1)).getContent)
		assertEquals(Literal("9", XSD_STRING), dataSource.getValue(Point(filename1, sheet1, row = 10, col = 1)).getContent)
	}
	
}

class ExcelDataSourceTest extends JUnitSuite {

	@Test def decimalFormatPattern {
	     assertFalse( "Testing '0'",ExcelCellValue(null).decimalFormatPattern.findFirstIn("0").isEmpty)
	     assertFalse( "Testing '0.00'",ExcelCellValue(null).decimalFormatPattern.findFirstIn("0.00").isEmpty)
	     assertFalse( "Testing '#,##0'",ExcelCellValue(null).decimalFormatPattern.findFirstIn("#,##0").isEmpty)
	     assertFalse( "Testing '#,##0.00'",ExcelCellValue(null).decimalFormatPattern.findFirstIn("#,##0.00").isEmpty)
	     assertFalse( "Testing '0.00;[Red]0.00'",ExcelCellValue(null).decimalFormatPattern.findFirstIn("0.00;[Red]0.00").isEmpty)
	     assertTrue( "Testing '1.00;[Red]0.00'",ExcelCellValue(null).decimalFormatPattern.findFirstIn("1.00;[Red]0.00").isEmpty)
	    
	  }

}