package es.ctic.tabels

import es.ctic.tabels.Dimension._
import org.scalatest.junit.JUnitSuite
import org.junit.Test
import org.junit.Assert._

class NumericExpressionsTest extends JUnitSuite {
  
  implicit val evaluationContext = EvaluationContext()
    
    // ==========================================    
    // XPath 2.0 operators on numeric values (6.2)
    // ==========================================    
    
    @Test def numericAdd{
    
    assertEquals(2 ,NumericFunctions.numericAdd(1,1),0)
    assertEquals(4 ,NumericFunctions.numericAdd(4,0),0)
    assertEquals(13 ,NumericFunctions.numericAdd(0,13),0)
   
    }
  	

   
    @Test def numericSubstract{
    
    assertEquals(0 ,NumericFunctions.numericSubstract(1,1),0)
    assertEquals(4 ,NumericFunctions.numericSubstract(4,0),0)
    assertEquals(-13 ,NumericFunctions.numericSubstract(0,13),0)
   
    }
    
    @Test def numericMultiply{
    
    assertEquals(1 ,NumericFunctions.numericMultiply(1,1),0)
    assertEquals(0 ,NumericFunctions.numericMultiply(4,0),0)
    assertEquals(0 ,NumericFunctions.numericMultiply(0,13),0)
    assertEquals(12 ,NumericFunctions.numericMultiply(2,6),0)
   
    }
   
    @Test def numericDivide{
    
    assertEquals(1 ,NumericFunctions.numericDivide(1,1),0)
    assertEquals(0 ,NumericFunctions.numericDivide(0,13),0)
    assertEquals(3 ,NumericFunctions.numericDivide(6,2),0)
    assertEquals(0.5 ,NumericFunctions.numericDivide(1,2), 0.1)
   
    }
  
    @Test def numericIntegerDivide{
    
    assertEquals(1 ,NumericFunctions.numericIntegerDivide(1,1),0)
    assertEquals(0 ,NumericFunctions.numericIntegerDivide(0,13),0)
    assertEquals(3 ,NumericFunctions.numericIntegerDivide(6,2),0)
    assertEquals(1 ,NumericFunctions.numericIntegerDivide(1,2),0)
   
    }
  
    @Test def numericMod{
    
    assertEquals(0 ,NumericFunctions.numericMod(1,1),0)
    assertEquals(0 ,NumericFunctions.numericMod(0,13),0)
    assertEquals(0 ,NumericFunctions.numericMod(6,2),0)
    assertEquals(1 ,NumericFunctions.numericMod(1,2),0)
   
    }
    
    @Test ( expected = classOf[ZeroDivisionException] )
  	def DivisionByZeroException {
  		assertEquals(0 ,NumericFunctions.numericDivide(4,0),0)
  		assertEquals(0 ,NumericFunctions.numericIntegerDivide(4,0),0)
  		assertEquals(0 ,NumericFunctions.numericMod(4,0),0)
    }
  // FIXME: numeric-unary-plus is missing
  // FIXME: numeric-unary-minus is missing
    
    // ==========================================
    // XPath 2.0 comparison operators on numeric values (6.3)
    // ==========================================    
    
   @Test def equal{
    
    assertTrue(NumericFunctions.equal(1,1))
    assertFalse(NumericFunctions.equal(4,0))
    assertFalse(NumericFunctions.equal(0,4))
       
  }
  
  @Test def greaterThan{
    
    assertTrue(NumericFunctions.greaterThan(4,0))
    assertFalse(NumericFunctions.greaterThan(0,4))
    assertFalse(NumericFunctions.greaterThan(1,1))
           
  }
  
  @Test def lessThan{
    
    assertTrue(NumericFunctions.lessThan(0,4))
    assertFalse(NumericFunctions.lessThan(4,0))
    assertFalse(NumericFunctions.lessThan(1,1))
           
  }

    // ==========================================    
    // XPath 2.0 functions on numeric values (6.4)
    // ==========================================

    @Test def abs{
    
    assertEquals(1.5 ,NumericFunctions.abs(1.5.asInstanceOf[Float]), 0)
    assertEquals(0 ,NumericFunctions.abs(0), 0)
    assertEquals(1 ,NumericFunctions.abs(-1), 0)
   
    }
    
    @Test def ceiling{
    
    assertEquals(2 ,NumericFunctions.ceiling(1.5),0)
    assertEquals(2 ,NumericFunctions.ceiling(1.1),0)
    assertEquals(2 ,NumericFunctions.ceiling(1.7),0)
    assertEquals(1 ,NumericFunctions.ceiling(1),0)
   
    }
    
    @Test def floor{
    
    assertEquals(1 ,NumericFunctions.floor(1.5),0)
    assertEquals(1 ,NumericFunctions.floor(1.1),0)
    assertEquals(1 ,NumericFunctions.floor(1.7),0)
    assertEquals(1 ,NumericFunctions.floor(1),0)
   
    }
    
    @Test def round{
    
    assertEquals(2 ,NumericFunctions.round(1.5),0)
    assertEquals(1 ,NumericFunctions.round(1.1),0)
    assertEquals(2 ,NumericFunctions.round(1.7),0)
    assertEquals(1 ,NumericFunctions.round(1))
   
    }
    
  // FIXME: round-half-to-even is missing
    
    @Test def int{
    
    assertEquals(1 ,NumericFunctions.int(1))
    assertEquals(14 , NumericFunctions.int(14))
    assertEquals(0 ,NumericFunctions.int(Literal("0",XSD_INT)),0)
    
    }
    
    @Test def intOrElse {
        assertEquals(3 , NumericFunctions.intOrElse(3,5))
        assertEquals(3 , NumericFunctions.intOrElse("3",5))
        assertEquals(3 , NumericFunctions.intOrElse("3.14",5))        
        assertEquals(5 , NumericFunctions.intOrElse("Foo",5))
    }
    
    @Test def float {
        assertEquals(1.0 ,NumericFunctions.float(1),0)
        assertEquals(14.0 , NumericFunctions.float(14),0)
        assertEquals(0.3136143982410431 ,NumericFunctions.float(Literal("0.3136143982410431",XSD_FLOAT)),0)
    }
    
    @Test def double {
        assertEquals(1.0 ,NumericFunctions.double(1.0),0)
        assertEquals(0.3136144 ,NumericFunctions.double(Literal("0.3136144",XSD_DOUBLE)),0)
    }
    
    @Test def doubleOrElse {
        assertEquals(3.0, NumericFunctions.doubleOrElse(3.0,5.0),0)
        assertEquals(3.0, NumericFunctions.doubleOrElse("3.0",5.0),0)        
        assertEquals(5.0, NumericFunctions.doubleOrElse("Foo",5.0),0)        
    }
    @Test def isDouble {
        assertTrue(NumericFunctions.isDouble(3.0))
        assertFalse(NumericFunctions.isDouble("3.0"))  
        assertFalse(NumericFunctions.isDouble(1)) 
        assertFalse(NumericFunctions.isDouble("."))        
    }
    @Test def canBeDouble {
        assertTrue(NumericFunctions.canBeDouble(3.0))
        assertTrue(NumericFunctions.canBeDouble("3.0"))  
        assertTrue(NumericFunctions.canBeDouble("1"))
        assertTrue(NumericFunctions.canBeDouble(1)) 
        assertTrue(NumericFunctions.canBeDouble("1."))
        assertTrue(NumericFunctions.canBeDouble(".4"))
        assertFalse(NumericFunctions.canBeDouble("."))
        assertFalse(NumericFunctions.canBeDouble("foo"))     
    }
    @Test def isInt {
        assertTrue(NumericFunctions.isInt(3))
        assertFalse(NumericFunctions.isInt("3"))  
        assertFalse(NumericFunctions.isInt(1.0)) 
        assertFalse(NumericFunctions.isInt("."))        
    }
    @Test def canBeInt {
        assertTrue(NumericFunctions.canBeInt(3))
        assertTrue(NumericFunctions.canBeInt("3"))  
        assertTrue(NumericFunctions.canBeInt("1.0"))
        assertTrue(NumericFunctions.canBeInt(1.0)) 
        assertTrue(NumericFunctions.canBeInt("1."))
        assertTrue(NumericFunctions.canBeInt(".4"))
        assertFalse(NumericFunctions.canBeInt("."))
        assertFalse(NumericFunctions.canBeInt("foo"))     
    }
    

}
