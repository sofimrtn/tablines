package es.ctic.tabels

import org.scalatest.junit.JUnitSuite
import org.junit.Test
import org.junit.Assert._

class LuceneTest extends JUnitSuite{
	
  @Test def testQueryTest{
    val lucene = new Lucene()
    val wA = new WorkArea
     assertEquals( Resource("http://dbpedia.org/resource/Andorra"),lucene.query(wA,"Andorra","first"))
     assertEquals( Resource("http://dbpedia.org/resource/Rome"),lucene.query(wA,"roma","first"))
     assertEquals( Resource("http://dbpedia.org/resource/Madrid"),lucene.query(wA,"madrid", "first"))
     assertEquals( Resource("http://dbpedia.org/resource/Madrid"),lucene.query(wA,"madrid", "very-best"))
     assertEquals( Resource("http://example.org/ResourceNotDisambiguated?query=casa"),lucene.query(wA,"casa", "very-best"))
     assertEquals( Resource("http://example.org/ResourceNotDisambiguated?query=madrid"),lucene.query(wA,"madrid", "single"))
     assertEquals( Resource("http://dbpedia.org/resource/Pulmonary_alveolus"),lucene.query(wA,"alveolo", "single"))
     assertEquals( Resource("http://dbpedia.org/resource/Rome,_Open_City"),lucene.query(wA,"rome", rdfType=Some(Resource("http://schema.org/Movie"))))
     
     assertEquals( None,lucene.query(wA,"arrimdsf"))
     assertEquals( None,lucene.query(wA,""))
  }
}