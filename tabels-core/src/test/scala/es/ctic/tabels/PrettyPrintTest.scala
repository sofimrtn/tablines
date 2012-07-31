package es.ctic.tabels

import org.scalatest.junit.JUnitSuite
import org.junit.Test
import org.junit.Assert._

class PrettyPrintTest extends JUnitSuite {

    val prettyPrint = new PrettyPrint()
    
    @Test def visitS() {
        val emptyProgram = S()
        prettyPrint.visit(emptyProgram)
        assertEquals("", prettyPrint.toString())
    }

}