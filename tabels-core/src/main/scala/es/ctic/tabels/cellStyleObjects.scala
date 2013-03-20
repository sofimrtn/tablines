package es.ctic.tabels

// enumerations are explained in sect 20.8 of the Scala book

object BorderPosition extends Enumeration {

	type BorderPosition = Value
	
	val top = Value("top")
  val right = Value("right")
	val bottom = Value("bottom")
	val left = Value("left") // aka: "tabs"

  val top_right = Value("top_right")
  val top_bottom = Value("top_bottom")
  val top_left = Value("top_left")

  val right_bottom = Value("right_bottom")
  val right_left = Value("right_left")

  val bottom_left = Value("bottom_left")

  val top_right_bottom = Value("top_right_bottom")
  val top_right_left = Value("top_right_left")

  val right_bottom_left = Value("right_bottom_left")

  val top_right_bottom_left = Value("top_right_bottom_left")

}

object BorderStyle extends Enumeration {

  type BorderStyle = Value

  val solid = Value("solid")
  val dashed = Value("dashed")
  val dotted = Value ("dotted")
}

object FontStyle extends Enumeration {

  type FontStyle = Value

  val none = Value("none")
  val italic = Value("italic")
  val bold = Value("bold")
  val italic_bold = Value ("italic_bold")
}

