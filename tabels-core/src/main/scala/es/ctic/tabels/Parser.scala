package es.ctic.tabels

import es.ctic.tabels.Dimension._
import es.ctic.tabels.RelativePos._
import es.ctic.tabels.TupleType._
import es.ctic.tabels.Operator._

import scala.util.parsing.combinator._
import scala.util.parsing.input.CharSequenceReader

import scala.collection._

class TabelsParser extends JavaTokenParsers {

	val prefixes = mutable.HashMap.empty[String, Resource]
	
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
	def RESOURCE = "resource".ignoreCase
	def A = "a".ignoreCase
	def PREFIX = "prefix".ignoreCase
	def PLACED = "placed".ignoreCase
	def WITH = "with".ignoreCase
	def IS = "is".ignoreCase
	def LOCATED = "located".ignoreCase
	def LEFT = "left".ignoreCase
	def RIGHT = "right".ignoreCase
	def TOP = "top".ignoreCase
	def BOTTOM = "bottom".ignoreCase
    
    def variable : Parser[Variable] = """\?[a-zA-Z][a-zA-Z0-9]*""".r ^^ Variable
	
    def tupleType: Parser[TupleType] = AS ~> (HORIZONTAL|VERTICAL) ^^ {t => TupleType.withName(t.toLowerCase)}
    
    def position : Parser[Position] = ("""[A-Z]+""".r ~ """[0-9]+""".r) ^^
		{ case c~r => FixedPosition(row = r.toInt - 1, col = columnConverter.alphaToInt(c)) }|
		((PLACED ~> WITH) ~> variable) ^^
		{ case v => WithVariablePosition(v) }|
		((IS ~> LOCATED)~>(LEFT|RIGHT|BOTTOM|TOP) ~ position) ^^
		{ case r~p => RelativePosition(RelativePos.withName(r),p,1) }
	
	def dimension : Parser[Dimension] = (ROWS|COLS|SHEETS|FILES) ^^ { d => Dimension.withName(d.toLowerCase) }
	
		
	//FIX ME: I accept everything as a regular expression. Use a better RE
	def filterCondition : Parser[FilterCondition] = (IGNORE~>BLANKS)^^{b => FilterCondition("""\s*""")}|
	((FILTER ~> BY) ~> """.*""".r) ^^ { re => FilterCondition(re)}

	// RDF

	def rdfLiteral : Parser[Literal] = stringLiteral ^^ { quotedString => Literal(quotedString.slice(1,quotedString.length-1)) } // FIXME: other literals

	def iriRef : Parser[Resource] = "<" ~>  """([^<>"{}|^`\\\x00-\x20])*""".r <~ ">" ^^ Resource
	
	def curieRef : Parser[Resource] = (ident <~ ":") ~ ident ^^ { case prefix~local => prefixes(prefix) + local }
		
	def rdfNode : Parser[RDFNode] = iriRef | curieRef | rdfLiteral
	
	def eitherRDFNodeOrVariable : Parser[Either[RDFNode,Variable]] = rdfNode ^^ { Left(_) } | variable ^^ { Right (_) } // FIXME
	
	// language grammar
	
	def start : Parser[S] = rep(prefixDecl)~>rep(pattern)~rep(template) ^^ { case ps~ts => S(ps,ts) }
	
	def prefixDecl = (PREFIX ~> ident) ~ (":" ~> iriRef) ^^ { case prefix~ns => prefixes += (prefix -> ns) }
	
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
	
    def expression: Parser[Expression] = ((RESOURCE <~"(") ~> variable )~ (","~> iriRef <~")") ^^ 
    {case v~u => ResourceExpression(variable = v, uri = u)}|
    variable ^^ VariableReference
    
    
    def tuple : Parser[Tuple] = ((TUPLE <~ "[") ~> (rep1sep(variable,",")<~ "]"))  ~ tupleType   ^^
    	{case vs~tt => Tuple(vs,tt)}
    
    def verbTemplate : Parser[Either[RDFNode, Variable]] =
		iriRef ^^ { Left(_) } |
		curieRef ^^ { Left(_) } |
		A ^^ { _ => Left(RDF_TYPE) } |
		variable ^^ { Right (_) }
       
	def objectsTemplate : Parser[List[Either[RDFNode, Variable]]] = rep1sep(eitherRDFNodeOrVariable, ",")

	def predicateObjectsTemplate : Parser[List[Tuple2[ Either[RDFNode, Variable], Either[RDFNode, Variable] ]]] =
		rep1sep((verbTemplate ~ objectsTemplate) ^^ { case pred~objs => for (obj <- objs) yield (pred,obj) }, ";") ^^ { _ flatten }

	def triplesSameSubjectTemplate : Parser[List[TripleTemplate]] =
	  eitherRDFNodeOrVariable~predicateObjectsTemplate ^^
	  { case subj~predObjs => for ((pred,obj) <- predObjs) yield TripleTemplate(subj,pred,obj) }
	
	def template : Parser[Template] = "{" ~> rep1sep(triplesSameSubjectTemplate, ".") <~ "}" ^^
	  { triples => Template((triples flatten) toSet) }

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
