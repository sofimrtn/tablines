package es.ctic.tabels
 
class PrettyPrint extends AbstractVisitor {
    
    val str = new StringBuilder()
    var indent : Int = 0
 
    override def toString() = str.toString
    
    override def visit(start : S) {
        start.prefixes foreach { case (prefix,ns) => str append ("PREFIX " + prefix + ": <" + ns.toString + ">\n") }
        start.statementList foreach {
            indent = 0
            _.accept(this)
        }
        start.templateList foreach (template => str append (template.toAbbrString(start.prefixes)))
    }
    
    override def visit(stmt : LetStatement) {
        str append (" " * indent) append "LET "
        str append stmt.variable append " = " append stmt.expression.get
        str append "\n"
        visitChildPatterns(stmt.childPatterns, false)
    }
    
    override def visit(stmt : MatchStatement) {
        str append (" " * indent) append "MATCH ... "
        // FIXME
        str append "\n"
        visitChildPatterns(stmt.childPatterns, false)
    }
  
    override def visit(stmt : IteratorStatement) {
        str append (" " * indent) append "FOR "
        str append (stmt.variable map (_ + " IN ") getOrElse "")
        str append stmt.dimension
        str append (stmt.filter map (" FILTER " + _) getOrElse "")
        str append (stmt.stopCond map (" UNTIL " + _) getOrElse "")
        str append "\n"
        visitChildPatterns(stmt.childPatterns, true)
    }
    
    override def visit(stmt : SetInDimensionStatement) {
        str append (" " * indent)
        str append (stmt.variable map ("SET " + _ + " ") getOrElse "")
        str append "IN " append stmt.dimension append " \"" append stmt.fixedDimension append "\""
        str append "\n"
        visitChildPatterns(stmt.childPatterns, true)
    }
    
    def visitChildPatterns(stmts : Seq[TabelsStatement], increaseIndent : Boolean) {
        val oldIndent = indent
        if (increaseIndent) {
            indent += 4
        }
        stmts foreach { _.accept(this) }
        indent = oldIndent
    }
  
}


