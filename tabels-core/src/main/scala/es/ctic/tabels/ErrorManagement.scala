package es.ctic.tabels

class TabelsException(msg:String) extends Exception(msg)

class CompileTimeTabelsException (msg: String) extends TabelsException(msg)

class RunTimeTabelsException (msg:String) extends TabelsException(msg)

class ParseException(input_ : String, msg : String, lineNumber_ : Int, column_ : Int)
  extends TabelsException("Syntax error at line " + lineNumber_ + ", column " + column_ + ": " + msg) {
     val input = input_
     val lineNumber = lineNumber_
     val column = column_
     var line = input.split("\n")(lineNumber-1).replace("\t", " ")
}

class SemanticException(variable : Set[Variable])
  extends CompileTimeTabelsException("Template variable " + variable.toString.drop(4).dropRight(1) + " unasigned in tabels program") 

class UndefinedPrefixException(prefix : String)
    extends CompileTimeTabelsException("Undefined prefix '" + prefix + "'")

class TemplateInstantiationException(msg:String) extends RunTimeTabelsException(msg)

class UnboundVariableException(variable: Variable) extends RunTimeTabelsException("Found unbound variable " + variable)

class InvalidTypeFunctionException(msg:String) extends RunTimeTabelsException(msg)

class NoInputFiles extends RunTimeTabelsException("No input file(s) selected")

class IndexOutOfBounds(point: Point) extends RunTimeTabelsException(point +"out of bounds")

class InvalidInputTab(tabName:String) extends RunTimeTabelsException("Unable to read tab " + tabName)

class InvalidInputFile(filename:String) extends RunTimeTabelsException("Unable to read input file " + filename)

class InvalidInputFileCannotReadXls(filename:String) extends InvalidInputFile("Not valid Excel file " + filename)
class InvalidInputFileCannotReadOds(filename:String) extends InvalidInputFile("Not valid ODS file " + filename)
class InvalidInputFileCannotReadHTML(filename:String) extends InvalidInputFile("Not valid HTML file " + filename)
class InvalidInputFileCannotReadCSV(filename:String) extends InvalidInputFile("Not valid CSV file " + filename)

class InvalidInputFileNoShpAttached(filename:String) extends InvalidInputFile("Shapefile zip is missing shp file " + filename)
class InvalidInputFileNoSldAttached(filename:String) extends InvalidInputFile("Shapefile zip is missing sld file " + filename)

class UnrecognizedSpreadsheetFormatException(uri:String) extends RunTimeTabelsException("Unable to recognize spreadsheet file format: " + uri)

class TypeConversionException(literal:Literal, targetType:Resource) extends RunTimeTabelsException("Unable to convert literal " + literal + " to type " + targetType)

class CannotConvertResourceToLiteralException(resource : Resource) extends RunTimeTabelsException("Cannot convert resource " + resource + " to literal")

class NotValidUriException(uri:String) extends CompileTimeTabelsException("Not valid URI: " + uri)


/*functions exceptions*/
class InvalidFucntionParameterException(parameter : String) extends RunTimeTabelsException("Invalid function parameter value: " + parameter)

class IllegalArgumentException(msg:String) extends RunTimeTabelsException(msg)

class ZeroDivisionException extends InvalidFucntionParameterException("Division by Zero")

class ServerReferedURIException(uri:String) extends CompileTimeTabelsException("Not valid URI - " + uri)

class LuceneQueryException(msg:String) extends RunTimeTabelsException(msg)

class ResourceCannotBeRetrievedException(uri:String) extends RunTimeTabelsException("Cannot retrieve resource " + uri)