package es.ctic.tabels

import scala.util.parsing.combinator._

class TabelsParser extends JavaTokenParsers {

    def patternMatch : Parser[PatternMatch] = variable~("in cell"~>position) ^^
        { case v~p => PatternMatch(variable = v, pos = p) }

    def variable : Parser[Var] = """\?[a-zA-Z]""".r ^^ Var

    def position : Parser[Position] = """[A-Z]+[0-9]+""".r ^^ Position

}

object testParser extends TabelsParser