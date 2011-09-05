package es.ctic.tabels

trait Evaluable{
	
  def grammarEvaluation( evaluationContext : EvaluationContext, dataSource: DataSource): List[Event] = this match{
	  
     case _:S  =>
       this.asInstanceOf[S].patternList.foreach(patt => 
         		evaluationContext.eventList :::= patt.grammarEvaluation(evaluationContext,dataSource) )
       return evaluationContext.eventList ::: List(new Event(List(new Binding("$prueba", dataSource.getValue(new Point("horas.xls","Hoja1",0,0)).getContent))))
     
     case _:Pattern  => 
       evaluationContext.eventList :::= this.asInstanceOf[Pattern].letE.grammarEvaluation(evaluationContext, dataSource)
       evaluationContext.eventList :::= this.asInstanceOf[Pattern].whereE.grammarEvaluation(evaluationContext, dataSource)
       return evaluationContext.eventList ::: List(new Event(List(new Binding("$pattern", dataSource.getValue(new Point("horas.xls","Hoja1",0,0)).getContent))))
	   
     
     case _:LetWhereExpression  => 
       var bList =List (Binding)
       this.asInstanceOf[LetWhereExpression].sentList.foreach(sent =>
         						bList:::= new Binding(sent.v.name, sent.e.exp))
         						
       return List(new Event(bList))  
       
     case _  => return List(new Event(List(new Binding("$nomatch", dataSource.getValue(new Point("horas.xls","Hoja1",0,0)).getContent))))
  }
}