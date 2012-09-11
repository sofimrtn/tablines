package es.ctic.tabels

import org.scalatest.junit.JUnitSuite
import org.junit.Test
import org.junit.Assert._
import java.io.File

class PXReaderSimpleTest extends JUnitSuite {
   
    private val reader =new PXReader(new File(this.getClass.getResource("/es/ctic/tabels/Test1.px").getFile.replace("%20"," ")))
       
    @Test def testReadHeadings {
        assertEquals("Sexo", reader.readHeadings(1))
        assertEquals("Año", reader.readHeadings(0))
        
    }
        
    @Test def testReadStub {
        assertEquals("País de nacionalidad", reader.readStub(0))
        
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
    @Test def testReadData {
        assertEquals("81227", reader.readData(0)(0))
        assertEquals("24627", reader.readData(2)(1))
        
    }
    
    @Test def testReadAll {
        assertEquals("", reader.readAll(0)(0))
        assertEquals("", reader.readAll(1)(0))
        assertEquals("2001@Año", reader.readAll(0)(10))
        assertEquals("Bulgaria@País de nacionalidad", reader.readAll(8)(0))
        assertEquals("81227", reader.readAll(2)(1))
        assertEquals("261", reader.readAll(6)(11))
        
    }
    
   @Test def testCalculateHeaders{
	   	assertEquals("1998@Año", reader.calculateHeaders(0).asInstanceOf[Array[String]](1))
	   	assertEquals("2003@Año", reader.calculateHeaders(0).asInstanceOf[Array[String]](18))
	   	assertEquals("2005@Año", reader.calculateHeaders(0).asInstanceOf[Array[String]](22))
	    assertEquals("Total@Sexo", reader.calculateHeaders(1).asInstanceOf[Array[String]](1))
	    assertEquals("Hombres@Sexo", reader.calculateHeaders(1).asInstanceOf[Array[String]](5))
	    assertEquals("Mujeres@Sexo", reader.calculateHeaders(1).asInstanceOf[Array[String]](9))
	   	//assertEquals(null, reader.calculateHeaders(2))
   }
   @Test def testCalculateStubs{
	    assertEquals("TOTAL@País de nacionalidad", reader.calculateStubs(0).asInstanceOf[Array[String]](0))
	   	assertEquals("Dinamarca@País de nacionalidad", reader.calculateStubs(8).asInstanceOf[Array[String]](0))
	   	assertEquals("APATRIDAS@País de nacionalidad", reader.calculateStubs(127).asInstanceOf[Array[String]](0))
	    
   }
  
}

class PXReaderSimpleDotsTest extends JUnitSuite {
   
    private val reader =new PXReader(new File(this.getClass.getResource("/es/ctic/tabels/Simple.px").getFile.replace("%20"," ")))
       
    @Test def testReadHeadings {
        assertEquals("sector", reader.readHeadings(1))
        assertEquals("periodo", reader.readHeadings(0))
        
    }
        
    @Test def testReadStub {
        assertEquals("país", reader.readStub(0))
        
    }
    
    @Test def testReadUnits {
        assertEquals(Seq(" millones de metros cúbicos.")(0), reader.readUnits(0))
        
    }
    
    @Test def testReadValues {
        assertEquals("1992", reader.readValues("periodo")(0))
        assertEquals("2005", reader.readValues("periodo")(13))
        assertEquals("Estonia", reader.readValues("país")(5))
        assertEquals("Industria manufacturera", reader.readValues("sector")(1))
        
        
    }
    @Test def testReadData {
        assertEquals("\"..\"", reader.readData(0)(0))
        assertEquals("253", reader.readData(1)(1))
        
    }
    
    @Test def testReadAll {
        assertEquals("", reader.readAll(0)(0))
        assertEquals("", reader.readAll(1)(0))
        assertEquals("1995@periodo", reader.readAll(0)(10))
        assertEquals("Irlanda@país", reader.readAll(8)(0))
        assertEquals("\"..\"", reader.readAll(2)(1))
        assertEquals("467", reader.readAll(6)(11))
    }
    
   @Test def testCalculateHeaders{
	   	assertEquals("1992@periodo", reader.calculateHeaders(0).asInstanceOf[Array[String]](1))
	   	assertEquals("1997@periodo", reader.calculateHeaders(0).asInstanceOf[Array[String]](18))
	   	assertEquals("1999@periodo", reader.calculateHeaders(0).asInstanceOf[Array[String]](22))
	    assertEquals("Total @sector", reader.calculateHeaders(1).asInstanceOf[Array[String]](1))
	    assertEquals("Industria manufacturera@sector", reader.calculateHeaders(1).asInstanceOf[Array[String]](5))
	    assertEquals("Sector doméstico@sector", reader.calculateHeaders(1).asInstanceOf[Array[String]](9))
	   	//assertEquals(null, reader.calculateHeaders(2))
   }
   @Test def testCalculateStubs{
	    assertEquals("Bélgica@país", reader.calculateStubs(0).asInstanceOf[Array[String]](0))
	   	assertEquals("Grecia@país", reader.calculateStubs(7).asInstanceOf[Array[String]](0))
	   	assertEquals("Reino Unido@país", reader.calculateStubs(26).asInstanceOf[Array[String]](0))
	    
   }
  
}