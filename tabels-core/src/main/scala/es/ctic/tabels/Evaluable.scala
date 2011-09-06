package es.ctic.tabels
import scala.collection.mutable.ListBuffer

trait Evaluable{
	
  def grammarEvaluation( evaluationContext : EvaluationContext, dataSource: DataSource): Event = this match{
	  
     
  //FIX IT//
     case _:S  =>
       this.asInstanceOf[S].patternList.foreach(patt => 
         		evaluationContext.buffList += patt.grammarEvaluation(evaluationContext,dataSource) )
       return new Event(List(new Binding("$prueba", dataSource.getValue(new Point("horas.xls","Hoja1",0,0)).getContent)))
     
     case _:Pattern  => 
       evaluationContext.buffList += this.asInstanceOf[Pattern].letE.grammarEvaluation(evaluationContext, dataSource)
       evaluationContext.buffList += this.asInstanceOf[Pattern].whereE.grammarEvaluation(evaluationContext, dataSource)
       return new Event(List(new Binding("$pattern", dataSource.getValue(new Point("horas.xls","Hoja1",0,0)).getContent)))
	   
     
     case _:LetWhereExpression  => 
       var bList =new ListBuffer[Binding]
       this.asInstanceOf[LetWhereExpression].sentList.foreach(sent =>
         						bList += new Binding(sent.v.name, sent.e.exp))
         						
       return new Event(bList.toList)   
       
     case _  => return new Event(List(new Binding("$nomatch", dataSource.getValue(new Point("horas.xls","Hoja1",0,0)).getContent)))
  }
}