package es.ctic.tabels

import java.io.File

object TEMP1 extends Template(List(TripleTemplate(Right(Variable("?x")),Left(Resource("http://example/name")), Right(Variable("?name"))),TripleTemplate(Right(Variable("?x")),Left(Resource("http://example/type")), Left(Resource("http://foaf:person")))))
object AST1 extends PatternMatch(List(), new Position(0,0),null, Variable("?x"),null ) 
object ROOT	extends S(List(Pattern(lBindE = List(), lPatternM = List(AST1))), List(TEMP1))	
object DS1 extends ExcelDataSource(List(new File("horas.xls")))
object DSO1 extends JenaDataOutput
object VIS1 extends VisitorToString()
object VIS2 extends VisitorEvaluate(DS1)
object P extends Point("horas.xls", "Hoja1" , 0, 0)
object INT extends Interpreter()