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
        assertParse(position, "3 Left OF ?y", RelativePosition(RelativePos.left, WithVariablePosition(Variable("?y")), 3))
        assertParse(position, "RIGHT of ?y", RelativePosition(RelativePos.right, WithVariablePosition(Variable("?y")), 1))
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
		assertParse(rdfLiteral, "3", Literal(3, rdfType = XSD_INT))
		assertParse(rdfLiteral, "3.1415", Literal(3.1415, rdfType = XSD_DECIMAL))
		assertParse(rdfLiteral, "true", LITERAL_TRUE)
		assertParse(rdfLiteral, "false", LITERAL_FALSE)
		assertFail (rdfLiteral, "")
		assertFail (rdfLiteral, "<http://example.org/>")
		assertFail (rdfLiteral, "?x")
		assertFail (rdfLiteral, "unquoted")
	}
	
	@Test def parsePath() {
	    assertParse(path, "\"C:\\foo\\load-1321975085368.html\"", """C:\foo\load-1321975085368.html""")
	    assertParse(path, "\"/private/var/folders/nz/76frgbr510s06zmt4q06lz9m0000gn/T/tabels/upload/download-1321975085368.html\"", "/private/var/folders/nz/76frgbr510s06zmt4q06lz9m0000gn/T/tabels/upload/download-1321975085368.html")
	    assertFail (path, "")
	}

	@Test def parseIriRef() {
		assertParse(iriRef, "<http://example.org/>", NamedResource("http://example.org/"))
		assertFail(iriRef, "http://example.org/")
		assertFail(iriRef, "<http://example.org/> <http://example.com>")
	}
	
	@Test def parseCurieRef() {
		prefixes += ("foo" -> NamedResource("http://example.org/"))
		assertParse(curieRef, "foo:bar", NamedResource("http://example.org/bar"))
		assertFail(curieRef,  "")
		assertFail(curieRef,  "foo")
		assertFail(curieRef,  "foo:")
		assertFail(curieRef,  ":bar") // FIXME: default namespace should be allowed
	}
	
	@Test def parseBlankNode() {
	    assertParse(blankNode, "[]", BlankNode(Right(0)))
	    assertParse(blankNode, "[]", BlankNode(Right(1))) // different id
	    assertParse(blankNode, "_:foo2", BlankNode(Left("foo2")))
	    assertFail(blankNode, "")
	}
	
	@Test def parseEitherRDFNodeOrVariable() {
		assertParse(eitherRDFNodeOrVariable, "\"hello\"", Left(Literal("hello")))
		assertParse(eitherRDFNodeOrVariable, "<http://example.org/>", Left(NamedResource("http://example.org/")))
		assertParse(eitherRDFNodeOrVariable, "?x", Right(Variable("?x")))
		assertFail (eitherRDFNodeOrVariable, "")
	}
	
	@Test def parseStart() {
		// FIXME: test some programs
		assertParse(start, "", S())
	}
	
	@Test def parseDirectives() {
	    assertParse(directives, "", Seq())
	}
	
	@Test def parseFetchDirective() {
//	    assertParse(fetchDirective, """@FETCH("dbpedia")""", FetchDirective("dbpedia".r))   // cannot compare regex
	    assertFail (fetchDirective, "@FETCH")
	}
	
	@Test def parseJenaRuleDirective() {
		assertParse(jenaRuleDirective, """@JENARULE("(?x ?y ?z) <- (?z ?y ?x)")""", JenaRuleDirective("(?x ?y ?z) <- (?z ?y ?x)"))
		assertFail (jenaRuleDirective, "@JENARULE")
	}
	
	@Test def parseSparqlDirective() {
		assertParse(sparqlDirective, """@SPARQL("DROP ALL")""", SparqlDirective("DROP ALL"))
		assertFail (sparqlDirective, "@SPARQL")
	}
	
	@Test def parseLoadDirective() {
	    assertParse(loadDirective, """@LOAD("http://example.org/data.rdf")""", LoadDirective("http://example.org/data.rdf"))
	    assertFail (loadDirective, "@LOAD")
	}
	
	@Test def parsePrefixDecl() {
		assertParse(prefixDecl, "PREFIX foo: <http://example.org/>", ("foo" -> NamedResource("http://example.org/")))
		assertEquals(NamedResource("http://example.org/"), prefixes("foo"))
		assertFail (prefixDecl, "")
		assertFail (prefixDecl, "PREFIX foo:")
		assertFail (prefixDecl, "PREFIX <http://example.org/>")
		assertFail (prefixDecl, "PREFIX : <http://example.org/>") // FIXME: default namespace should be allowed
	}
	
	@Test def parseTabelsStatements() {
		assertParse(tabelsStatement, "For ?y in rows \n For ?z in cols \n match ?x at A1", 
		    IteratorStatement(variable = Some(Variable("?y")), dimension = Dimension.rows, 
                nestedStatement = Some(IteratorStatement(variable = Some(Variable("?z")), dimension = Dimension.cols, 
                    nestedStatement = Some(MatchStatement(tuple = Tuple(Seq(Variable("?x"))), position = Some(FixedPosition(0,0))))))))
		    
        assertFail (tabelsStatement, "")
	}
	
    @Test def parseIteratorStatement(){
        assertParse(iteratorStatement, "For ?y in rows",
            IteratorStatement(variable = Some(Variable("?y")), dimension = Dimension.rows))
                           
        assertFail (matchStatement, "")
        assertFail (iteratorStatement, "For ?y")
    }

    @Test def parseSetInDimensionStatement {
        assertParse(setInDimensionStatement, "set files \"foo.csv\"", SetInDimensionStatement(Dimension.files, "foo.csv"))
        assertParse(setInDimensionStatement, "set ?x in sheets \"Sheet1\"", SetInDimensionStatement(Dimension.sheets, "Sheet1", variable = Some(Variable("?x"))))
        assertFail (setInDimensionStatement, "")
    }
    
    @Test def parseWhenConditionalStatement(){
        assertParse(whenConditionalStatement, "when can-be-double(?y) do",
            WhenConditionalStatement(condition = Some(Left(NumericFunctions.canBeDouble.createExpression(VariableReference(Variable("?y")))))))
                           
        assertFail (matchStatement, "")
        assertFail (iteratorStatement, "For ?y")
    }
    
    @Test def parseLetStatement {
        assertParse(letStatement, "let ?x = 10", LetStatement(Variable("?x"), LiteralExpression(Literal(10, XSD_INT))))
        assertFail (letStatement, "")
    }

    @Test def parseMatchStatement() {
        assertParse(matchStatement, "match ?X", MatchStatement(tuple = Tuple(Seq(Variable("?X"))), position = None))
        assertParse(matchStatement, "match ?X at A1", MatchStatement(tuple = Tuple(Seq(Variable("?X"))), position = Some(FixedPosition(0,0))))
        assertParse(matchStatement, "match [?X,?Y] IN VERTICAL at A1", MatchStatement(tuple = Tuple(Seq(Variable("?X"), Variable("?Y")), TupleType.vertical), position = Some(FixedPosition(0,0))))
        assertFail (matchStatement, "")
        assertFail (matchStatement, "?X")
        assertFail (matchStatement, "A1")
        assertFail (matchStatement, "IN CELL")
        assertFail (matchStatement, "IN CELL A1")
    }
    
    @Test def parseRegex(){
      assertFail(regex, "\"(hola\"")
    }
     
    @Test def parseExpression(){	    
      //VariableReference
        assertParse(expression, "?y", VariableReference(Variable("?y")))
        assertFail(expression, "y")
        
        // LiteralExpression
        assertParse(expression, "10", LiteralExpression(Literal(10, XSD_INT)))
        assertParse(expression, "3.14", LiteralExpression(Literal(3.14, XSD_DECIMAL)))
        assertParse(expression, "\"hello\"", LiteralExpression(Literal("hello")))
     }
     
     @Test def parseFunctionExpression() {  
        //ResourceExpression
      	assertParse(expression, "RESOURCE(?y, <http://ontorule-project.eu/resources/steeldata#coil>)", ResourceExpression(expression = VariableReference(Variable("?y")), uri =NamedResource("http://ontorule-project.eu/resources/steeldata#coil") ))
          assertParse(expression, "RESourCE (?y, <http://ontorule-project.eu/resources/steeldata#coil>)", ResourceExpression(expression = VariableReference(Variable("?y")), uri =NamedResource("http://ontorule-project.eu/resources/steeldata#coil") ))
          assertFail(expression, "RESourC (?y, <http://ontorule-project.eu/resources/steeldata#coil>)")
          assertFail(expression, "RESOURCE (y, <http://ontorule-project.eu/resources/steeldata#coil>)")
          assertFail(expression, "RESOURCE (?y, http://ontorule-project.eu/resources/steeldata#coil>)")

       // isResourceExpression
       //assertParse(expression,"IS_RESOURCE(?y)", IsResourceExpression(expression = VariableReference(Variable("?y"))))

      //RegexExpression
        //This test can't be done because Regex does not support the equal operation between regular expressions
        //assertParse(expression, "matches (?y, \"hola\")", RegexExpression(VariableReference(Variable("?y")),new Regex("hola")))
        assertFail(expression, "matches (?y, \"(hola\")")        
   }
   
    @Test def parseTuple {
        assertParse(tuple, "[?a]", Tuple(Seq(Variable("?a")), TupleType.horizontal))
        assertParse(tuple, "[?a, ?b] in vertical", Tuple(Seq(Variable("?a"), Variable("?b")), TupleType.vertical))
        assertFail (tuple, "")
    }
    
    @Test def parseBlankNodeBlock() {
		
		assertParse(blankNodeBlock, "[ ?y ?z ]",
			List(TripleTemplate(Left(BlankNode(Right(0))), Right(Variable("?y")), Right(Variable("?z")))))
		assertParse(blankNodeBlock, "[ ?y ?z ; ?a ?b ]",
 			List(TripleTemplate(Left(BlankNode(Right(1))), Right(Variable("?y")), Right(Variable("?z"))),
 			     TripleTemplate(Left(BlankNode(Right(1))), Right(Variable("?a")), Right(Variable("?b")))))
 	    assertParse(blankNodeBlock, "[ ?y ?z ; ?a ?b .]",
 			List(TripleTemplate(Left(BlankNode(Right(2))), Right(Variable("?y")), Right(Variable("?z"))),
 			     TripleTemplate(Left(BlankNode(Right(2))), Right(Variable("?a")), Right(Variable("?b")))))
 		
		assertFail (blankNodeBlock, "[]")
		assertFail (blankNodeBlock, "[ ?x ]")
		assertFail (triplesSameSubjectTemplate, ".")
	}
    
	@Test def parseVerbTemplate() {
		prefixes += ("foo" -> NamedResource("http://example.org/"))
		assertParse(verbTemplate, "<http://example.org/>", Left(NamedResource("http://example.org/")))
		assertParse(verbTemplate, "a", Left(RDF_TYPE))
		assertParse(verbTemplate, "foo:bar", Left(NamedResource("http://example.org/bar")))
		assertParse(verbTemplate, "?x", Right(Variable("?x")))
		assertFail (verbTemplate, "")
		assertFail (verbTemplate, "\"hello\"")
	}
	
	@Test def parseObjectsTemplate() {
		assertParse(objectsTemplate, "?x", (List(Right(Variable("?x"))), List()))
		assertParse(objectsTemplate, "<http://example.org/>", (List(Left(NamedResource("http://example.org/"))), List()))
		assertParse(objectsTemplate, "?x, ?y", (List(Right(Variable("?x")), Right(Variable("?y"))), List()))
		assertParse(objectsTemplate, "[ ?y ?z ]",(List(Left(BlankNode(Right(0)))), List(TripleTemplate(Left(BlankNode(Right(0))), Right(Variable("?y")), Right(Variable("?z"))))))
			
		assertFail (objectsTemplate, "")
		assertFail (objectsTemplate, "?x ?y")
		assertFail (objectsTemplate, "?x ; ?y")
		assertFail (objectsTemplate, "?x . ?y")
	}
	
	@Test def parsePredicateObjectsTemplate() {
		assertParse(predicateObjectsTemplate, "?x ?y",
			(List((Right(Variable("?x")), Right(Variable("?y")))), List()))
		assertParse(predicateObjectsTemplate, "<http://example.org/> \"Hello\"",
			(List((Left(NamedResource("http://example.org/")), Left(Literal("Hello")))), List()))
		assertParse(predicateObjectsTemplate, "a <http://example.org/>",
			(List((Left(RDF_TYPE), Left(NamedResource("http://example.org/")))), List()))
		assertParse(predicateObjectsTemplate, "?x ?y ; ?a ?b",
			(List((Right(Variable("?x")), Right(Variable("?y"))),
		         (Right(Variable("?a")), Right(Variable("?b")))), 
		         List()))
		assertParse(predicateObjectsTemplate, "?x ?y , ?z ; ?a ?b",
			(List((Right(Variable("?x")), Right(Variable("?y"))),
			     (Right(Variable("?x")), Right(Variable("?z"))),
		         (Right(Variable("?a")), Right(Variable("?b")))), List()))
		assertParse(predicateObjectsTemplate, "?x [ ?y ?z ]",
			(List((Right(Variable("?x")), Left(BlankNode(Right(0))))), List(TripleTemplate(Left(BlankNode(Right(0))), Right(Variable("?y")), Right(Variable("?z"))))))         
		
		assertFail (predicateObjectsTemplate, "")
		assertFail (predicateObjectsTemplate, "?x")
		assertFail (predicateObjectsTemplate, "?x , ?y")
		assertFail (predicateObjectsTemplate, "?x . ?y")
	}
	
	@Test def parseTriplesSameSubjectTemplate() {
		assertParse(triplesSameSubjectTemplate, "?x ?y ?z",
			List(TripleTemplate(Right(Variable("?x")), Right(Variable("?y")), Right(Variable("?z")))))
		assertParse(triplesSameSubjectTemplate, "[ ?y ?z ]",
			List(TripleTemplate(Left(BlankNode(Right(0))), Right(Variable("?y")), Right(Variable("?z")))))
		assertParse(triplesSameSubjectTemplate, "?x <http://example.org/> \"hello\"",
			List(TripleTemplate(Right(Variable("?x")), Left(NamedResource("http://example.org/")), Left(Literal("hello")))))
		assertParse(triplesSameSubjectTemplate, "?x ?y ?z ; ?a ?b",
			List(TripleTemplate(Right(Variable("?x")), Right(Variable("?y")), Right(Variable("?z"))),
			     TripleTemplate(Right(Variable("?x")), Right(Variable("?a")), Right(Variable("?b")))))
		assertParse(triplesSameSubjectTemplate, "[ ?y ?z ; ?a ?b ]",
 			List(TripleTemplate(Left(BlankNode(Right(1))), Right(Variable("?y")), Right(Variable("?z"))),
 			     TripleTemplate(Left(BlankNode(Right(1))), Right(Variable("?a")), Right(Variable("?b")))))
 		assertParse(triplesSameSubjectTemplate, "?x ?c ?d; ?e [ ?y ?z ; ?a ?b ]",
 			List(TripleTemplate(Left(BlankNode(Right(2))), Right(Variable("?y")), Right(Variable("?z"))),
 			     TripleTemplate(Left(BlankNode(Right(2))), Right(Variable("?a")), Right(Variable("?b"))),
 			     TripleTemplate(Right(Variable("?x")), Right(Variable("?c")), Right(Variable("?d"))),
 			     TripleTemplate(Right(Variable("?x")),Right(Variable("?e")) ,Left(BlankNode(Right(2))))
 			     ))
		assertFail (triplesSameSubjectTemplate, "")
		assertFail (triplesSameSubjectTemplate, "[]")
		assertFail (triplesSameSubjectTemplate, "?x")
		assertFail (triplesSameSubjectTemplate, "[ ?x ]")
		assertFail (triplesSameSubjectTemplate, "?x ?y")
		assertFail (triplesSameSubjectTemplate, "?x ?y ?a ?b")
		assertFail (triplesSameSubjectTemplate, "?x . ")
		assertFail (triplesSameSubjectTemplate, ".")
	}

	@Test def parseTemplate() {
		assertParse(template, "{ ?x ?y ?z . [] ?b [] }",
			Template(Seq(TripleTemplate(Right(Variable("?x")), Right(Variable("?y")), Right(Variable("?z"))),
			              TripleTemplate(Left(BlankNode(Right(0))), Right(Variable("?b")), Left(BlankNode(Right(1)))))))
		assertParse(template, "{ ?x ?y ?z .}",
			Template(Seq(TripleTemplate(Right(Variable("?x")), Right(Variable("?y")), Right(Variable("?z"))))))
		assertParse(template, "{ ?x ?y ?z }",
			Template(Seq(TripleTemplate(Right(Variable("?x")), Right(Variable("?y")), Right(Variable("?z"))))))
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