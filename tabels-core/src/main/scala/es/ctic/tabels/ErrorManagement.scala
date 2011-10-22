package es.ctic.tabels

class TabelsException(msg:String) extends Exception(msg)

class ParseException(input_ : String, msg : String, lineNumber_ : Int, column_ : Int)
  extends TabelsException("Syntax error at line " + lineNumber_ + ", column " + column_ + ": " + msg) {
     val input = input_
     val lineNumber = lineNumber_
     val column = column_
     var line = input.split("\n")(lineNumber-1).replace("\t", " ")
}

class TemplateInstantiationException(msg:String) extends TabelsException(msg)

class UnboundVariableException(variable: Variable) extends TabelsException("Found unbound variable " + variable)

class InvalidTypeFunctionException(msg:String) extends TabelsException(msg)

class NoInputFiles extends TabelsException("No input file(s) selected")

class InvalidInputFile(filename:String) extends TabelsException("Unable to read input file " + filename)