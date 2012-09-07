package es.ctic.tabels

import org.scalatest.junit.JUnitSuite
import org.junit.Test
import org.junit.Assert._
import java.io.File

class PXReaderTest extends JUnitSuite {
   
    private val reader =new PXReader(new File(this.getClass.getResource("/es/ctic/tabels/Test1.px").getFile.replace("%20"," ")))
    private val reader2 =new PXReader(new File(this.getClass.getResource("/es/ctic/tabels/simple.px").getFile.replace("%20"," ")))
    private val reader3 =new PXReader(new File(this.getClass.getResource("/es/ctic/tabels/simplePointTest.px").getFile.replace("%20"," ")))
    
    @Test def testReadHeadings {
        assertEquals("Sexo", reader.readHeadings(1))
        assertEquals("Año", reader.readHeadings(0))
        
    }
    @Test def testRead2Headings {
        assertEquals("sector", reader2.readHeadings(1))
        assertEquals("periodo", reader2.readHeadings(0))
        
    }
    
    @Test def testReadStub {
        assertEquals("País de nacionalidad", reader.readStub(0))
        
    }
    
    @Test def testRead2Stub {
        assertEquals("país", reader2.readStub(0))
        
    }
    
    @Test def testReadUnits {
        assertEquals(Seq("valores absolutos")(0), reader.readUnits(0))
        
    }
    
    @Test def testReadValues {
        assertEquals("1998", reader.readValues("Año")(0))
        assertEquals("2011", reader.readValues("Año")(13))
        assertEquals("Bélgica", reader.readValues("País de nacionalidad")(5))
        assertEquals("Mujeres", reader.readValues("Sexo")(2))
        
        
    }
    @Test def testRead2Values {
        assertEquals("1992", reader2.readValues("periodo")(0))
        assertEquals("2008", reader2.readValues("periodo")(16))
        assertEquals("Bélgica", reader2.readValues("país")(0))
        assertEquals("Total ", reader2.readValues("sector")(0))
        
        
    }
    
    @Test def testReadData {
        assertEquals("81227", reader.readData(0)(0))
        assertEquals("24627", reader.readData(2)(1))
        
    }
    @Test def testRead2Data {
        
        assertEquals("253", reader2.readData(1)(1))
        
    }
   /* @Test def testReadAll {
        assertEquals(null, reader.readAll)
        
    }
     @Test def testRead2All {
        assertEquals(null, reader2.readAll)
        
    }*/
     @Test def testRead3All {
        assertEquals(null, reader3.readAll)
        
    }
   @Test def testCalculateHeaders{
	   	assertEquals(null, reader.calculateHeaders(0))
	   	assertEquals(null, reader.calculateHeaders(1))
	   	assertEquals(null, reader.calculateHeaders(2))
   }
   @Test def testCalculateStubs{
	   	assertEquals(null, reader.calculateStubs(0))
	   	assertEquals(null, reader.calculateStubs(1))
	   	assertEquals(null, reader.calculateStubs(2))
   }
  
}