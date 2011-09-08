package es.ctic.tabels

import scala.util.parsing.combinator._

class TabelsParser extends JavaTokenParsers {

    def patternMatch : Parser[PatternMatch] = variable~("in cell"~>position) ^^
        { case v~p => PatternMatch(variable = v, pos = p) }

    def variable : Parser[Variable] = """\?[a-zA-Z][a-zA-Z0-9]*""".r ^^ Variable

    def position : Parser[Position] = """[A-Z]+[0-9]+""".r ^^ Position

}

object testParser extends TabelsParser