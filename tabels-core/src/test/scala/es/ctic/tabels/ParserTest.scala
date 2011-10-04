package es.ctic.tabels

import es.ctic.tabels.Dimension._
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

	@Test def parseDimension() {
		assertParse(dimension, "rows", rows)
		assertParse(dimension, "Cols", cols)
		assertParse(dimension, "SHEETS", sheets)
		assertParse(dimension, "fILES", files)
		assertFail (dimension, "")
		assertFail (dimension, "pages")
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
        assertParse(pattern, "let ?X in cell A1", Pattern(concretePattern = Right(LetWhereExpression(tupleOrVariable = Right(Variable("?X")), position = Some(Position(0,0))))))
        assertParse(pattern, "For ?y in rows \n let ?x in cell A1", 
            Pattern(concretePattern = Left(BindingExpression(variable = Variable("?y"), dimension = Dimension.rows, 
                childPatterns = Seq(Pattern( concretePattern = Right(LetWhereExpression(tupleOrVariable = Right(Variable("?x")), position = Some(Position(0,0))))))))))
		assertParse(pattern, "For ?y in rows \n For ?z in cols \n let ?x in cell A1", 
		    Pattern(concretePattern = Left(BindingExpression(variable = Variable("?y"), dimension = Dimension.rows, 
                childPatterns = Seq(Pattern(concretePattern = Left(BindingExpression(variable = Variable("?z"), dimension = Dimension.cols, 
                childPatterns = Seq(Pattern(concretePattern = Right(LetWhereExpression(tupleOrVariable = Right(Variable("?x")), position = Some(Position(0,0))))))))))))))
		    
		assertFail (pattern, "For ?y in rows")
        assertFail (pattern, "")
	}
    
     @Test def parseletWhereExpression() {
        assertParse(letWhereExpression, "let ?X in cell A1", LetWhereExpression(tupleOrVariable = Right(Variable("?X")), position = Some(Position(0,0))))
        assertParse(letWhereExpression, "LET ?X    in  cell   A1", LetWhereExpression(tupleOrVariable = Right(Variable("?X")), position = Some(Position(0,0))))
        assertParse(letWhereExpression, "Let  ?X IN CELL A1", LetWhereExpression(tupleOrVariable = Right(Variable("?X")), position = Some(Position(0,0))))
        assertFail (letWhereExpression, "")
        assertFail (letWhereExpression, "?X")
        assertFail (letWhereExpression, "A1")
        assertFail (letWhereExpression, "IN CELL")
        assertFail (letWhereExpression, "IN CELL A1")
    }
    
    @Test def parseBindingExpression(){
       assertParse(bindingExpression, "For ?y in rows \n let ?x in cell A1",
           BindingExpression(variable = Variable("?y"), dimension = Dimension.rows, 
                childPatterns = Seq(Pattern( concretePattern = Right(LetWhereExpression(tupleOrVariable = Right(Variable("?x")), position = Some(Position(0,0))))))))
       assertParse(bindingExpression, "For ?y in rows  For ?z in cols let ?x in cell A1", 
           BindingExpression(variable = Variable("?y"), dimension = Dimension.rows, 
                childPatterns = Seq(Pattern(concretePattern = Left(BindingExpression(variable = Variable("?z"), dimension = Dimension.cols, 
                childPatterns = Seq(Pattern(concretePattern = Right(LetWhereExpression(tupleOrVariable = Right(Variable("?x")), position = Some(Position(0,0))))))))))))
           
        assertFail (bindingExpression, "For ?y in rows")
        assertFail (bindingExpression, "For ?y in rows  For ?z in cols")
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
			Template(Set(TripleTemplate(Right(Variable("?x")), Right(Variable("?y")), Right(Variable("?z"))),
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