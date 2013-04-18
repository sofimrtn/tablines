package es.ctic.tabels

import es.ctic.tabels.Dimension._
import org.scalatest.junit.JUnitSuite
import org.junit.Test
import org.junit.Assert._

class MiscellaneaExpressionsTest extends JUnitSuite {
  
  implicit val evaluationContext = EvaluationContext()
  	 
  //FIXME:DBPediaDisambiguation3 and DBPediaDisambiguation11 is missing
	
  	 @Test def setLangTag{
    
     assertEquals(Literal("caracola", XSD_STRING, "es") ,MiscellaneaFunctions.setLangTag("caracola","es"))
     assertEquals(Literal("seashell", XSD_STRING, "en") ,MiscellaneaFunctions.setLangTag("seashell","en"))
     assertEquals(Literal("Tritonshorn", XSD_STRING, "gr") ,MiscellaneaFunctions.setLangTag("Tritonshorn","gr"))
   
     } 
  	
  	@Test def boolean{
    
     assertTrue(MiscellaneaFunctions.boolean(true))
     assertFalse(MiscellaneaFunctions.boolean(false))
     
   
     }

}