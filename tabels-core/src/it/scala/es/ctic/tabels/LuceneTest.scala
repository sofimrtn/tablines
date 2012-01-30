package es.ctic.tabels

import org.scalatest.junit.JUnitSuite
import org.junit.Test
import org.junit.Assert._

class LuceneTest extends JUnitSuite{
 val lucene = new Lucene()
 val evaluationContext: EvaluationContext = null
  @Test def testQueryTest{
   
    
     assertEquals( NamedResource("http://dbpedia.org/resource/Andorra"),lucene.query(evaluationContext,"Andorra","first"))
     assertEquals( NamedResource("http://dbpedia.org/resource/Rome"),lucene.query(evaluationContext,"roma","first"))
     assertEquals( NamedResource("http://dbpedia.org/resource/Oviedo"),lucene.query(evaluationContext,"oviedo", "first", Some(NamedResource("""http://dbpedia.org/ontology/PopulatedPlace"""))))
     assertEquals( NamedResource("http://example.org/ResourceNotDisambiguated?query=madrid"),lucene.query(evaluationContext,"madrid", "very-best"))
     assertEquals( NamedResource("http://example.org/ResourceNotDisambiguated?query=casa"),lucene.query(evaluationContext,"casa", "very-best"))
     assertEquals( NamedResource("http://example.org/ResourceNotDisambiguated?query=madrid"),lucene.query(evaluationContext,"madrid", "single"))
     assertEquals( NamedResource("http://dbpedia.org/resource/Trisquel"),lucene.query(evaluationContext,"trisquel", "single"))
     assertEquals( NamedResource("http://dbpedia.org/resource/Rome-Paris-Rome"),lucene.query(evaluationContext,"rome", rdfType=Some(NamedResource("http://schema.org/Movie"))))
   
    
     assertEquals( NamedResource("http://example.org/ResourceNotDisambiguated?query=arrimdsf"),lucene.query(evaluationContext,"arrimdsf"))
     
  }
  @Test ( expected = classOf[LuceneQueryException] )
  	def testEmptyQueryt {
  		lucene.query(evaluationContext,"")
  		
    }
}

