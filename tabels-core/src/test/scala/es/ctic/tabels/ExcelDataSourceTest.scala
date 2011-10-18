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
		assertEquals(1, dataSource.getCols(filename1, sheet1))
	}
	
	@Test def getRows {
		assertEquals(1, dataSource.getRows(filename1, sheet1))
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