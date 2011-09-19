package es.ctic.tabels

import scala.util.parsing.combinator._
import scala.util.parsing.input.CharSequenceReader

class TabelsParser extends JavaTokenParsers {

	// language terminal symbols

	def CELL = "cell".ignoreCase
	def FOR  = "for".ignoreCase
	def IN   = "in".ignoreCase
	def ROWS = "rows".ignoreCase
	def COLS = "cols".ignoreCase
	def SHEETS = "sheets".ignoreCase // aka: "tabs"
	def FILES = "files".ignoreCase
    
    def variable : Parser[Variable] = """\?[a-zA-Z][a-zA-Z0-9]*""".r ^^ Variable
	
    def position : Parser[Position] = ("""[A-Z]+""".r ~ """[0-9]+""".r) ^^
		{ case c~r => new Position(row = r.toInt - 1, col = columnConverter.alphaToInt(c)) }
	
	def dimension : Parser[Dimension] = (ROWS|COLS|SHEETS|FILES) ^^ Dimension

	// RDF

	def rdfLiteral : Parser[Literal] = stringLiteral ^^ { quotedString => Literal(quotedString.slice(1,quotedString.length-1)) } // FIXME: other literals

	def uriRef : Parser[Resource] = "<" ~> """[a-zA-Z0-9:#/\.\?\-]+""".r <~ ">" ^^ Resource // FIXME: use a better RE
	
	def rdfNode : Parser[RDFNode] = uriRef | rdfLiteral
	
	def eitherRDFNodeOrVariable : Parser[Either[RDFNode,Variable]] = rdfNode ^^ { Left(_) } | variable ^^ { Right (_) } // FIXME
	
	// language grammar
	
	def start : Parser[S] = rep(pattern)~rep(template) ^^ { case ps~ts => S(ps,ts) }
	
	//FIX ME : This code requires both patternMatch AND Binding Expression 
	def pattern : Parser[Pattern] = rep1(""~>bindingExpresion) ^^ { case be => Pattern(lBindE = be)}|
	rep1(""~>patternMatch) ^^ { pm => Pattern(lPatternM = pm, lBindE=List())}

	def bindingExpresion : Parser[BindingExpresion] = (FOR ~> variable <~ IN) ~ dimension ~ rep1(bindingExpresion) ^^
        { case v~d~p => BindingExpresion(variable = v, dim = d,lBindE = p) }|
        ("For" ~> variable <~ "in") ~ dimension ~ rep1(patternMatch) ^^
        { case v~d~p => BindingExpresion(variable = v, dim = d,lPatternM = p, lBindE=List()) }
       
	
	def patternMatch : Parser[PatternMatch] = variable ~ (IN ~> CELL ~> position) ^^
        { case v~p => PatternMatch(variable = v, position = p) }

	def tripleTemplate : Parser[TripleTemplate] = eitherRDFNodeOrVariable~eitherRDFNodeOrVariable~eitherRDFNodeOrVariable<~"." ^^ { case s~p~o => TripleTemplate(s, p, o) }
	
	def template : Parser[Template] = "{" ~> rep1(tripleTemplate) <~ "}" ^^ Template

	// parsing methods
	
	protected def parse[T](p : Parser[T], input: String) : T = {
        val phraseParser = phrase(p)
        phraseParser(new CharSequenceReader(input)) match {
            case Success(t,_)     => t
            case NoSuccess(msg,_) => throw new IllegalArgumentException("Could not parse '" + input + "': " + msg)
        }		
	}
	
	def parseProgram(input : String) : S = parse(start, input)


	// trick from http://stackoverflow.com/questions/6080437/case-insensitive-scala-parser-combinator
	
	class CaseInsensitiveKeyword(str: String) {
	  def ignoreCase: Parser[String] = ("""(?i)\Q""" + str + """\E""").r
	}

	implicit def pimpString(str: String): CaseInsensitiveKeyword = new CaseInsensitiveKeyword(str)	
	
	
}
