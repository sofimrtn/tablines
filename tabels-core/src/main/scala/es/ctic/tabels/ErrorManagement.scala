package es.ctic.tabels

class TabelsException(msg:String) extends Exception(msg)

class ParseException(input : String, msg : String, line : Int, column : Int) extends TabelsException("Syntax error at line " + line + ", column " + column + ": " + msg)

class TemplateInstantiationException(msg:String) extends TabelsException(msg)

class UnboundVariableException(variable: Variable) extends TabelsException("Found unbound variable " + variable)
