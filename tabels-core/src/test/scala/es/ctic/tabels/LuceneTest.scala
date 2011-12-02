package es.ctic.tabels

import org.scalatest.junit.JUnitSuite
import org.junit.Test
import org.junit.Assert._

class LuceneTest extends JUnitSuite{
	
  @Test def testQueryTest{
    val lucene = new Lucene()
     assertEquals( Some(Resource("http://dbpedia.org/resource/Andorra")),lucene.query("Andorra"))
     assertEquals( None,lucene.query("arrimdsf"))
     assertEquals( None,lucene.query(""))
  }
}