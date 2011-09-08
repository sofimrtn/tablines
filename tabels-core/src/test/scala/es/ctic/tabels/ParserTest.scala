package es.ctic.tabels

import org.scalatest.junit.JUnitSuite
import scala.collection.mutable.ListBuffer
import org.junit.Test
import org.junit.Assert._
import scala.util.parsing.combinator._
import scala.util.parsing.input.CharSequenceReader

class TabelsParserTest extends TabelsParser with JUnitSuite {
    
    @Test def parseVariable() {
        assertParse(variable, "?x", Variable("?x"))
        assertParse(variable, "?X", Variable("?X"))
        assertParse(variable, "?xx", Variable("?xx"))
        assertParse(variable, "?x0y", Variable("?x0y"))
        assertFail (variable, "")
        assertFail (variable, "?")
        assertFail (variable, "??x")
        assertFail (variable, "?0")
    }
    
    @Test def parsePosition() {
        assertParse(position, "A1", Position("A1"))
        assertParse(position, "BC10", Position("BC10"))
        assertFail (position, "")
        assertFail (position, "A")
        assertFail (position, "1")
        assertFail (position, "1A")
    }

    // auxiliary methods inspired by
    // http://henkelmann.eu/2011/01/29/an_introduction_to_scala_parser_combinators-part_3_unit_tests
    private def assertParse[T](p:Parser[T], input:String, expectedValue: T) = {
        val phraseParser = phrase(p)
        phraseParser(new CharSequenceReader(input)) match {
            case Success(t,_)     => assertEquals("While parsing '" + input + "'", expectedValue, t)
            case NoSuccess(msg,_) => throw new IllegalArgumentException("Could not parse '" + input + "': " + msg)
        }
    }

    private def assertFail[T](p:Parser[T],input:String) = {
        val phraseParser = phrase(p)
        phraseParser(new CharSequenceReader(input)) match {
            case Success(t,_)     => fail()
            case NoSuccess(msg,_) => msg
        }
    }

}