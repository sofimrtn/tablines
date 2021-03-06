package es.ctic.tabels

import es.ctic.tabels.Dimension._
import scala.util.matching.Regex
import es.ctic.tabels.TupleType._

import StringFunctions._

import scala.util.parsing.combinator._
import scala.util.parsing.input.CharSequenceReader

import scala.collection._

import java.io.File
import grizzled.slf4j.Logging
class TabelsParser extends JavaTokenParsers with Logging{

	val prefixes = mutable.HashMap.empty[String, NamedResource]
	var blankNodeId : Int = 0
	
	def createFreshBlankNode() : BlankNode = {
	    val blankNode = BlankNode(Right(blankNodeId))
	    blankNodeId += 1
	    return blankNode
    }
	
	// regular expression from http://stackoverflow.com/questions/5952720/ignoring-c-style-comments-in-a-scala-combinator-parser
    protected override val whiteSpace = """(\s|//.*|#.*|(?m)/\*(\*(?!/)|[^*])*\*/)+""".r

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
	def AS = "as".ignoreCase
	def AT = "at".ignoreCase
	def OF = "of".ignoreCase
	def HORIZONTAL = "horizontal".ignoreCase
	def VERTICAL = "vertical".ignoreCase
	def LET = "let".ignoreCase
	def A = "a".ignoreCase
	def PREFIX = "prefix".ignoreCase
	def FETCH = "fetch".ignoreCase
	def JENARULE = "jenarule".ignoreCase
	def SPARQL = "sparql".ignoreCase
	def LOAD = "load".ignoreCase
//	def PLACED = "placed".ignoreCase
//	def WITH = "with".ignoreCase
//	def IS = "is".ignoreCase
//	def LOCATED = "located".ignoreCase
	def LEFT = "left".ignoreCase
	def RIGHT = "right".ignoreCase
	def TOP = "top".ignoreCase
	def BOTTOM = "bottom".ignoreCase
	def MATCHES = "matches".ignoreCase
	def WHILE = "while".ignoreCase
	def UNTIL = "until".ignoreCase
	def MATCH = "match".ignoreCase
    def SET = "set".ignoreCase
    def DO = "do".ignoreCase
    
    // functions
	def RESOURCE = "resource".ignoreCase
  def IS_RESOURCE = "is-resource".ignoreCase
	def GET_ROW = "get-row".ignoreCase
	def GET_COL = "get-col".ignoreCase
    def ADD = "add".ignoreCase
    def TRUE = "true".ignoreCase
    def FALSE = "false".ignoreCase
    def CONCAT = "concat".ignoreCase
    def INT = "int".ignoreCase
    def DECIMAL = "decimal".ignoreCase
    def FLOAT = "float".ignoreCase
    def BOOLEAN = "boolean".ignoreCase
    def STRING_JOIN = "string-join".ignoreCase
    def SUBSTRING = "substring".ignoreCase
    def STRING_LENGTH = "string-length". ignoreCase
    def CONTAINS = "contains".ignoreCase
    def STARTS_WITH = "starts-with".ignoreCase
    def ENDS_WITH = "ends-with".ignoreCase
    def SUBSTRING_BEFORE = "substring-before".ignoreCase
    def SUBSTRING_AFTER = "substring-after".ignoreCase
    def REPLACE = "replace".ignoreCase
    def CASE = "case".ignoreCase
    def UPPER = "upper".ignoreCase
    def LOWER = "lower".ignoreCase
    def TRANSLATE = "translate".ignoreCase
    def NUMERIC_ADD = "numeric-add".ignoreCase
    def NUMERIC_SUBSTRACT = "numeric-substract".ignoreCase
    def NUMERIC_MULTIPLY = "numeric-multiply".ignoreCase
    def NUMERIC_DIVIDE = "numeric-divide".ignoreCase
    def NUMERIC_INTEGER_DIVIDE = "numeric-integer-divide".ignoreCase
    def INTEGER = "integer".ignoreCase
    def NUMERIC_EQUAL = "numeric-equal".ignoreCase
    def NUMERIC_GREATER_THAN = "numeric-greater-than".ignoreCase
    def NUMERIC_LESS_THAN = "numeric-less-than".ignoreCase
    def NUMERIC_MOD = "numeric-mod".ignoreCase
    def IF = "if".ignoreCase
    def THEN = "then".ignoreCase
    def ELSE = "else".ignoreCase
    def NOT = "not".ignoreCase
    def AND = "and".ignoreCase
    def OR = "or".ignoreCase
   // def DBPEDIA_DISAMBIGUATION = "dbpedia-disambiguation".ignoreCase
    def LEVENSHTEIN_DISTANCE = "levenshtein-distance".ignoreCase
    def ABS = "abs".ignoreCase
    def FLOOR = "floor".ignoreCase
    def CEIL = "ceil".ignoreCase
    def ROUND = "round".ignoreCase
    def STARTS = "starts".ignoreCase
    def WHEN = "when".ignoreCase
    
    def WINDOWED = "windowed".ignoreCase
    def CONSTRUCT = "construct".ignoreCase
    
	def parens[X] (innerParser : Parser[X]) : Parser[X] = "(" ~> innerParser <~ ")"

    def variable : Parser[Variable] = """\?[a-zA-Z][a-zA-Z0-9_]*""".r ^^ Variable
	
    def tupleType: Parser[TupleType] = (HORIZONTAL|VERTICAL) ^^ {t => TupleType.withName(t.toLowerCase)}
    
	def displacement : Parser[Int] = opt("""[0-9]+""".r ^^ (_ toInt)) ^^ (_ getOrElse(1))
	
	def dimension : Parser[Dimension] = (ROWS|COLS|SHEETS|FILES) ^^ { d => Dimension.withName(d.toLowerCase) }
	
    def position : Parser[Position] =
        ("""[A-Z]+""".r ~ """[0-9]+""".r) ^^
		{ case c~r => FixedPosition(row = r.toInt - 1, col = columnConverter.alphaToInt(c)) }|
		variable ^^
		{ case v => WithVariablePosition(v) }|
		displacement ~ ((LEFT|RIGHT|BOTTOM|TOP) <~ OF) ~ position ^^
		{ case d~r~p => RelativePosition(RelativePos.withName(r.toLowerCase),p,d) }
		
		
	// RDF

    
	def quotedString : Parser[String] =
         /*stringLiteral*/""""(\\["\\]|[^"\n])*"""".r ^^ { s => s.slice(1,s.length-1).replaceAll("""\\"""",""""""").replaceAll("""\\\\""","""\\""") }
         
    def langTag : Parser[String] = """[a-zA-Z][a-zA-Z\-]*""".r

	def rdfLiteral : Parser[Literal] =
       quotedString ~ ("^^" ~> iriRef) ^^ { case value~rdfType => Literal(value, rdfType = rdfType) } |
       quotedString ~ ("@" ~> langTag) ^^ { case value~langTag => Literal(value, langTag = langTag) } |
       quotedString ^^ { value => Literal(value) } |
	   decimalNumber ^^ { asString => if (asString contains ".") Literal(asString.toDouble, XSD_DECIMAL) else Literal(asString.toInt, XSD_INT) } |
	   TRUE ^^ { x => LITERAL_TRUE } |
	   FALSE ^^ { x => LITERAL_FALSE }

	def path : Parser[String] =  "\"" ~> """([^\"]+)""".r <~"\""  
	
	def iriRef : Parser[NamedResource] = "<" ~>  """([^<>"{}|^`\\\x00-\x20])*""".r <~ ">" ^^ NamedResource
		
	//FIXME: Curi nodes not support sparql specification 
	def curieRef : Parser[NamedResource] = (definedPrefix <~ ":") ~ ("""[a-zA-Z\-0-9]+[a-zA-Z\-\.?=_]*""".r) ^^
	    { case prefix~local =>  prefix + local }
	
	def definedPrefix : Parser[NamedResource] = (ident ) ^^
	    { case prefix=> if (prefixes.contains(prefix)) { prefixes(prefix) } else { throw new UndefinedPrefixException(prefix) } }
	   
	
	def blankNode : Parser[BlankNode] =
	    "[]" ^^ { _ => createFreshBlankNode() } |
	    "_"~>":" ~> ident ^^ { internalId => BlankNode(Left(internalId)) }
			
	def rdfNode : Parser[RDFNode] = iriRef | blankNode| curieRef  | rdfLiteral 
	
	def eitherRDFNodeOrVariable : Parser[Either[RDFNode,Variable]] = rdfNode ^^ { Left(_) } | variable ^^ { Right (_) } // FIXME
	
	// language grammar
	
	def start : Parser[S] = directives ~ rep(prefixDecl) ~ rep(tabelsStatement) ~ rep(CONSTRUCT ~> template) ^^
	   { case directiv~prefixes~ps~ts => S(directiv,prefixes,ps,ts) }
	   
	def directives : Parser[Seq[Directive]] = rep(directive)
	
	def directive : Parser[Directive] =
		fetchDirective | jenaRuleDirective | loadDirective | sparqlDirective
		
	def fetchDirective = "@" ~> FETCH ~> parens(regex) ^^ { FetchDirective(_) }
	
	def jenaRuleDirective = "@" ~> JENARULE ~> parens(quotedString) ^^ { JenaRuleDirective(_) }
	
	def loadDirective = "@" ~> LOAD ~> parens(quotedString) ^^ { LoadDirective(_) }
	
	def sparqlDirective = "@" ~> SPARQL ~> parens(quotedString) ^^ { SparqlDirective(_) }
	
	def prefixDecl : Parser[(String,NamedResource)] = (PREFIX ~> ident) ~ (":" ~> iriRef) ^^
	    { case prefix~ns => prefixes += (prefix -> ns)
	                        (prefix -> ns)
	    }
	
	def tabelsStatement : Parser[TabelsStatement] = blockStatement | iteratorStatement | setInDimensionStatement | whenConditionalStatement | letStatement | matchStatement 
	
	def blockStatement : Parser[BlockStatement] = "{" ~> rep1sep(tabelsStatement, ";") <~ "}" ^^ { BlockStatement(_) }
	
	def iteratorStatement : Parser[IteratorStatement] = (FOR ~> opt(variable <~ IN)) ~ dimension ~ startCondition ~filterCondition~ stopCondition ~ opt(tabelsStatement) ^^
        { case v~d~b~f~s~p => IteratorStatement(variable = v, dimension = d,startCond =b, filter = f, stopCond = s, nestedStatement = p) }|
        NOT~>WINDOWED~>(FOR ~> opt(variable <~ IN)) ~ dimension ~ startCondition ~filterCondition~ stopCondition ~ opt(tabelsStatement) ^^
        { case v~d~b~f~s~p => IteratorStatement(variable = v, dimension = d,startCond =b, filter = f, stopCond = s, nestedStatement = p,windowed = false) }
    def whenConditionalStatement : Parser[WhenConditionalStatement] = ((WHEN ~> condition) <~ DO) ~ opt(tabelsStatement)^^
        { case c ~ s => WhenConditionalStatement(condition =c,  nestedStatement = s) }
    def setInDimensionStatement : Parser[SetInDimensionStatement] = (SET ~> opt(variable <~ IN) ~ dimension) ~ path ~ opt(tabelsStatement) ^^
        { case v~d~s~p => SetInDimensionStatement(variable = v, dimension = d, fixedDimension = s, nestedStatement = p) }
      
	
    def letStatement : Parser[LetStatement] =
      (LET ~> variable) ~ ("=" ~>expression) ~ opt(tabelsStatement) ^^
          {case v1~exp~pat => LetStatement(variable = v1, expression = exp, nestedStatement = pat) }

    def matchStatement : Parser[MatchStatement] = 
		  (MATCH ~> variableOrTuple) ~ opt(AT ~> position) ~ filterCondition ~ opt(tabelsStatement) ^^
        { case t~p~fc~pat => MatchStatement(tuple = t, position = p, filter = fc, nestedStatement = pat) }
        
    def variableOrTuple : Parser[Tuple] = variable ^^ { v => Tuple(Seq(v)) } | tuple
    
    def regex : Parser[Regex] =
      (""""(\\[^n]|[^"\n])*"""".r) ^^ {case r => new Regex( (r.drop(1)).dropRight(1) )}
    
    def startCondition : Parser[Option[Either[Expression,Position]]] =
      opt(STARTS ~>((WHEN ~> expression)^^{Left(_)}|(AT ~> position)^^{Right(_)}))
    
    def stopCondition : Parser[Option[Expression]] =
      opt((WHILE ~> expression)|(UNTIL ~> expression)^^{case e =>NotExpression(expression = e) })
     
    def filterCondition : Parser[Option[Expression]] = 
      opt(FILTER ~> expression)
    
    def condition : Parser[Option[Either[Expression,Position]]] =
      opt((expression)^^{Left(_)}|(position)^^{Right(_)})  
      
    def expression : Parser[Expression] = 
        variable ^^ VariableReference |
        rdfLiteral ^^ LiteralExpression |
        functionExpression |
        numericFunctions |
        stringFunctions |
        miscellaneaFunctions
      
    implicit def unaryFunction2ExpressionParser[TYPE1, TYPE_RESULT]
        (func : UnaryFunction[TYPE1, TYPE_RESULT])
        (implicit type1Converter : CanFromRDFNode[TYPE1], resultConverter : CanToRDFNode[TYPE_RESULT]) : Parser[Expression] =
        func.name.ignoreCase ~> parens(expression) ^^
        { case p1 => func.createExpression(p1) }
    
    implicit def binaryFunction2ExpressionParser[TYPE1, TYPE2, TYPE_RESULT]
        (func : BinaryFunction[TYPE1, TYPE2, TYPE_RESULT])
        (implicit type1Converter : CanFromRDFNode[TYPE1], type2Converter : CanFromRDFNode[TYPE2], resultConverter : CanToRDFNode[TYPE_RESULT]) : Parser[Expression] =
        func.name.ignoreCase ~> "(" ~> (expression <~ ",") ~ (expression <~ ")") ^^
        { case p1~p2 => func.createExpression(p1, p2) }
        
    implicit def ternaryFunction2ExpressionParser[TYPE1, TYPE2,TYPE3, TYPE_RESULT]
        (func : TernaryFunction[TYPE1, TYPE2, TYPE3, TYPE_RESULT])
        (implicit type1Converter : CanFromRDFNode[TYPE1], type2Converter : CanFromRDFNode[TYPE2], type3Converter : CanFromRDFNode[TYPE3],resultConverter : CanToRDFNode[TYPE_RESULT]) : Parser[Expression] =
        func.name.ignoreCase ~> "(" ~> (expression <~ ",")~(expression <~ ",") ~ (expression <~ ")") ^^
        { case p1~p2~p3 => func.createExpression(p1, p2, p3) }
        
    import MiscellaneaFunctions._
    
    def miscellaneaFunctions : Parser[Expression] = 
      DBPediaDisambiguation3 | DBPediaDisambiguation1 |
      setLangTag |
      matches |
      boolean |
      isResource | canBeResource
    
    import NumericFunctions._

    def numericFunctions : Parser[Expression] = 
      numericAdd |
      numericSubstract |
      numericMultiply |
      numericDivide |
      numericIntegerDivide |
      numericMod |
      equal |
      greaterThan |
      lessThan |
      abs |
      floor |
      round |
      ceiling |
      int | intOrElse |
      float |
      double | doubleOrElse | isDouble| canBeDouble |
      intAdd | intSubstract | intMultiply | intDivide | isInt | canBeInt
    
    import StringFunctions._
    
    def stringFunctions : Parser[Expression] =
      startsWith |
      normalizeSpace |
      normalizeUnicode1 | normalizeUnicode2 |
      encodeForUri |
      upperCase |
      compare |
      levenshteinDistance |
      translate |
      lowerCase |
      replace |
      contains |
      endsWith |
      substringAfter |
      substringBefore |
      stringLength |
      substring2 | substring3 |
      string |
      firstIndexOf | lastIndexOf |
      trim

    def functionExpression : Parser[Expression] =
      ((RESOURCE <~"(") ~> expression )~ (","~> iriRef <~")") ^^
    		{case v~u => ResourceExpression(expression = v, uri = u)} |
    	((RESOURCE <~"(") ~> expression )~ (","~> curieRef <~")") ^^
    		{case v~u => ResourceExpression(expression = v, uri = u)} |
    	((RESOURCE <~"(") ~> expression )~ (","~> definedPrefix <~")") ^^
    		{case v~p => ResourceExpression(expression = v, uri = p)} |
      ((RESOURCE <~"(") ~> expression)<~")" ^^
    		{case local =>if (prefixes.contains("my")) {ResourceExpression(local, prefixes("my")) }
    					  else { throw new UndefinedPrefixException("my") } }|
      GET_ROW ~> "(" ~> variable <~ ")" ^^
    		{case v => GetRowExpression(variable = v) } |		
      GET_COL ~> "(" ~> variable <~ ")" ^^
    		{case v => GetColExpression(variable = v) }|		
      ((IF ~> expression) ~ (THEN~> expression) ~ (ELSE ~> expression)) ^^
    		{case condition ~ trueExpression ~ falseExpression => TernaryOperationExpression(condition, trueExpression, falseExpression)}|		
     	(CONCAT~> "("~>repsep(expression, ",")<~")")^^
    		{e => ConcatExpression(e)}|
    	(STRING_JOIN~> "("~>repsep(expression, ";")~("," ~>expression)<~")")^^
    		{case re~qs => StringJoinExpression(re, qs)}|
    	(DECIMAL ~> "("~>expression~opt("," ~> quotedString)<~")")^^
    		{case exp ~ sep =>DecimalExpression(exp, sep)}|
    	(NOT ~> expression)^^
    		{NotExpression}|
    	(AND~>"(" ~>((expression <~",")  ~ expression)<~")" )^^
    		{case exp1 ~ exp2 => AndExpression(exp1,exp2)}|
    	(OR~>"(" ~>((expression <~",")  ~ expression)<~")" )^^
    		{case exp1 ~ exp2 => OrExpression(exp1,exp2)}

    
    def tuple : Parser[Tuple] = ("[" ~> rep1sep(variable,",") <~ "]") ~ opt(IN ~> tupleType) ^^
    	{ case vars~direction => Tuple(vars,direction getOrElse TupleType.horizontal) }
    
    
    	
    // templates
    def blankNodeBlock : Parser[Seq[TripleTemplate]] =
	   "[" ~> predicateObjectsTemplate <~ (("."<~ "]") | "]") ^^
	  { case predObjs => 
	      val bn = createFreshBlankNode()
	      for ((pred,obj) <- predObjs._1) yield TripleTemplate(Left(bn),pred,obj) }/***Revisar***/
    
    def verbTemplate : Parser[Either[RDFNode, Variable]] =
      iriRef ^^ { Left(_) } |
      A ^^ { _ => Left(RDF_TYPE) } |
      curieRef ^^ { Left(_) } |
      variable ^^ { Right (_) }
       
	def objectsTemplate : Parser[(Seq[Either[RDFNode, Variable]], Seq[TripleTemplate])] =
	  rep1sep(eitherRDFNodeOrVariable, ",") ^^ { case objs => (objs, Seq()) } |
	  blankNodeBlock ^^{case innerTriples => (Seq(Left(innerTriples.head.s.left.get)),innerTriples)}

	def predicateObjectsTemplate : Parser[(Seq[Tuple2[ Either[RDFNode, Variable], Either[RDFNode, Variable] ]], Seq[TripleTemplate])] =
		rep1sep((verbTemplate ~ objectsTemplate) ^^ { case pred~objs => ((for ((obj) <- objs._1) yield (pred,obj)),objs _2) }, ";") ^^ { case predObjsInnerTriples =>(predObjsInnerTriples.unzip._1.flatten,predObjsInnerTriples.unzip._2.flatten)}

	def triplesSameSubjectTemplate : Parser[Seq[TripleTemplate]] =
	  eitherRDFNodeOrVariable~predicateObjectsTemplate ^^
	  { case subj~predObjs => predObjs._2 ++(for ((pred,obj) <- predObjs._1) yield TripleTemplate(subj,pred,obj)) } |
	  blankNodeBlock
	
	def template : Parser[Template] = "{" ~> rep1sep(triplesSameSubjectTemplate, ".") <~ (("."<~ "}") | "}")  ^^
	  { triples => Template((triples flatten)) }
	

	// parsing methods
	
	protected def parse[T](p : Parser[T], input: String) : T = {
        val phraseParser = phrase(p)
        phraseParser(new CharSequenceReader(input)) match {
            case Success(t,_)     => t
            case NoSuccess(msg,next) => throw new ParseException(input, msg, next.pos.line, next.pos.column)
        }		
	}
	
	def parseProgram(input : String) : S ={
	  val program = parse(start, input)
      new SemanticCheck().checkTemplateVars(program)
      return program
	} 
	
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
