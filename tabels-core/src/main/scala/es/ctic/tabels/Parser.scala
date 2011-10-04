package es.ctic.tabels

import es.ctic.tabels.Dimension._
import es.ctic.tabels.TupleType._

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
	def IGNORE = "ignore".ignoreCase
	def BLANKS = "blanks".ignoreCase
	def FILTER = "filter".ignoreCase
	def BY = "by".ignoreCase
	def TUPLE = "@tuple".ignoreCase
	def AS = "as".ignoreCase
	def HORIZONTAL = "horizontal".ignoreCase
	def VERTICAL = "vertical".ignoreCase
	def LET = "let".ignoreCase
    
    def variable : Parser[Variable] = """\?[a-zA-Z][a-zA-Z0-9]*""".r ^^ Variable
	
    def tupleType: Parser[TupleType] = AS ~> (HORIZONTAL|VERTICAL) ^^ {t => TupleType.withName(t.toLowerCase)}
    
    def position : Parser[Position] = ("""[A-Z]+""".r ~ """[0-9]+""".r) ^^
		{ case c~r => new Position(row = r.toInt - 1, col = columnConverter.alphaToInt(c)) }
	
	def dimension : Parser[Dimension] = (ROWS|COLS|SHEETS|FILES) ^^ { d => Dimension.withName(d.toLowerCase) }
	
	//FIX ME: I accept everything as a regular expression. Use a better RE
	def filterCondition : Parser[FilterCondition] = (IGNORE~>BLANKS)^^{b => FilterCondition("""\s*""")}|
	((FILTER ~> BY) ~> """.*""".r) ^^ { re => FilterCondition(re)}

	// RDF

	def rdfLiteral : Parser[Literal] = stringLiteral ^^ { quotedString => Literal(quotedString.slice(1,quotedString.length-1)) } // FIXME: other literals

	def uriRef : Parser[Resource] = "<" ~> """[a-zA-Z0-9:#/\.\?\-]+""".r <~ ">" ^^ Resource // FIXME: use a better RE
	
	def rdfNode : Parser[RDFNode] = uriRef | rdfLiteral
	
	def eitherRDFNodeOrVariable : Parser[Either[RDFNode,Variable]] = rdfNode ^^ { Left(_) } | variable ^^ { Right (_) } // FIXME
	
	// language grammar
	
	def start : Parser[S] = rep(pattern)~rep(template) ^^ { case ps~ts => S(ps,ts) }
	
	def pattern : Parser[Pattern] = bindingExpression ^^ {bind => Pattern(Left(bind))}|
		letWhereExpression ^^ { pm => Pattern(Right(pm))}

	def bindingExpression : Parser[BindingExpression] = (FOR ~> variable <~ IN) ~ dimension ~ rep(pattern) ^^
        { case v~d~p => BindingExpression(variable = v, dimension = d, childPatterns = p) }
       
	
	def letWhereExpression : Parser[LetWhereExpression] = 
		((LET ~> variable) ~ (IN ~> CELL ~>opt(position)) ~ rep(filterCondition))~ rep(pattern) ^^
        { case v~p~fc~pat => LetWhereExpression(tupleOrVariable = Right(v), position = p, filterCondList= fc, childPatterns = pat) }|
        ((LET ~> tuple) ~opt(position) ~ rep(filterCondition))~ rep(pattern) ^^
        { case t~p~fc~pat => LetWhereExpression(tupleOrVariable = Left(t), position = p, filterCondList= fc, childPatterns = pat) }|
        (LET ~> variable) ~ ("=" ~>expression)~ rep(pattern) ^^
        {case v1~exp~pat => LetWhereExpression(tupleOrVariable = Right(v1), expression = Some(exp),childPatterns = pat) }
	
    def expression: Parser[Expression] = ("RESOURCE(" ~> variable )~ (","~> """[a-zA-Z0-9:#/\.\?\-]+""".r <~")") ^^ 
    {case v~u => Expression(v, u)}
    
    
    def tuple : Parser[Tuple] = ((TUPLE <~ "[") ~> (rep1sep(variable,",")<~ "]"))  ~ tupleType   ^^
    	{case vs~tt => Tuple(vs,tt)}
    
   
    def tripleTemplate : Parser[TripleTemplate] = eitherRDFNodeOrVariable~eitherRDFNodeOrVariable~eitherRDFNodeOrVariable<~"." ^^ { case s~p~o => TripleTemplate(s, p, o) }
	
	def template : Parser[Template] = "{" ~> rep1(tripleTemplate) <~ "}" ^^ (triples => Template(triples toSet))

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
