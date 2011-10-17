package es.ctic.tabels

import es.ctic.tabels.Dimension._
import scala.util.matching.Regex
import es.ctic.tabels.RelativePos._
import es.ctic.tabels.TupleType._

import scala.util.parsing.combinator._
import scala.util.parsing.input.CharSequenceReader

import scala.collection._

import java.io.File

class TabelsParser extends JavaTokenParsers {

	val prefixes = mutable.HashMap.empty[String, Resource]
	
	// keywords

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
	def MATCHES = "matches".ignoreCase
	def WHILE = "while".ignoreCase
	def UNTIL = "until".ignoreCase
    def ADD = "add".ignoreCase
    
    // functions
	def RESOURCE = "resource".ignoreCase
    def TRUE = "true".ignoreCase
    def FALSE = "false".ignoreCase
    
    def variable : Parser[Variable] = """\?[a-zA-Z][a-zA-Z0-9]*""".r ^^ Variable
	
    def tupleType: Parser[TupleType] = AS ~> (HORIZONTAL|VERTICAL) ^^ {t => TupleType.withName(t.toLowerCase)}
    
    def position : Parser[Position] = ("""[A-Z]+""".r ~ """[0-9]+""".r) ^^
		{ case c~r => FixedPosition(row = r.toInt - 1, col = columnConverter.alphaToInt(c)) }|
		( variable) ^^
		{ case v => WithVariablePosition(v) }|
		(displacement ~ (LEFT|RIGHT|BOTTOM|TOP) ~ position) ^^
		{ case d~r~p => RelativePosition(RelativePos.withName(r.toLowerCase),p,d) }
		
	def displacement : Parser[Int] = opt("""[0-9]+""".r ^^ (_ toInt)) ^^ (_ getOrElse(1))
	
	def dimension : Parser[Dimension] = (ROWS|COLS|SHEETS|FILES) ^^ { d => Dimension.withName(d.toLowerCase) }
	
		
	// RDF

    def quotedString : Parser[String] =
         stringLiteral ^^ { s => s.slice(1,s.length-1) }
         
    def langTag : Parser[String] = """[a-zA-Z][a-zA-Z\-]*""".r

	def rdfLiteral : Parser[Literal] =
       quotedString ~ ("^^" ~> iriRef) ^^ { case value~rdfType => Literal(value, rdfType = rdfType) } |
       quotedString ~ ("@" ~> langTag) ^^ { case value~langTag => Literal(value, langTag = langTag) } |
       quotedString ^^ { value => Literal(value) } |
	   decimalNumber ^^ { asString => Literal(asString, rdfType = if (asString contains ".") XSD_DECIMAL else XSD_INT) } |
	   TRUE ^^ { x => LITERAL_TRUE } |
	   FALSE ^^ { x => LITERAL_FALSE }

	def iriRef : Parser[Resource] = "<" ~>  """([^<>"{}|^`\\\x00-\x20])*""".r <~ ">" ^^ Resource
	
	def curieRef : Parser[Resource] = (ident <~ ":") ~ ident ^^ { case prefix~local => prefixes(prefix) + local }
		
	def rdfNode : Parser[RDFNode] = iriRef | curieRef | rdfLiteral
	
	def eitherRDFNodeOrVariable : Parser[Either[RDFNode,Variable]] = rdfNode ^^ { Left(_) } | variable ^^ { Right (_) } // FIXME
	
	// language grammar
	
	def start : Parser[S] = rep(prefixDecl) ~ rep("{" ~> pattern <~"}") ~ rep(template) ^^
	   { case prefixes~ps~ts => S(prefixes,ps,ts) }
	
	def prefixDecl : Parser[(String,Resource)] = (PREFIX ~> ident) ~ (":" ~> iriRef) ^^
	    { case prefix~ns => prefixes += (prefix -> ns)
	                        (prefix -> ns)
	    }
	
	def pattern : Parser[Pattern] = bindingExpression ^^ {bind => Pattern(Left(bind))}|
		letWhereExpression ^^ { pm => Pattern(Right(pm))}

	def bindingExpression : Parser[BindingExpression] = (FOR ~> variable <~ IN) ~ dimension ~filterCondition~ stopCondition ~ rep(pattern) ^^
        { case v~d~f~s~p => BindingExpression(variable = v, dimension = d, childPatterns = p, filter = f, stopCond = s) }
       
	
	def letWhereExpression : Parser[LetWhereExpression] = 
		((LET ~> variable) ~ (((IN ~> CELL)|(PLACED ~> WITH)|(IS ~> LOCATED)) ~>opt(position)) ~ filterCondition)~ rep(pattern) ^^
        { case v~p~fc~pat => LetWhereExpression(tupleOrVariable = Right(v), position = p, filter = fc, childPatterns = pat) }|
        ((LET ~> tuple) ~opt(position) ~ opt(FILTER ~> expression))~ rep(pattern) ^^
        { case t~p~fc~pat => LetWhereExpression(tupleOrVariable = Left(t), position = p, filter = fc, childPatterns = pat) }|
        (LET ~> variable) ~ ("=" ~>expression)~ rep(pattern) ^^
        {case v1~exp~pat => LetWhereExpression(tupleOrVariable = Right(v1), expression = Some(exp),childPatterns = pat) }
	
    def regex : Parser[Regex] =
      ("""\"[^"]*\"""".r) ^^ {case r => new Regex( (r.drop(1)).dropRight(1) )}
    
    def stopCondition : Parser[Option[Expression]] =
      opt((WHILE ~> expression)|(UNTIL ~> expression)^^{case e =>NotExpression(expression = e) })
     
    def filterCondition : Parser[Option[Expression]] = 
      opt(FILTER ~> expression)
      
      
    def expression: Parser[Expression] = 
      ((RESOURCE <~"(") ~> expression )~ (","~> iriRef <~")") ^^ 
      		{case v~u => ResourceExpression(expression = v, uri = u)}|		
      variable ^^ VariableReference |
      ((MATCHES<~"(") ~>expression ~ (","~> regex <~")") ) ^^ 
      		{case e~r => RegexExpression(expression = e, re = r)}|
      ((ADD <~"(") ~>variable ~ (","~> expression <~")") ) ^^ 
      		{case v~e => AddVariableExpression(v, e)}|
      ("\""~>"""[a-zA-Z0-9 _]+""".r <~"\"") ^^ LiteralExpression
      		
      
    
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
            case NoSuccess(msg,next) => throw new ParseException(input, msg, next.pos.line, next.pos.column)
        }		
	}
	
	def parseProgram(input : String) : S = parse(start, input)
	
	def parseProgram(file : File) : S = {
	    val source = scala.io.Source.fromFile(file)
        val lines = source.mkString
        source.close()
        return parseProgram(lines)
	}

	// trick from http://stackoverflow.com/questions/6080437/case-insensitive-scala-parser-combinator
	
	class CaseInsensitiveKeyword(str: String) {
	  def ignoreCase: Parser[String] = ("""(?i)\Q""" + str + """\E""").r
	}

	implicit def pimpString(str: String): CaseInsensitiveKeyword = new CaseInsensitiveKeyword(str)	
	
	
}
