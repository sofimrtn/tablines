package es.ctic.tabels

class TabelsException(msg:String) extends Exception(msg)

class RunTimeTabelsException (msg:String) extends RuntimeException(msg)

class ParseException(input_ : String, msg : String, lineNumber_ : Int, column_ : Int)
  extends TabelsException("Syntax error at line " + lineNumber_ + ", column " + column_ + ": " + msg) {
     val input = input_
     val lineNumber = lineNumber_
     val column = column_
     var line = input.split("\n")(lineNumber-1).replace("\t", " ")
}

class UndefinedPrefixException(prefix : String)
    extends TabelsException("Undefined prefix '" + prefix + "'")

class TemplateInstantiationException(msg:String) extends RunTimeTabelsException(msg)

class UnboundVariableException(variable: Variable) extends RunTimeTabelsException("Found unbound variable " + variable)

class InvalidTypeFunctionException(msg:String) extends RunTimeTabelsException(msg)

class NoInputFiles extends RunTimeTabelsException("No input file(s) selected")

class IndexOutOfBounds(point: Point) extends RunTimeTabelsException(point +"out of bounds")

class InvalidInputFile(filename:String) extends RunTimeTabelsException("Unable to read input file " + filename)

class UnrecognizedSpreadsheetFormatException(uri:String) extends RunTimeTabelsException("Unable to recognize spreadsheet file format: " + uri)

class TypeConversionException(literal:Literal, targetType:Resource) extends RunTimeTabelsException("Unable to convert literal " + literal + " to type " + targetType)

class CannotConvertResourceToLiteralException(resource : Resource) extends RunTimeTabelsException("Cannot convert resource " + resource + " to literal")

/*functions exceptions*/
class InvalidFucntionParameterException(parameter : String) extends RunTimeTabelsException("Invalid function parameter value: " + parameter)

class ZeroDivision extends InvalidFucntionParameterException("Division by Zero")
