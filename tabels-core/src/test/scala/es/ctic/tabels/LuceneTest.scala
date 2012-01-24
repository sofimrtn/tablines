package es.ctic.tabels

import org.scalatest.junit.JUnitSuite
import org.junit.Test
import org.junit.Assert._

class LuceneTest extends JUnitSuite{
	
  @Test def testQueryTest{
    val lucene = new Lucene()
    val evaluationContext: EvaluationContext = null
     assertEquals( NamedResource("http://dbpedia.org/resource/Andorra"),lucene.query(evaluationContext,"Andorra","first"))
     assertEquals( NamedResource("http://dbpedia.org/resource/Rome"),lucene.query(evaluationContext,"roma","first"))
     assertEquals( NamedResource("http://dbpedia.org/resource/Madrid"),lucene.query(evaluationContext,"madrid", "first"))
     assertEquals( NamedResource("http://dbpedia.org/resource/Madrid"),lucene.query(evaluationContext,"madrid", "very-best"))
     assertEquals( NamedResource("http://example.org/ResourceNotDisambiguated?query=casa"),lucene.query(evaluationContext,"casa", "very-best"))
     assertEquals( NamedResource("http://example.org/ResourceNotDisambiguated?query=madrid"),lucene.query(evaluationContext,"madrid", "single"))
     assertEquals( NamedResource("http://dbpedia.org/resource/Pulmonary_alveolus"),lucene.query(evaluationContext,"alveolo", "single"))
     assertEquals( NamedResource("http://dbpedia.org/resource/Rome,_Open_City"),lucene.query(evaluationContext,"rome", rdfType=Some(NamedResource("http://schema.org/Movie"))))
   
    
     assertEquals( None,lucene.query(evaluationContext,"arrimdsf"))
     assertEquals( None,lucene.query(evaluationContext,""))
  }
}