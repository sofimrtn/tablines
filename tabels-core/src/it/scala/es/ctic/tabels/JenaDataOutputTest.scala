package es.ctic.tabels

import org.scalatest.junit.JUnitSuite
import org.junit.{Test,Before}
import org.junit.Assert._

class JenaDataOutputITest extends JUnitSuite {
    
    var dataOutput : JenaDataOutput = null
    
    @Before def setUp {
        dataOutput = new JenaDataOutput()
    }
 
    @Test def testFetchDescription {
        dataOutput.fetchDescription("http://dbpedia.org/resource/Oviedo")
        assertTrue(dataOutput.model.size() > 0)
    }
   
    @Test ( expected = classOf[ResourceCannotBeRetrievedException] )
  	def testFetchDescriptionBrokenLink {
  		dataOutput.fetchDescription("http://brooookenLiiiink.org/resource/Oviedo")
    }
    
}

