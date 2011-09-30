package es.ctic.tabels

// enumerations are explained in sect 20.8 of the Scala book

object RelativityEnum extends Enumeration {

	type RelativityEnum = Value
	
	val left = Value("left")
	val right = Value("right")
	val top = Value("top") // aka: "tabs"
	val bottom = Value("bottom")

}

