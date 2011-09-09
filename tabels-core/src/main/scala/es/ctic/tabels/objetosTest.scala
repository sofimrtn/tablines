package es.ctic.tabels

object AST1 extends PatternMatch(List(),Position("A1"),null, Var("?x"),null ) 
object DS1 extends ExcelDataSource(List("horas.xls")) 
object VIS1 extends VisitorToString()
object VIS2 extends VisitorEvaluate(DS1)
object POINT extends Point("horas.xls", "Hoja1" , 0, 0)