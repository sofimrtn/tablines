package es.ctic.tabels

import org.scalatest.junit.JUnitSuite
import org.junit.Test
import org.junit.Assert._

class TemplateTest extends JUnitSuite {

    val bn1 = BlankNode(Left("a"))
    val bn2 = BlankNode(Left("b"))
    val varX = Variable("x")
    val varY = Variable("y")
    val varZ = Variable("z")
    val template = Template(List(((bn1, varX, bn2)),
                                 ((bn1, varY, varZ))))

    @Test def variables() {
        assertEquals(Set(varX, varY, varZ), template.variables)
    }
    
    @Test def blankNodes() {
        assertEquals(Set(bn1, bn2), template.blankNodes)
    }
    
    @Test def blankNodeRenamingSubstitution() {
        val s = template.blankNodeRenamingSubstitution(0)
        assertEquals(2, s.size)
        assertEquals(bn1, s.toList(0)._1)
        assertFalse(s.toList(0)._2 equals s.toList(1)._2) // fresh nodes are disjoint
        val anotherS = template.blankNodeRenamingSubstitution(1)
        assertFalse(s.toList(0)._2 equals anotherS.toList(0)._2) // a different seed delivers different nodes
    }
  
}

//class TripleTemplateTest extends JUnitSuite {
//
//}