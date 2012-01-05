package es.ctic.tabels

class TabelsException(msg:String) extends Exception(msg)

class ParseException(input_ : String, msg : String, lineNumber_ : Int, column_ : Int)
  extends TabelsException("Syntax error at line " + lineNumber_ + ", column " + column_ + ": " + msg) {
     val input = input_
     val lineNumber = lineNumber_
     val column = column_
     var line = input.split("\n")(lineNumber-1).replace("\t", " ")
}

class UndefinedPrefixException(prefix : String)
    extends TabelsException("Undefined prefix '" + prefix + "'")

class TemplateInstantiationException(msg:String) extends TabelsException(msg)

class UnboundVariableException(variable: Variable) extends TabelsException("Found unbound variable " + variable)

class InvalidTypeFunctionException(msg:String) extends TabelsException(msg)

class NoInputFiles extends TabelsException("No input file(s) selected")

class InvalidInputFile(filename:String) extends TabelsException("Unable to read input file " + filename)

class UnrecognizedSpreadsheetFormatException(uri:String) extends TabelsException("Unable to recognize spreadsheet file format: " + uri)

class TypeConversionException(literal:Literal, targetType:Resource) extends TabelsException("Unable to convert literal " + literal + " to type " + targetType)

class CannotConvertResourceToLiteralException(resource : Resource) extends TabelsException("Cannot convert resource " + resource + " to literal")

class InvalidFucntionParameterException(parameter : String) extends TabelsException("Invalid function parameter value: " + parameter)
