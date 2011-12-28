package es.ctic.tabels

import org.scalatest.junit.JUnitSuite
import org.junit.Test
import org.junit.Assert._

class LuceneTest extends JUnitSuite{
	
  @Test def testQueryTest{
    val lucene = new Lucene()
     assertEquals( Some(Resource("http://dbpedia.org/resource/Andorra")),lucene.query("Andorra"))
     assertEquals( Some(Resource("http://dbpedia.org/resource/Roman")),lucene.query("roma"))
     assertEquals( Some(Resource("http://dbpedia.org/resource/Madrid")),lucene.query("madrid", "first"))
     assertEquals( Some(Resource("http://dbpedia.org/resource/Madrid")),lucene.query("madrid", "very-best"))
     assertEquals( None,lucene.query("casa", "very-best"))
     assertEquals( None,lucene.query("madrid", "single"))
     assertEquals( Some(Resource("http://dbpedia.org/resource/Pulmonary_alveolus")),lucene.query("alveolo", "single"))
     assertEquals( Some(Resource("http://dbpedia.org/resource/Rome,_Open_City")),lucene.query("rome", rdfType=Some(Resource("http://schema.org/Movie"))))
     
     assertEquals( None,lucene.query("arrimdsf"))
     assertEquals( None,lucene.query(""))
  }
}