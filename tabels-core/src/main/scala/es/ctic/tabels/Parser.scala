package es.ctic.tabels

import scala.util.parsing.combinator._
import scala.util.parsing.input.CharSequenceReader

class TabelsParser extends JavaTokenParsers {
	
	// language terminal symbols

	def IN_CELL : Parser[String] = """(in +cell)|(IN +CELL)""".r
    
    def variable : Parser[Variable] = """\?[a-zA-Z][a-zA-Z0-9]*""".r ^^ Variable

    def ColToNumber(col : String) : Int = {
	  var num :Double = 0
	  var index :Int = col.length()
	  
	  col.foreach(c  =>{if(index>1) num += scala.math.pow(26 , index-1)*(c - 'A'+1); 
	  					else num += (c - 'A')
	  					index-=1;})
	  
	  println("num "+ num)
	  return num.toInt
	}
	
    def position : Parser[Position] = ("""[A-Z]+""".r ~ """[0-9]+""".r) ^^
		{ case c~r => new Position(row = r.toInt - 1, col = ColToNumber(c)) }

	// RDF

	def rdfLiteral : Parser[Literal] = stringLiteral ^^ { quotedString => Literal(quotedString.slice(1,quotedString.length-1)) } // FIXME: other literals

	def uriRef : Parser[Resource] = "<" ~> """[a-zA-Z0-9:#/\.\?\-]+""".r <~ ">" ^^ Resource // FIXME: use a better RE
	
	def rdfNode : Parser[RDFNode] = uriRef | rdfLiteral
	
	def eitherRDFNodeOrVariable : Parser[Either[RDFNode,Variable]] = rdfNode ^^ { Left(_) } | variable ^^ { Right (_) } // FIXME
	
	// language grammar
	
	def start : Parser[S] = rep(pattern)~rep(template) ^^ { case ps~ts => S(ps,ts) }
	
	def pattern : Parser[Pattern] = rep1(patternMatch) ^^ { Pattern(_) }

	def patternMatch : Parser[PatternMatch] = variable~(IN_CELL~>position) ^^
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

}
