package es.ctic.tabels

trait Evaluable{
	def grammarEvaluation( evaluationContext : EvaluationContext, dataSource: DataSource): Event = this match{
	  
	  case S(patternList, templateList )=>
	    print("S := ")
	    patternList.foreach(pattern => grammarEvaluation(evaluationContext, dataSource) :: evaluationContext.eventList)
	    return new Event(new BindingList(List(new Binding("$prueba", dataSource.getValue(new Point("","",0,0))))))
	    
	  case Pattern(letE, lBindE,lPatternM , whereE)=>
	    print("S := ")
	    lBindE.foreach(pattern => grammarEvaluation(evaluationContext, dataSource) :: evaluationContext.eventList)
	    return new Event(new BindingList(List(new Binding("$prueba", dataSource.getValue(new Point("","",0,0))))))
	    
	  case LetWhereExpression(sentList)=>
	    print(" LET/Where")
	    sentList.foreach(assing => grammarEvaluation(evaluationContext, dataSource) :: evaluationContext.eventList)
	    return new Event(new BindingList(List(new Binding("$prueba", dataSource.getValue(new Point("","",0,0))))))
	  
	 case BindingExpresion(dim , filterCondList, pos, stopCond , variable)=>
	    filterCondList.foreach(condition => grammarEvaluation(evaluationContext, dataSource) :: evaluationContext.eventList)
	    return new Event(new BindingList(List(new Binding("$prueba", dataSource.getValue(new Point("","",0,0)))))) 
	    
	  
	}
}