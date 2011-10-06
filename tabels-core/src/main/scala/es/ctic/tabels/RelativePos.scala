package es.ctic.tabels

// enumerations are explained in sect 20.8 of the Scala book

object RelativePos extends Enumeration {

	type RelativePos = Value
	
	val left = Value("left")
	val right = Value("right")
	val top = Value("top") 
	val bottom = Value("bottom")

}

