package es.ctic.tabels

import org.scalatest.junit.JUnitSuite
import org.junit.Test
import org.junit.Assert._

class ColumnConverterTest extends JUnitSuite {

	@Test def intToAlpha() {
		assertEquals("A", columnConverter.intToAlpha(0))
		assertEquals("AC", columnConverter.intToAlpha(28))
	}
	
	@Test def alphaToInt() {
		assertEquals(0, columnConverter.alphaToInt("A"))
		assertEquals(28, columnConverter.alphaToInt("AC"))
	}

}
