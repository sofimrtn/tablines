package es.ctic.tabels
import scala.collection.mutable.ListBuffer

abstract class Visitor {
  def visit(s : S)
  def visit(patt : Pattern)
  def visit(lwexp : LetWhereExpression)
  def visit(bindExp : BindingExpresion)
  def visit(pattMatch : PatternMatch)
  def visit(dim : Dimension)
  def visit(filtCond : FilterCondition)
  def visit(pos : Position)
  def visit(sCond : StopCondition)
  def visit(v : Variable)
  def visit(tupple : Tuple)
  def visit(exp : Expression)
  def visit(assing : Assignment)

}

class AbstractVisitor extends Visitor{
  override def visit(s : S) = {}
  override def visit(patt : Pattern) = {}
  override def visit(lwexp : LetWhereExpression) = {}
  override def visit(bindExp : BindingExpresion) = {}
  override def visit(pattMatch : PatternMatch) = {}
  override def visit(dim : Dimension) = {}
  override def visit(filtCond : FilterCondition) = {}
  override def visit(pos : Position) = {}
  override def visit(sCond : StopCondition) = {}
  override def visit(v : Variable) = {}
  override def visit(tupple : Tuple) = {}
  override def visit(exp : Expression) = {}
  override def visit(assing : Assignment) = {}
}


class VisitorEvaluate(dS : DataSource) extends AbstractVisitor{
 
  val dataSource : DataSource = dS
  def events :List[Event] = buffEventList.toList
  private val buffEventList = new ListBuffer[Event]
  
  override def visit(patternMatch : PatternMatch){
  
    val point = new Point("horas.xls", "Hoja1", 0, 0)
    val binding = new Binding(patternMatch.variable, dataSource.getValue(point).getContent)
    val event = new Event(List(binding))
    buffEventList += event
  }
  
  /*override def visit(v : Var){
    print(v.name)
  }
  
  override def visit(pos : Position){
    print(pos.pos)
  }
*/
}

class VisitorToString extends AbstractVisitor{
  
  override def visit(pattMatch : PatternMatch){
  
    pattMatch.variable.accept(this)
    print("  in CELL ")
    pattMatch.position.accept(this)
  }
  
  override def visit(v : Variable){
    print(v.name)
  }
  
  override def visit(position : Position){
    print(position.position)
  }
  
}


