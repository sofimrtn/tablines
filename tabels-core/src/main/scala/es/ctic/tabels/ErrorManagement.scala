package es.ctic.tabels

class TemplateInstantiationException(msg:String) extends Exception(msg)
class UnboundVariableException(variable: Variable) extends Exception("Found unbound variable " + variable)