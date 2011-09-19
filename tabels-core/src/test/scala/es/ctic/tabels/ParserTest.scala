package es.ctic.tabels

import org.scalatest.junit.JUnitSuite
import org.junit.Test
import org.junit.Assert._

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
        assertParse(position, "A1", Position(row = 0, col = 0))
        assertParse(position, "A10", Position(row = 9, col = 0))
        assertParse(position, "D1", Position(row = 0, col = 3))
        assertParse(position, "AA99", Position(row = 98, col = 26)) 
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
	
	@Test def parseStart() {
		// FIXME: test some programs
		assertParse(start, "", S())
	}
	
	@Test def parsePattern() {
        assertParse(pattern, "?X in cell A1", Pattern(lBindE= List(), lPatternM = List(PatternMatch(variable = Variable("?X"), position = Position(0,0))) ))
        assertParse(pattern, "For ?y in rows \n ?x in cell A1", 
            Pattern(lBindE = List( 
                BindingExpresion(lBindE = List(), variable = Variable("?y"), dim = Dimension("rows"), 
                    lPatternM = List(PatternMatch(variable = Variable("?x"), position = Position(0,0)))))))
		assertParse(pattern, "For ?y in rows \n For ?z in cols \n ?x in cell A1", 
		    Pattern(lBindE = List( 
		        BindingExpresion(lBindE = List( 
		            BindingExpresion(lBindE = List(), variable = Variable("?z"), dim = Dimension("cols"), 
		                lPatternM = List(PatternMatch(variable = Variable("?x"), position = Position(0,0))))), 
		        variable = Variable("?y"), dim = Dimension("rows")))))
		assertFail (pattern, "For ?y in rows")
        assertFail (pattern, "")
	}
    
     @Test def parsePatternMatch() {
        assertParse(patternMatch, "?X in cell A1", PatternMatch(variable = Variable("?X"), position = Position(0,0)))
        assertParse(patternMatch, "?X    in  cell   A1", PatternMatch(variable = Variable("?X"), position = Position(0,0)))
        assertParse(patternMatch, "?X IN CELL A1", PatternMatch(variable = Variable("?X"), position = Position(0,0)))
        assertFail (patternMatch, "")
        assertFail (patternMatch, "?X")
        assertFail (patternMatch, "A1")
        assertFail (patternMatch, "IN CELL")
        assertFail (patternMatch, "IN CELL A1")
    }
    
    @Test def parseBindingExpresion(){
       assertParse(bindingExpresion, "For ?y in rows \n ?x in cell A1",
           BindingExpresion(lBindE = List(), variable = Variable("?y"), dim = Dimension("rows"), 
               lPatternM = List(PatternMatch(variable = Variable("?x"), position = Position(0,0)))))
       assertParse(bindingExpresion, "For ?y in rows  For ?z in cols  ?x in cell A1", 
           BindingExpresion(lBindE = List(
               BindingExpresion(lBindE = List(), variable = Variable("?z"), dim = Dimension("cols"), 
            		   lPatternM = List(PatternMatch(variable = Variable("?x"), position = Position(0,0))))),
           variable = Variable("?y"), dim = Dimension("rows")))
           
        assertFail (bindingExpresion, "For ?y in rows")
        assertFail (bindingExpresion, "For ?y in rows  For ?z in cols")
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

	// auxiliary methods

    private def assertParse[T](p:Parser[T], input:String, expectedValue: T) = {
		assertEquals("While parsing '" + input + "'", expectedValue, parse(p, input))
    }

    private def assertFail[T](p:Parser[T], input:String) = {
		try {
			parse(p, input)
			fail("Should not have parsed '" + input + "'")
		} catch {
			case e => // expected
		}
    }

}