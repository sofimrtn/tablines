package es.ctic.tabels
 
class PrettyPrint(indent_ : Int = 0) extends AbstractVisitor {
    
    val str = new StringBuilder()
    var indent = indent_
 
    override def toString() = str.toString
    
    override def visit(start : S) {
        start.prefixes foreach { case (prefix,ns) => str append ("PREFIX " + prefix + ": " + ns.toString + "\n") }
        start.statementList foreach {
            indent = 0
            _.accept(this)
        }
        start.templateList foreach (template => str append "\n" append (template.toAbbrString(start.prefixes)))
    }
    
    override def visit(stmt : BlockStatement) {
        str append "{ "
        str append (stmt.childStatements map { subStmt =>
            val pp = new PrettyPrint(indent+4)
            subStmt.accept(pp)
            pp.toString
        } mkString " ; ")
        str append (" " * indent) append "}"
    }
    
    override def visit(stmt : LetStatement) {
        str append "\n" append (" " * indent) append "LET "
        str append stmt.variable append " = " append stmt.expression
        visitNestedStatement(stmt.nestedStatement, false)
    }
    
    override def visit(stmt : MatchStatement) {
        str append "\n" append (" " * indent) append "MATCH "
        if (stmt.tuple.variables.size == 1) {
            str append stmt.tuple.variables(0).toString() append " "
        } else {
            str append stmt.tuple.toString() append " "
        }
        str append (stmt.position map ("AT " + _) getOrElse "")
        str append (stmt.filter map ("FILTER " + _) getOrElse "")
        visitNestedStatement(stmt.nestedStatement, false)
    }
  
    override def visit(stmt : IteratorStatement) {
        str append "\n" append (" " * indent) append "FOR "
        str append (stmt.variable map (_ + " IN ") getOrElse "")
        str append stmt.dimension
        str append (stmt.filter map (" FILTER " + _) getOrElse "")
        str append (stmt.stopCond map (" UNTIL " + _) getOrElse "")
        visitNestedStatement(stmt.nestedStatement, true)
    }
    
    override def visit(stmt : SetInDimensionStatement) {
        str append "\n" append (" " * indent)
        str append (stmt.variable map ("SET " + _ + " ") getOrElse "")
        str append "IN " append stmt.dimension append " \"" append stmt.fixedDimension append "\""
        visitNestedStatement(stmt.nestedStatement, true)
    }
    
    def visitNestedStatement(stmts : Option[TabelsStatement], increaseIndent : Boolean) {
        val oldIndent = indent
        if (increaseIndent) {
            indent += 4
        }
        stmts map { _.accept(this) }
        indent = oldIndent
    }
  
}


