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

	@Test def parseRdfLiteral() {
		assertParse(rdfLiteral, "\"hello\"", Literal("hello"))
		assertFail (rdfLiteral, "")
		assertFail (rdfLiteral, "<http://example.org/>")
		assertFail (rdfLiteral, "?x")
	}

	@Test def parseUriRef() {
		assertParse(uriRef, "<http://example.org/>", Resource("http://example.org/"))
		assertFail(uriRef, "http://example.org/")
	}
	
	@Test def parseEitherRDFNodeOrVariable() {
		assertParse(eitherRDFNodeOrVariable, "\"hello\"", Left(Literal("hello")))
		assertParse(eitherRDFNodeOrVariable, "<http://example.org/>", Left(Resource("http://example.org/")))
		assertParse(eitherRDFNodeOrVariable, "?x", Right(Variable("?x")))
		assertFail (eitherRDFNodeOrVariable, "")
	}
    
     @Test def parsePatternMatch() {
        assertParse(patternMatch, "?X in cell A1", PatternMatch(variable = Variable("?X"), position = Position("A1")))
        assertParse(patternMatch, "?X    in  cell   A1", PatternMatch(variable = Variable("?X"), position = Position("A1")))
        assertParse(patternMatch, "?X IN CELL A1", PatternMatch(variable = Variable("?X"), position = Position("A1")))
        assertFail (patternMatch, "")
        assertFail (patternMatch, "?X")
        assertFail (patternMatch, "A1")
        assertFail (patternMatch, "IN CELL")
        assertFail (patternMatch, "IN CELL A1")
    }

	@Test def parseTripleTemplate() {
		assertParse(tripleTemplate, "?x ?y ?z .", TripleTemplate(Right(Variable("?x")), Right(Variable("?y")), Right(Variable("?z"))))
		assertParse(tripleTemplate, "?x <http://example.org/> \"hello\" .", TripleTemplate(Right(Variable("?x")), Left(Resource("http://example.org/")), Left(Literal("hello"))))
		assertFail (tripleTemplate, "")
		assertFail (tripleTemplate, ".")
		assertFail (tripleTemplate, "?x . ")
		assertFail (tripleTemplate, "?x ?y ?z")
	}
	
	@Test def parseTemplate() {
		assertParse(template, "{ ?x ?y ?z . ?a ?b ?c . }",
			Template(List(TripleTemplate(Right(Variable("?x")), Right(Variable("?y")), Right(Variable("?z"))),
			              TripleTemplate(Right(Variable("?a")), Right(Variable("?b")), Right(Variable("?c"))))))
		assertFail (template, "")
		assertFail (template, "{ }")
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