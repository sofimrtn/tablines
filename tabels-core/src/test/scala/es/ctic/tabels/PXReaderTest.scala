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
        assertEquals(Seq("millones de metros cúbicos.")(0), reader.readUnits(0))
        
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
class PXReaderFormatTest extends JUnitSuite {
   
    private val reader =new PXReader(new File(this.getClass.getResource("/es/ctic/tabels/EIE_es_2539.px").getFile.replace("%20"," ")))
       
    @Test def testReadHeadings {
        assertEquals("Periodo", reader.readHeadings(1))
        assertEquals("Grupos de tamano", reader.readHeadings(0))
        
    }
        
    @Test def testReadStub {
        assertEquals("Sectores", reader.readStub(0))
        
    }
    
    @Test def testReadUnits {
        assertEquals(Seq("Número de empresas")(0), reader.readUnits(0))
        
    }
    
    @Test def testReadValues {
        assertEquals("2008", reader.readValues("Periodo")(0))
        assertEquals("2010", reader.readValues("Periodo")(2))
        assertEquals("20 o más personas ocupadas", reader.readValues("Grupos de tamano")(2))
        assertEquals("001 CNAE 05. Extracción de carbón (antracita, hulla y lignito)", reader.readValues("Sectores")(1))
   }
    
    @Test def testReadData {
        assertEquals("108", reader.readData(7)(8))
        assertEquals("48", reader.readData(1)(1))
        
    }
    
    @Test def testReadAll {
        assertEquals("", reader.readAll(0)(0))
        assertEquals("", reader.readAll(1)(0))
        assertEquals("108", reader.readAll(9)(9))
        assertEquals("027 CNAE 16.10. Aserrado y cepillado de la madera@Sectores", reader.readAll(29)(0))
        assertEquals("2010@Periodo", reader.readAll(1)(9))
        assertEquals("20 o más personas ocupadas@Grupos de tamano", reader.readAll(0)(9))
        assertEquals("18462", reader.readAll(2)(9))
        assertEquals("Total industria@Sectores", reader.readAll(2)(0))
        
    }
    
   @Test def testCalculateHeaders{
	   	assertEquals("2010@Periodo", reader.calculateHeaders(1).asInstanceOf[Array[String]](9))
	   	assertEquals("20 o más personas ocupadas@Grupos de tamano", reader.calculateHeaders(0).asInstanceOf[Array[String]](9))
	   	assertEquals("", reader.calculateHeaders(0).asInstanceOf[Array[String]](0))
	    assertEquals("", reader.calculateHeaders(1).asInstanceOf[Array[String]](0))
	    assertEquals("2009@Periodo", reader.calculateHeaders(1).asInstanceOf[Array[String]](5))
	    assertEquals("2008@Periodo", reader.calculateHeaders(1).asInstanceOf[Array[String]](1))
	   	//assertEquals(null, reader.calculateHeaders(2))
   }
   @Test def testCalculateStubs{
	    assertEquals("022 Confección de prendas de vestir, excepto de peletería@Sectores", reader.calculateStubs(22).asInstanceOf[Array[String]](0))
	   	assertEquals("025 Preparación, curtido y acabado del cuero; fabricación de artículos de marroquinería, viaje y de guarnicionería y talabartería; preparación y teñido de pieles@Sectores", reader.calculateStubs(25).asInstanceOf[Array[String]](0))
	   	assertEquals("026 Fabricación de calzado@Sectores", reader.calculateStubs(26).asInstanceOf[Array[String]](0))
	    
   }
  
}

class PXReaderEduTest extends JUnitSuite {
   
    private val reader =new PXReader(new File(this.getClass.getResource("/es/ctic/tabels/Edu.px").getFile.replace("%20"," ")))
       
  /*  @Test def testReadHeadings {
       assertEquals("curso acad?mico", reader.readHeadings(0))
        
    }*/
        
    @Test def testReadStub {
        assertEquals("tipo de presentaci¢n", reader.readStub(0))
        assertEquals("clase de ense¤anzas", reader.readStub(1))
    }
    
    @Test def testReadUnits {
        assertEquals(Seq("Profesorado")(0), reader.readUnits(0))
        
    }
    
    @Test def testReadValues {
        assertEquals("Indices (base 2000/2001 = 100)         ", reader.readValues("tipo de presentaci¢n")(1))
        assertEquals("- ESTUDIOS DE 1ER Y 2§ CICLO (2)", reader.readValues("clase de ense¤anzas")(2))
        //assertEquals("2005/2006", reader.readValues("curso acad‚mico")(4))
         
    }
    @Test def testReadData {
        assertEquals("72.50", reader.readData(7)(7))
        assertEquals("\"..\"", reader.readData(1)(1))
        
    }
    
    @Test def testReadAll {
        assertEquals("", reader.readAll(0)(0))
        assertEquals("", reader.readAll(0)(1))
        assertEquals("46.09", reader.readAll(8)(12))
        assertEquals("Indices (base 2000/2001 = 100)         @tipo de presentaci¢n", reader.readAll(8)(0))
      //  assertEquals("2010@curso acad‚mico", reader.readAll(0)(6))
        assertEquals("ENSE¥ANZA UNIVERSITARIA (1)@clase de ense¤anzas", reader.readAll(5)(1))
    }
    
   /*@Test def testCalculateHeaders{
	   	assertEquals("2010@Periodo", reader.calculateHeaders(0).asInstanceOf[Array[String]](0))
	   	assertEquals("20 o más personas ocupadas@Grupos de tamano", reader.calculateHeaders(0).asInstanceOf[Array[String]](9))
	   	assertEquals("", reader.calculateHeaders(0).asInstanceOf[Array[String]](0))
	    assertEquals("", reader.calculateHeaders(1).asInstanceOf[Array[String]](0))
	    assertEquals("2009@Periodo", reader.calculateHeaders(1).asInstanceOf[Array[String]](5))
	    assertEquals("2008@Periodo", reader.calculateHeaders(1).asInstanceOf[Array[String]](1))
	   	//assertEquals(null, reader.calculateHeaders(2))
   }*/
  @Test def testCalculateStubs{
	    assertEquals("Indices (base 2000/2001 = 100)         @tipo de presentaci¢n", reader.calculateStubs(6).asInstanceOf[Array[String]](0))
	   //	assertEquals("025 Preparación, curtido y acabado del cuero; fabricación de artículos de marroquinería, viaje y de guarnicionería y talabartería; preparación y teñido de pieles@Sectores", reader.calculateStubs(25).asInstanceOf[Array[String]](0))
	   	//assertEquals("026 Fabricación de calzado@Sectores", reader.calculateStubs(26).asInstanceOf[Array[String]](0))
	    
   }
  
}
