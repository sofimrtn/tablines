package es.ctic.tabels
import scala.collection.mutable.ListBuffer

abstract class Evaluable{
  
}

abstract class EvaluablePosition{
  //def accept(vis : Visitor)
	
/*  def grammarEvaluation( visit : Visitor , evaluationContext : EvaluationContext, dataSource: DataSource): Event = this match{
	  
     
  //FIX IT//
     case _:S  =>
       this.asInstanceOf[S].patternList.foreach(patt => 
         		evaluationContext.buffList += patt.grammarEvaluation(visit, evaluationContext,dataSource) )
       return new Event(List(new Binding("$prueba", dataSource.getValue(new Point("horas.xls","Hoja1",0,0)).getContent)))
     
     
     case _:Pattern  => 
       evaluationContext.buffList += this.asInstanceOf[Pattern].letE.grammarEvaluation(visit, evaluationContext, dataSource)
       this.asInstanceOf[Pattern].lPatternM.foreach(patternMatch=> evaluationContext.buffList +=  patternMatch.grammarEvaluation(visit, evaluationContext, dataSource))
       evaluationContext.buffList += this.asInstanceOf[Pattern].whereE.grammarEvaluation(visit, evaluationContext, dataSource)
       
       return new Event(List(new Binding("$pattern", dataSource.getValue(new Point("horas.xls","Hoja1",0,0)).getContent)))
	   
     
     case _:LetWhereExpression  => 
       var bList =new ListBuffer[Binding]
       this.asInstanceOf[LetWhereExpression].sentList.foreach(sent =>
         						bList += new Binding(sent.v.name, sent.e.exp))
       return new Event(bList.toList)   
     
     case _:PatternMatch =>
       
     	return new Event(List(new Binding("$patternMatch", dataSource.getValue(new Point("horas.xls","Hoja1",0,0)).getContent)))
	   
     case _:BindingExpresion => 
     	var bList =new ListBuffer[Binding]
        this.asInstanceOf[BindingExpresion].variable					
        return new Event(List(new Binding(this.asInstanceOf[BindingExpresion].variable.name, this.asInstanceOf[BindingExpresion].variable.name)))   
       
     case _  => return new Event(List(new Binding("$nomatch", dataSource.getValue(new Point("horas.xls","Hoja1",0,0)).getContent)))
  }*/
}
abstract class EvaluableExpression{
  
}