package es.ctic.tabels
 
import scala.collection.mutable.HashSet

class VisitorGetVars extends AbstractVisitor {
    
   val varsContained:scala.collection.mutable.Set[Variable]= new HashSet()
    
   override def visit(start : S) {
        start.statementList foreach {
          _.accept(this)
        }
    }
    
    override def visit(stmt : BlockStatement) {
           stmt.childStatements foreach {
             _.accept(this)
           }
    }
    
    override def visit(stmt : LetStatement) {
      varsContained += stmt.variable
       stmt.nestedStatement match{
         case None =>
         case Some(_) => stmt.nestedStatement.get.accept(this)
       }
    }
    
    override def visit(stmt : MatchStatement) {
        
      varsContained ++= stmt.tuple.variables
       stmt.nestedStatement match{
         case None =>
         case Some(_) => stmt.nestedStatement.get.accept(this)
       }

    }
  
    override def visit(stmt : IteratorStatement) {
       stmt.variable match{
         case None =>
         case Some(_) => varsContained += stmt.variable.get
       }
       stmt.nestedStatement match{
         case None =>
         case Some(_) => stmt.nestedStatement.get.accept(this)
       }
    }
    
    override def visit(stmt : SetInDimensionStatement) {
        stmt.variable match{
         case None =>
         case Some(_) => varsContained += stmt.variable.get
       }
       stmt.nestedStatement match{
         case None =>
         case Some(_) => stmt.nestedStatement.get.accept(this)
       }
    }
    
    
  
}


