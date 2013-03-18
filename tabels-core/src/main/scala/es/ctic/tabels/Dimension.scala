package es.ctic.tabels

// enumerations are explained in sect 20.8 of the Scala book

object Dimension extends Enumeration {

	type Dimension = Value
	
	val cols = Value("cols")
	val rows = Value("rows")

  val table = Value("table")

  val sheets = Value("sheets") // aka: "tabs"
	val files = Value("files")


}

