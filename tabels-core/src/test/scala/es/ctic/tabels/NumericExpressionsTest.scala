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
    
    assertEquals(2 ,NumericFunctions.numericAdd(1,1))
    assertEquals(4 ,NumericFunctions.numericAdd(4,0))
    assertEquals(13 ,NumericFunctions.numericAdd(0,13))
   
    }
   
    @Test def numericSubstract{
    
    assertEquals(0 ,NumericFunctions.numericSubstract(1,1))
    assertEquals(4 ,NumericFunctions.numericSubstract(4,0))
    assertEquals(-13 ,NumericFunctions.numericSubstract(0,13))
   
    }
    
    @Test def numericMultiply{
    
    assertEquals(1 ,NumericFunctions.numericMultiply(1,1))
    assertEquals(0 ,NumericFunctions.numericMultiply(4,0))
    assertEquals(0 ,NumericFunctions.numericMultiply(0,13))
    assertEquals(12 ,NumericFunctions.numericMultiply(2,6))
   
    }
   
    @Test def numericDivide{
    
    assertEquals(1 ,NumericFunctions.numericDivide(1,1))
 //   assertEquals(0 ,NumericFunctions.numericDivide(4,0))
    assertEquals(0 ,NumericFunctions.numericDivide(0,13))
    assertEquals(3 ,NumericFunctions.numericDivide(6,2))
    assertEquals(0.5 ,NumericFunctions.numericDivide(1,2), 0.1)
   
    }
  
    @Test def numericIntegerDivide{
    
    assertEquals(1 ,NumericFunctions.numericIntegerDivide(1,1))
 //   assertEquals(0 ,NumericFunctions.numericIntegerDivide(4,0))
    assertEquals(0 ,NumericFunctions.numericIntegerDivide(0,13))
    assertEquals(3 ,NumericFunctions.numericIntegerDivide(6,2))
    assertEquals(0 ,NumericFunctions.numericIntegerDivide(1,2))
   
    }
  
    @Test def numericMod{
    
    assertEquals(0 ,NumericFunctions.numericMod(1,1))
 //   assertEquals(0 ,NumericFunctions.numericMod(4,0))
    assertEquals(0 ,NumericFunctions.numericMod(0,13))
    assertEquals(0 ,NumericFunctions.numericMod(6,2))
    assertEquals(1 ,NumericFunctions.numericMod(1,2))
   
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
    
    assertEquals(2 ,NumericFunctions.ceiling(1.5.asInstanceOf[Float]),0)
    assertEquals(2 ,NumericFunctions.ceiling(1.1.asInstanceOf[Float]),0)
    assertEquals(2 ,NumericFunctions.ceiling(1.7.asInstanceOf[Float]),0)
    assertEquals(1 ,NumericFunctions.ceiling(1),0)
   
    }
    
    @Test def floor{
    
    assertEquals(1 ,NumericFunctions.floor(1.5.asInstanceOf[Float]),0)
    assertEquals(1 ,NumericFunctions.floor(1.1.asInstanceOf[Float]),0)
    assertEquals(1 ,NumericFunctions.floor(1.7.asInstanceOf[Float]),0)
    assertEquals(1 ,NumericFunctions.floor(1),0)
   
    }
    
    @Test def round{
    
    assertEquals(2 ,NumericFunctions.round(1.5.asInstanceOf[Float]))
    assertEquals(1 ,NumericFunctions.round(1.1.asInstanceOf[Float]))
    assertEquals(2 ,NumericFunctions.round(1.7.asInstanceOf[Float]))
    assertEquals(1 ,NumericFunctions.round(1))
   
    }
    
  // FIXME: round-half-to-even is missing
    
    @Test def int{
    
    assertEquals(1 ,NumericFunctions.int(1))
    assertEquals(14 , NumericFunctions.int(14))
 //   assertEquals(345.56 , NumericFunctions.int("345,56".toInt))
 //   assertEquals(345.56 , NumericFunctions.int("345.56".toInt))
 //   assertEquals(23.345,56 , NumericFunctions.int("23.345,56".toInt))
  //  assertEquals(23.223.345,56 , NumericFunctions.int("23.223.345,56".toInt))
    
    }
    
    @Test def float{
    
    assertEquals(1.0 ,NumericFunctions.float(1),0)
    assertEquals(14.0 , NumericFunctions.float(14),0)
    assertEquals(Literal("345.56",XSD_INT) , NumericFunctions.float("345,56".toInt))
    assertEquals(Literal("345.56",XSD_INT) , NumericFunctions.float("345.56".toInt))
    assertEquals(Literal("23.345,56",XSD_INT) , NumericFunctions.float("23.345,56".toInt))
    assertEquals(Literal("23.223.345,56",XSD_INT) , NumericFunctions.float("23.223.345,56".toInt))
    }
  
    

}
