package es.ctic.tabels


abstract class Expression {
  
  def evaluate(evaluationContext : EvaluationContext) : RDFNode
  
  def evaluateAsLiteral(evaluationContext : EvaluationContext) : Literal = evaluate(evaluationContext) match {
      case l : Literal  => l
      case r : Resource => throw new CannotConvertResourceToLiteralException(r)
  }
  
  def evaluateAsTruthValue(evaluationContext : EvaluationContext) : Boolean = evaluateAsLiteral(evaluationContext).asBoolean.truthValue
  def evaluateAsStringValue(evaluationContext : EvaluationContext) : String = evaluateAsLiteral(evaluationContext).asString.value.toString
  def evaluateAsIntValue(evaluationContext : EvaluationContext) : Int = evaluateAsLiteral(evaluationContext).asInt.value.toString.toInt // FIXME: perhaps can be simplified
  def evaluateAsFloatValue(evaluationContext : EvaluationContext) : Float = evaluateAsLiteral(evaluationContext).asFloat.value.toString.toFloat  // FIXME: perhaps can be simplified
  
  def prettyPrint() : String
  
  override def toString = prettyPrint

}

case class FunctionName(name : String) {
  
   def isDefinedBy[TYPE1,TYPE_RESULT]
        (f : (EvaluationContext, TYPE1) => TYPE_RESULT)
        (implicit type1Converter : CanFromRDFNode[TYPE1], resultConverter : CanToRDFNode[TYPE_RESULT])
         : UnaryFunction[TYPE1, TYPE_RESULT] =
        UnaryFunction[TYPE1,TYPE_RESULT](name, f)
        
    def isDefinedBy[TYPE1,TYPE_RESULT]
        (f : (TYPE1) => TYPE_RESULT)
        (implicit type1Converter : CanFromRDFNode[TYPE1], resultConverter : CanToRDFNode[TYPE_RESULT])
         : UnaryFunction[TYPE1, TYPE_RESULT] =
        UnaryFunction[TYPE1,TYPE_RESULT](name, { (ev : EvaluationContext, p1 : TYPE1) => f(p1) })

    def isDefinedBy[TYPE1,TYPE2,TYPE_RESULT]
        (f : (EvaluationContext, TYPE1, TYPE2) => TYPE_RESULT)
        (implicit type1Converter : CanFromRDFNode[TYPE1], type2Converter : CanFromRDFNode[TYPE2], resultConverter : CanToRDFNode[TYPE_RESULT])
         : BinaryFunction[TYPE1, TYPE2, TYPE_RESULT] =
        BinaryFunction[TYPE1,TYPE2,TYPE_RESULT](name, f)
        
    def isDefinedBy[TYPE1,TYPE2,TYPE_RESULT]
        (f : (TYPE1, TYPE2) => TYPE_RESULT)
        (implicit type1Converter : CanFromRDFNode[TYPE1], type2Converter : CanFromRDFNode[TYPE2], resultConverter : CanToRDFNode[TYPE_RESULT])
         : BinaryFunction[TYPE1, TYPE2, TYPE_RESULT] =
        BinaryFunction[TYPE1,TYPE2,TYPE_RESULT](name, { (ev : EvaluationContext, p1 : TYPE1, p2 : TYPE2) => f(p1,p2) })
        
    def isDefinedBy[TYPE1,TYPE2, TYPE3, TYPE_RESULT]
        (f : (EvaluationContext, TYPE1, TYPE2, TYPE3) => TYPE_RESULT)
        (implicit type1Converter : CanFromRDFNode[TYPE1], type2Converter : CanFromRDFNode[TYPE2], type3Converter : CanFromRDFNode[TYPE3], resultConverter : CanToRDFNode[TYPE_RESULT])
         : TernaryFunction[TYPE1, TYPE2, TYPE3, TYPE_RESULT] =
        TernaryFunction[TYPE1,TYPE2, TYPE3,TYPE_RESULT](name, f)
        
    def isDefinedBy[TYPE1,TYPE2, TYPE3,TYPE_RESULT]
        (f : (TYPE1, TYPE2, TYPE3) => TYPE_RESULT)
        (implicit type1Converter : CanFromRDFNode[TYPE1], type2Converter : CanFromRDFNode[TYPE2], type3Converter : CanFromRDFNode[TYPE3], resultConverter : CanToRDFNode[TYPE_RESULT])
         : TernaryFunction[TYPE1, TYPE2, TYPE3, TYPE_RESULT] =
        TernaryFunction[TYPE1,TYPE2, TYPE3, TYPE_RESULT](name, { (ev : EvaluationContext, p1 : TYPE1, p2 : TYPE2, p3 : TYPE3) => f(p1,p2,p3) })

}

case class UnaryFunction[TYPE1, TYPE_RESULT](name : String, f : (EvaluationContext, TYPE1) => TYPE_RESULT)
    (implicit type1Converter : CanFromRDFNode[TYPE1],  resultConverter : CanToRDFNode[TYPE_RESULT]) {

    def apply(arg1 : TYPE1)(implicit ec : EvaluationContext) : TYPE_RESULT = f(ec, arg1)

    def createExpression(arg1 : Expression) : UnaryExpression[TYPE1, TYPE_RESULT] =
        new UnaryExpression(this)(arg1)

}

case class UnaryExpression[TYPE1, TYPE_RESULT](func : UnaryFunction[TYPE1, TYPE_RESULT])
    (arg1 : Expression)
    (implicit type1Converter : CanFromRDFNode[TYPE1], resultConverter : CanToRDFNode[TYPE_RESULT])
    extends Expression {

    def typeWrapper(ec : EvaluationContext, param1 : RDFNode) : RDFNode =
        resultConverter.toRDFNode(func.f(ec, type1Converter.fromRDFNode(param1)))

    override def evaluate(evaluationContext : EvaluationContext) : RDFNode =
        typeWrapper(evaluationContext, arg1.evaluate(evaluationContext))
        
    override def prettyPrint = func.name + "(" + arg1 + ")"

}

case class BinaryFunction[TYPE1, TYPE2, TYPE_RESULT](name : String, f : (EvaluationContext, TYPE1, TYPE2) => TYPE_RESULT)
    (implicit type1Converter : CanFromRDFNode[TYPE1], type2Converter : CanFromRDFNode[TYPE2], resultConverter : CanToRDFNode[TYPE_RESULT]) {

    def apply(arg1 : TYPE1, arg2 : TYPE2)(implicit ec : EvaluationContext) : TYPE_RESULT = f(ec, arg1, arg2)

    def createExpression(arg1 : Expression, arg2: Expression) : BinaryExpression[TYPE1, TYPE2, TYPE_RESULT] =
        new BinaryExpression(this)(arg1, arg2)

}

case class BinaryExpression[TYPE1, TYPE2, TYPE_RESULT](func : BinaryFunction[TYPE1, TYPE2, TYPE_RESULT])
    (arg1 : Expression, arg2 : Expression)
    (implicit type1Converter : CanFromRDFNode[TYPE1], type2Converter : CanFromRDFNode[TYPE2], resultConverter : CanToRDFNode[TYPE_RESULT])
    extends Expression {

    def typeWrapper(ec : EvaluationContext, param1 : RDFNode, param2 : RDFNode) : RDFNode =
        resultConverter.toRDFNode(func.f(ec, type1Converter.fromRDFNode(param1),
                                             type2Converter.fromRDFNode(param2)))

    override def evaluate(evaluationContext : EvaluationContext) : RDFNode =
        typeWrapper(evaluationContext, arg1.evaluate(evaluationContext), arg2.evaluate(evaluationContext))
        
    override def prettyPrint = func.name + "(" + arg1 + "," + arg2 + ")"

}

case class TernaryFunction[TYPE1, TYPE2, TYPE3, TYPE_RESULT](name : String, f : (EvaluationContext, TYPE1, TYPE2, TYPE3) => TYPE_RESULT)
    (implicit type1Converter : CanFromRDFNode[TYPE1], type2Converter : CanFromRDFNode[TYPE2], type3Converter : CanFromRDFNode[TYPE3], resultConverter : CanToRDFNode[TYPE_RESULT]) {

    def apply(arg1 : TYPE1, arg2 : TYPE2, arg3 : TYPE3)(implicit ec : EvaluationContext) : TYPE_RESULT = f(ec, arg1, arg2, arg3)

    def createExpression(arg1 : Expression, arg2: Expression, arg3: Expression) : TernaryExpression[TYPE1, TYPE2, TYPE3, TYPE_RESULT] =
        new TernaryExpression(this)(arg1, arg2, arg3)

}

case class TernaryExpression[TYPE1, TYPE2, TYPE3, TYPE_RESULT](func : TernaryFunction[TYPE1, TYPE2, TYPE3, TYPE_RESULT])
    (arg1 : Expression, arg2 : Expression, arg3 : Expression)
    (implicit type1Converter : CanFromRDFNode[TYPE1], type2Converter : CanFromRDFNode[TYPE2], type3Converter : CanFromRDFNode[TYPE3], resultConverter : CanToRDFNode[TYPE_RESULT])
    extends Expression {

    def typeWrapper(ec : EvaluationContext, param1 : RDFNode, param2 : RDFNode, param3 : RDFNode) : RDFNode =
        resultConverter.toRDFNode(func.f(ec, type1Converter.fromRDFNode(param1),
                                             type2Converter.fromRDFNode(param2),
                                             type3Converter.fromRDFNode(param3)))

    override def evaluate(evaluationContext : EvaluationContext) : RDFNode =
        typeWrapper(evaluationContext, arg1.evaluate(evaluationContext), arg2.evaluate(evaluationContext), arg3.evaluate(evaluationContext))
        
    override def prettyPrint = func.name + "(" + arg1 + "," + arg2 + "," + arg3 +")"

}

trait FunctionCollection {

    implicit def string2functionName(name: String) : FunctionName = FunctionName(name)

}









