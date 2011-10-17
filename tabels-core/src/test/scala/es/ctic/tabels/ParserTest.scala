package es.ctic.tabels

import es.ctic.tabels.Dimension._
import scala.util.matching.Regex
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
    
    @Test def parseDisplacement() {
        assertParse(displacement, "3", 3)
        assertParse(displacement, "", 1)
    }

	@Test def parseDimension() {
		assertParse(dimension, "rows", rows)
		assertParse(dimension, "Cols", cols)
		assertParse(dimension, "SHEETS", sheets)
		assertParse(dimension, "fILES", files)
		assertFail (dimension, "")
		assertFail (dimension, "pages")
	}
	
    @Test def parsePosition() {
        assertParse(position, "A1", FixedPosition(row = 0, col = 0))
        assertParse(position, "A10", FixedPosition(row = 9, col = 0))
        assertParse(position, "D1", FixedPosition(row = 0, col = 3))
        assertParse(position, "AA99", FixedPosition(row = 98, col = 26)) 
        assertParse(position, "?x", WithVariablePosition(Variable("?x")))
        assertParse(position, "3 Left ?y", RelativePosition(RelativePos.left, WithVariablePosition(Variable("?y")), 3))
        assertParse(position, "RIGHT ?y", RelativePosition(RelativePos.right, WithVariablePosition(Variable("?y")), 1))
        assertFail (position, "")
        assertFail (position, "A")
        assertFail (position, "1")
        assertFail (position, "1A")
    }
    
	@Test def parseQuotedString() {
	    assertParse(quotedString, "\"hello\"", "hello")
	    assertFail (quotedString, "")
	}
	
	@Test def parseLangTag() {
	    assertParse(langTag, "en", "en")
	    assertParse(langTag, "en-US", "en-US")
	    assertFail (langTag, "")
	}

	@Test def parseRdfLiteral() {
		assertParse(rdfLiteral, "\"hello\"", Literal("hello"))
		assertParse(rdfLiteral, "\"hello\"@en", Literal("hello", langTag = "en"))
		assertParse(rdfLiteral, "\"5.3\"^^<http://www.w3.org/2001/XMLSchema#double>", Literal("5.3", XSD_DOUBLE))
		assertParse(rdfLiteral, "3", Literal("3", rdfType = XSD_INT))
		assertParse(rdfLiteral, "3.1415", Literal("3.1415", rdfType = XSD_DECIMAL))
		assertParse(rdfLiteral, "true", LITERAL_TRUE)
		assertParse(rdfLiteral, "false", LITERAL_FALSE)
		assertFail (rdfLiteral, "")
		assertFail (rdfLiteral, "<http://example.org/>")
		assertFail (rdfLiteral, "?x")
		assertFail (rdfLiteral, "unquoted")
	}

	@Test def parseIriRef() {
		assertParse(iriRef, "<http://example.org/>", Resource("http://example.org/"))
		assertFail(iriRef, "http://example.org/")
		assertFail(iriRef, "<http://example.org/> <http://example.com>")
	}
	
	@Test def parseCurieRef() {
		prefixes += ("foo" -> Resource("http://example.org/"))
		assertParse(curieRef, "foo:bar", Resource("http://example.org/bar"))
		assertFail(curieRef,  "")
		assertFail(curieRef,  "foo")
		assertFail(curieRef,  "foo:")
		assertFail(curieRef,  ":bar") // FIXME: default namespace should be allowed
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
	
	@Test def parsePrefixDecl() {
		assertParse(prefixDecl, "PREFIX foo: <http://example.org/>", ("foo" -> Resource("http://example.org/")))
		assertEquals(Resource("http://example.org/"), prefixes("foo"))
		assertFail (prefixDecl, "")
		assertFail (prefixDecl, "PREFIX foo:")
		assertFail (prefixDecl, "PREFIX <http://example.org/>")
		assertFail (prefixDecl, "PREFIX : <http://example.org/>") // FIXME: default namespace should be allowed
	}
	
	@Test def parsePattern() {
        assertParse(pattern, "let ?X in cell A1", Pattern(concretePattern = Right(LetWhereExpression(tupleOrVariable = Right(Variable("?X")), position = Some(FixedPosition(0,0))))))
        assertParse(pattern, "For ?y in rows \n let ?x in cell A1", 
            Pattern(concretePattern = Left(BindingExpression(variable = Variable("?y"), dimension = Dimension.rows, 
                childPatterns = Seq(Pattern( concretePattern = Right(LetWhereExpression(tupleOrVariable = Right(Variable("?x")), position = Some(FixedPosition(0,0))))))))))
		assertParse(pattern, "For ?y in rows \n For ?z in cols \n let ?x in cell A1", 
		    Pattern(concretePattern = Left(BindingExpression(variable = Variable("?y"), dimension = Dimension.rows, 
                childPatterns = Seq(Pattern(concretePattern = Left(BindingExpression(variable = Variable("?z"), dimension = Dimension.cols, 
                childPatterns = Seq(Pattern(concretePattern = Right(LetWhereExpression(tupleOrVariable = Right(Variable("?x")), position = Some(FixedPosition(0,0))))))))))))))
		    
		assertFail (pattern, "For ?y in rows")
        assertFail (pattern, "")
	}
	
    @Test def parseBindingExpression(){
       assertParse(bindingExpression, "For ?y in rows \n let ?x in cell A1",
           BindingExpression(variable = Variable("?y"), dimension = Dimension.rows, 
                childPatterns = Seq(Pattern( concretePattern = Right(LetWhereExpression(tupleOrVariable = Right(Variable("?x")), position = Some(FixedPosition(0,0))))))))
       assertParse(bindingExpression, "For ?y in rows  For ?z in cols let ?x in cell A1", 
           BindingExpression(variable = Variable("?y"), dimension = Dimension.rows, 
                childPatterns = Seq(Pattern(concretePattern = Left(BindingExpression(variable = Variable("?z"), dimension = Dimension.cols, 
                childPatterns = Seq(Pattern(concretePattern = Right(LetWhereExpression(tupleOrVariable = Right(Variable("?x")), position = Some(FixedPosition(0,0))))))))))))
           
        assertFail (bindingExpression, "For ?y in rows")
        assertFail (bindingExpression, "For ?y in rows  For ?z in cols")
    }

     @Test def parseLetWhereExpression() {
        assertParse(letWhereExpression, "let ?X in cell A1", LetWhereExpression(tupleOrVariable = Right(Variable("?X")), position = Some(FixedPosition(0,0))))
        assertParse(letWhereExpression, "LET ?X    in  cell   A1", LetWhereExpression(tupleOrVariable = Right(Variable("?X")), position = Some(FixedPosition(0,0))))
        assertParse(letWhereExpression, "Let  ?X IN CELL A1", LetWhereExpression(tupleOrVariable = Right(Variable("?X")), position = Some(FixedPosition(0,0))))
        assertFail (letWhereExpression, "")
        assertFail (letWhereExpression, "?X")
        assertFail (letWhereExpression, "A1")
        assertFail (letWhereExpression, "IN CELL")
        assertFail (letWhereExpression, "IN CELL A1")
    }
    
    @Test def parseRegex(){
      assertFail(regex, "\"(hola\"")
    }
     
    @Test def parseExpression(){
	    
      //ResourceExpression
    	assertParse(expression, "RESOURCE(?y, <http://ontorule-project.eu/resources/steeldata#coil>)", ResourceExpression(expression = VariableReference(Variable("?y")), uri =Resource("http://ontorule-project.eu/resources/steeldata#coil") ))
        assertParse(expression, "RESourCE (?y, <http://ontorule-project.eu/resources/steeldata#coil>)", ResourceExpression(expression = VariableReference(Variable("?y")), uri =Resource("http://ontorule-project.eu/resources/steeldata#coil") ))
        assertFail(expression, "RESourC (?y, <http://ontorule-project.eu/resources/steeldata#coil>)")
        assertFail(expression, "RESOURCE (y, <http://ontorule-project.eu/resources/steeldata#coil>)")
        assertFail(expression, "RESOURCE (?y, http://ontorule-project.eu/resources/steeldata#coil>)")
      
      //VariableReference
        assertParse(expression, "?y", VariableReference(Variable("?y")))
        assertFail(expression, "y")
       
      //RegexExpression
        //This test can't be done because Regex does not support the equal operation between regular expressions
        //assertParse(expression, "matches (?y, \"hola\")", RegexExpression(VariableReference(Variable("?y")),new Regex("hola")))
        assertFail(expression, "matches (?y, \"(hola\")")
        
   }
    
	@Test def parseVerbTemplate() {
		prefixes += ("foo" -> Resource("http://example.org/"))
		assertParse(verbTemplate, "<http://example.org/>", Left(Resource("http://example.org/")))
		assertParse(verbTemplate, "a", Left(RDF_TYPE))
		assertParse(verbTemplate, "foo:bar", Left(Resource("http://example.org/bar")))
		assertParse(verbTemplate, "?x", Right(Variable("?x")))
		assertFail (verbTemplate, "")
		assertFail (verbTemplate, "\"hello\"")
	}
	
	@Test def parseObjectsTemplate() {
		assertParse(objectsTemplate, "?x", List(Right(Variable("?x"))))
		assertParse(objectsTemplate, "<http://example.org/>", List(Left(Resource("http://example.org/"))))
		assertParse(objectsTemplate, "?x, ?y", List(Right(Variable("?x")), Right(Variable("?y"))))
		assertFail (objectsTemplate, "")
		assertFail (objectsTemplate, "?x ?y")
		assertFail (objectsTemplate, "?x ; ?y")
		assertFail (objectsTemplate, "?x . ?y")
	}
	
	@Test def parsePredicateObjectsTemplate() {
		assertParse(predicateObjectsTemplate, "?x ?y",
			List((Right(Variable("?x")), Right(Variable("?y")))))
		assertParse(predicateObjectsTemplate, "<http://example.org/> \"Hello\"",
			List((Left(Resource("http://example.org/")), Left(Literal("Hello")))))
		assertParse(predicateObjectsTemplate, "a <http://example.org/>",
			List((Left(RDF_TYPE), Left(Resource("http://example.org/")))))
		assertParse(predicateObjectsTemplate, "?x ?y ; ?a ?b",
			List((Right(Variable("?x")), Right(Variable("?y"))),
		         (Right(Variable("?a")), Right(Variable("?b")))))
		assertParse(predicateObjectsTemplate, "?x ?y , ?z ; ?a ?b",
			List((Right(Variable("?x")), Right(Variable("?y"))),
			     (Right(Variable("?x")), Right(Variable("?z"))),
		         (Right(Variable("?a")), Right(Variable("?b")))))
		assertFail (predicateObjectsTemplate, "")
		assertFail (predicateObjectsTemplate, "?x")
		assertFail (predicateObjectsTemplate, "?x , ?y")
		assertFail (predicateObjectsTemplate, "?x . ?y")
	}
	
	@Test def parseTriplesSameSubjectTemplate() {
		assertParse(triplesSameSubjectTemplate, "?x ?y ?z",
			List(TripleTemplate(Right(Variable("?x")), Right(Variable("?y")), Right(Variable("?z")))))
		assertParse(triplesSameSubjectTemplate, "?x <http://example.org/> \"hello\"",
			List(TripleTemplate(Right(Variable("?x")), Left(Resource("http://example.org/")), Left(Literal("hello")))))
		assertParse(triplesSameSubjectTemplate, "?x ?y ?z ; ?a ?b",
			List(TripleTemplate(Right(Variable("?x")), Right(Variable("?y")), Right(Variable("?z"))),
			     TripleTemplate(Right(Variable("?x")), Right(Variable("?a")), Right(Variable("?b")))))
		assertFail (triplesSameSubjectTemplate, "")
		assertFail (triplesSameSubjectTemplate, "?x")
		assertFail (triplesSameSubjectTemplate, "?x ?y")
		assertFail (triplesSameSubjectTemplate, "?x ?y ?a ?b")
		assertFail (triplesSameSubjectTemplate, "?x . ")
		assertFail (triplesSameSubjectTemplate, ".")
	}

	@Test def parseTemplate() {
		assertParse(template, "{ ?x ?y ?z . ?a ?b ?c }",
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