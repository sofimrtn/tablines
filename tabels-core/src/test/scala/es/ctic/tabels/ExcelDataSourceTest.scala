package es.ctic.tabels

import org.scalatest.junit.JUnitSuite
import org.junit.Test
import org.junit.Assert._

class ExcelDataSourceTest extends JUnitSuite {
	@Test def evaluate{
	     assertFalse( "Testing '0'",ExcelCellValue(null).decimalFormatPattern.findFirstIn("0").isEmpty)
	     assertFalse( "Testing '0.00'",ExcelCellValue(null).decimalFormatPattern.findFirstIn("0.00").isEmpty)
	     assertFalse( "Testing '#,##0'",ExcelCellValue(null).decimalFormatPattern.findFirstIn("#,##0").isEmpty)
	     assertFalse( "Testing '#,##0.00'",ExcelCellValue(null).decimalFormatPattern.findFirstIn("#,##0.00").isEmpty)
	     assertFalse( "Testing '0.00;[Red]0.00'",ExcelCellValue(null).decimalFormatPattern.findFirstIn("0.00;[Red]0.00").isEmpty)
	     assertTrue( "Testing '1.00;[Red]0.00'",ExcelCellValue(null).decimalFormatPattern.findFirstIn("1.00;[Red]0.00").isEmpty)
	    
	  }
}