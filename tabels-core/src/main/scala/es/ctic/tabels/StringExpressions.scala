package es.ctic.tabels

import scala.util.matching.Regex
import java.text.Normalizer
import java.net.URLEncoder
/*
 * STRING FUNCTIONS
 * 
 */

object StringFunctions extends FunctionCollection {

    // ==========================================    
    // XPath 2.0 functions
    // ==========================================    

    // NOTE: concat is defined below
    // NOTE: string-join is defined below
	val substring3 = "substring" isDefinedBy { (x:String, y:Int, z:Int) => if (x.length >0) 
			           															x.substring(y, y+z)
			           														else ""
          									 }
	val substring2 = "substring" isDefinedBy { (x:String, y:Int) => if (x.length >0) 
			           															x.substring(y)
			           														else ""
          									 }
    val stringLength = "string-length" isDefinedBy { (x:String) => x.length()}
    val normalizeSpace = "normalize-space" isDefinedBy { x : String => x.replaceAll("\\s+"," ").trim() }
    val normalizeUnicode2 = "normalize-unicode" isDefinedBy { (x : String, form: String) =>
                                                              val str = if (x.trim.length==0) x.trim else x //If x is the empty string(full of blank spaces) replace it by the real empty string
                                                              form.trim.toUpperCase match {
                                                                case "NFC" => Normalizer.normalize(str,Normalizer.Form.NFC)
                                                                case "NFD" => Normalizer.normalize(str,Normalizer.Form.NFD)
                                                                case "NFKC "=> Normalizer.normalize(str,Normalizer.Form.NFKC)
                                                                case "NFKD" => Normalizer.normalize(str,Normalizer.Form.NFKD)
                                                                // FIXME: How to implement the Fully normalization option?
                                                                // case "FULLY-NORMALIZED"=> ??
                                                                case "" => x
                                                                case default => throw (new IllegalArgumentException("The unicode normalization form "+form+" is not supported"))
                                                              }
                                                            }
    val normalizeUnicode1 = "normalize-unicode" isDefinedBy { x : String =>
                                                Normalizer.normalize(

                                                  if (x.trim.length==0) x.trim else x //If x is the empty string(full of blank spaces) replace it by the real empty string
                                                  ,Normalizer.Form.NFC) }

    val upperCase = "upper-case" isDefinedBy { (x:String) => x.toUpperCase}
    val lowerCase = "lower-case" isDefinedBy { (x:String) => x.toLowerCase()}
    val translate = "translate" isDefinedBy { (x:String, y:String, z:String) =>{ 
    	 																		var newX:String = x
    	 																		for(char <- y.toCharArray().take(z.length)) {         
																					newX = newX.replaceAll(char.toString,z.charAt(y.indexOf(char)).toString )
																	     		}
																	     		for(char <- y.toCharArray().drop(z.length)) {
																	     		    newX = newX.replaceAll(char.toString, "")
																	     		}
																				newX.toString}
     										}

  val encodeForUri = "encode-for-uri" isDefinedBy { (x:String) => URLEncoder.encode(x)}
    // FIXME: iri-to-uri is missing
    // FIXME: escape-html-uri is missing
    
    // ==========================================    
    // XPath 2.0 predicates
    // ==========================================    
    
    val contains = "contains" isDefinedBy { (x:String, y:String) => x.contains(y)}
    val startsWith = "starts-with" isDefinedBy { (x : String, y : String) => x.startsWith(y) }
    val endsWith = "ends-with" isDefinedBy { (x:String, y:String) => x.endsWith(y)}
    val substringBefore = "substring-before" isDefinedBy { (x:String, y:String) =>x.dropRight(x.length-x.indexOf(y))}
    val substringAfter = "substring-after" isDefinedBy { (x:String, y:String) => if (x.contains(y))
   	 																				x.drop(x.indexOf(y)+y.length)
   	 																		  else ""
   	 												}
   	val matches = "matches" isDefinedBy {(lit:String, re:Regex)=> lit.matches(re.toString())}
    val replace = "replace" isDefinedBy { (x:String, y :Regex, z:String) => x.replaceAll(y.toString,z)}
   	// FIXME: tokenize is missing
    val compare = "compare" isDefinedBy { (x:String, y:String) => x.compareToIgnoreCase(y) match{
    																  case num if (num < 0) => -1
    																  case num if (num > 0) => 1
																      case _ => 0
    																}
										}
    // FIXME: codepoint-equal is missing

    // ==========================================    
    // Other functions and predicates not in XPath 2.0
    // ==========================================    
    
    val levenshteinDistance = "levenshtein-distance" isDefinedBy { (x:String, y:String) => Levenshtein.stringDistance(x,y)}
    val string = "string" isDefinedBy { (x:String) => x}
    val firstIndexOf = "first-index-of" isDefinedBy { (x:String, y:String) => x.indexOf(y) }
    val lastIndexOf = "last-index-of" isDefinedBy { (x:String, y:String) => x.lastIndexOf(y) }
    val trim = "trim" isDefinedBy { x:String => x.trim() }
}

case class ConcatExpression(expressions: Seq[Expression]) extends Expression{
  
   override def evaluate(evaluationContext:EvaluationContext): RDFNode ={
	var result : String = ""
    expressions.foreach( exp => result += exp.evaluateAsStringValue(evaluationContext))
    return Literal(result, XSD_STRING). asInstanceOf[RDFNode]
  }
   override def prettyPrint = "concat(" + expressions.map(_ toString).mkString(",") + ")"
}

case class StringJoinExpression(expressions: Seq[Expression], separator : Expression) extends Expression{
  
   override def evaluate(evaluationContext:EvaluationContext): RDFNode ={
	val sep = separator.evaluateAsStringValue(evaluationContext)
    Literal(expressions.map(_.evaluateAsStringValue(evaluationContext)).mkString(sep), XSD_STRING). asInstanceOf[RDFNode]
   }
   override def prettyPrint = "string join(" + expressions.map(_ toString).mkString(",")  + " , "+ separator + ")"
}

