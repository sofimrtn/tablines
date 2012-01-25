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
        assertEquals(Set(bn1, bn2), s.keys)
        assertEquals("Fresh nodes are disjoin", s.size, s.values.toSet.size)
        val anotherS = template.blankNodeRenamingSubstitution(1)
        assertEquals("Different seed delivers different nodes", s.size*2, (s.values.toSet ++ anotherS.values.toSet).size)
    }
  
}

//class TripleTemplateTest extends JUnitSuite {
//
//}