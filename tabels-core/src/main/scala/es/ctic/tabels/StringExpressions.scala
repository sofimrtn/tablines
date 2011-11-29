package es.ctic.tabels

import scala.util.matching.Regex
/*
 * STRING FUNCTIONS
 * 
 */

object StringFunctions extends FunctionCollection {

    val startsWith = "starts-with" isDefinedBy { (x : String, y : String) => x.startsWith(y) }

}

case class AddVariableExpression(variable : Variable, expression: Expression) extends Expression{
  
  override def evaluate(evaluationContext : EvaluationContext) = evaluationContext.bindings.getValue(variable) + expression.evaluate(evaluationContext).asString.value.toString
  override def prettyPrint = variable.toString + "+" + expression.toString

}

case class ConcatExpression(expressions: Seq[Expression]) extends Expression{
  
   override def evaluate(evaluationContext:EvaluationContext): RDFNode ={
	var result : String = ""
    expressions.foreach( exp => result += exp.evaluate(evaluationContext).asString.value.toString)
    return Literal(result, XSD_STRING). asInstanceOf[RDFNode]
  }
   override def prettyPrint = "concat(" + expressions.map(_ toString).mkString(",") + ")"
}

case class StringJoinExpression(expressions: Seq[Expression], separator : Expression) extends Expression{
  
   override def evaluate(evaluationContext:EvaluationContext): RDFNode ={
	val sep = separator.evaluate(evaluationContext).asString
    Literal(expressions.map(_.evaluate(evaluationContext).asString.value.toString).mkString(sep.value.toString), XSD_STRING). asInstanceOf[RDFNode]
   }
   override def prettyPrint = "string join(" + expressions.map(_ toString).mkString(",")  + " , "+ separator + ")"
}

case class SubStringExpression(expression: Expression, startingLoc : Expression, length : Option[Expression]) extends Expression{
  
   override def evaluate(evaluationContext:EvaluationContext): RDFNode ={
       val evaluatedExpression = expression.evaluate(evaluationContext).asString
       val evaluatedStartingLoc = startingLoc.evaluate(evaluationContext).asString.value.toString.toInt
       val evaluatedLength : Option[Int] = length map (e => e.evaluate(evaluationContext).asString.value.toString.toInt)
       if (evaluatedExpression.value.toString.length >0) {
           val newValue = evaluatedLength match {
               case None => evaluatedExpression.value.toString.substring(evaluatedStartingLoc)
               case Some(l) => evaluatedExpression.value.toString.substring(evaluatedStartingLoc, evaluatedStartingLoc+l)
           }
           Literal(newValue, XSD_STRING)
       } else Literal("", XSD_STRING)
   }
   override def prettyPrint = "substring(" + expression.toString  + " , "+ startingLoc + ")"
}

case class StringLengthExpression(expression: Expression) extends Expression
{
	override def evaluate(evaluationContext : EvaluationContext) : RDFNode =
	  Literal (expression.evaluate(evaluationContext).asString.value.toString.length, XSD_INT)
	
	override def prettyPrint = "length(" + expression.toString  + ")"
}

case class StartsWithExpression(container: Expression, start: Expression) extends Expression
{
	override def evaluate(evaluationContext : EvaluationContext) : RDFNode =
	  if(container.evaluate(evaluationContext).asString.value.toString.startsWith(start.evaluate(evaluationContext).asString.value.toString))
		   LITERAL_TRUE
	  else LITERAL_FALSE
	
	override def prettyPrint = "startswith(" + container.toString  +", "+ start.toString  + ")"
}

case class SubstringBeforeExpression(container: Expression, subString: Expression) extends Expression
{
	override def evaluate(evaluationContext : EvaluationContext) : RDFNode = {
	  val evaluatedContainer = container.evaluate(evaluationContext).asString
	  Literal(evaluatedContainer.value.toString.dropRight(
	      evaluatedContainer.value.toString.length()-evaluatedContainer.value.toString.indexOf(subString.evaluate(evaluationContext).asString.value.toString)))
    }
	 
	override def prettyPrint = "substringbefore(" + container.toString  +", "+ subString.toString  + ")"
}

case class SubstringAfterExpression(container: Expression, subString: Expression) extends Expression
{
	override def evaluate(evaluationContext : EvaluationContext) : RDFNode = {
	    val evaluatedContainer = container.evaluate(evaluationContext).asString
	    val evaluatedSubstring = subString.evaluate(evaluationContext).asString
	  if(evaluatedContainer.value.toString.contains(evaluatedSubstring.value.toString))
		  Literal(evaluatedContainer.value.toString.drop(
				  evaluatedContainer.value.toString.indexOf(evaluatedSubstring.value.toString)+evaluatedSubstring.value.toString.length()))
	  else Literal("")
	}
	override def prettyPrint = "substringafter(" + container.toString  +", "+ subString.toString  + ")"
}

case class EndsWithExpression(container: Expression, end: Expression) extends Expression
{
	override def evaluate(evaluationContext : EvaluationContext) : RDFNode =
	  if(container.evaluate(evaluationContext).asString.value.toString.endsWith(end.evaluate(evaluationContext).asString.value.toString))
		   LITERAL_TRUE
	  else LITERAL_FALSE
	
	override def prettyPrint = "endswith(" + container.toString  +", "+ end.toString  + ")"
}

case class ContainsExpression(container: Expression, content: Expression) extends Expression
{
	override def evaluate(evaluationContext : EvaluationContext) : RDFNode =
	  if(container.evaluate(evaluationContext).asString.value.toString.contains(content.evaluate(evaluationContext).asString.value.toString))
		   LITERAL_TRUE
	  else LITERAL_FALSE
	
	override def prettyPrint = "contains(" + container.toString  +", "+ content.toString  + ")"
}

case class ReplaceExpression(input: Expression,pattern : Regex, replacement: Expression) extends Expression
{
	override def evaluate(evaluationContext : EvaluationContext) : RDFNode =
	   Literal(input.evaluate(evaluationContext).asString.value.toString.replaceAll(pattern.toString, replacement.evaluate(evaluationContext).asString.value.toString),XSD_STRING)
			
	override def prettyPrint = "replace(" + input.toString  +", "+ pattern.toString +", "+replacement.toString  + ")"
}

case class UpperCaseExpression(expression: Expression) extends Expression
{
	override def evaluate(evaluationContext : EvaluationContext) : RDFNode =
	  Literal(expression.evaluate(evaluationContext).asString.value.toString.toUpperCase)
		  
	override def prettyPrint = "upper case(" + expression   + ")"
}

case class LowerCaseExpression(expression: Expression) extends Expression
{
	override def evaluate(evaluationContext : EvaluationContext) : RDFNode =
	  Literal(expression.evaluate(evaluationContext).asString.value.toString.toLowerCase)
		  
	override def prettyPrint = "lower case(" + expression   + ")"
}

case class TranslateExpression(input: Expression, pattern : Expression,replacement: Expression) extends Expression
{
	override def evaluate(evaluationContext : EvaluationContext) : RDFNode ={
	 var textChain :String =input.evaluate(evaluationContext).asString.value.toString
	 val patternToMatch = pattern.evaluate(evaluationContext).asString
	 for(char <- patternToMatch.value.toString.toCharArray())
	 {
	   //FIX ME: not checked if the index in pattern is out of bounds in replacement
	   textChain = textChain.replaceAll(char.toString,replacement.evaluate(evaluationContext).asString.value.toString.charAt(patternToMatch.value.toString.indexOf(char)).toString )
	 }
	 
	 Literal(textChain,XSD_STRING)
}
	override def prettyPrint = "translate(" +  input.toString  +", "+ pattern.toString +", "+replacement.toString + ")"
}
/*FIX ME: To be implemented function 
 * escape-html-uri(expression: Expression)
 * */

case class LevenshteinDistanceExpression(expression1 :Expression, expression2: Expression) extends Expression{
  
  override def evaluate (evaluationContext : EvaluationContext) = 
    Literal(Levenshtein.stringDistance(expression1.evaluate(evaluationContext).asString.value.toString,expression2.evaluate(evaluationContext).asString.value.toString), XSD_INT)
   override def prettyPrint = "levenshtein-distance(" + expression1.toString + expression2.toString +")"

}


/* *Type change expressions  * */
case class StringExpression(expression: Expression) extends Expression{
  
  override def evaluate(evaluationContext : EvaluationContext) = Literal(expression.evaluate(evaluationContext).asString.value, XSD_STRING)
  override def prettyPrint = "string(" + expression.toString + ")"

}