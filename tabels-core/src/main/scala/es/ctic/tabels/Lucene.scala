package es.ctic.tabels
import org.apache.lucene.analysis.standard.StandardAnalyzer
import org.apache.lucene.analysis.LimitTokenCountAnalyzer
import org.apache.lucene.document.Document
import org.apache.lucene.document.Field
import org.apache.lucene.index.IndexWriter
import org.apache.lucene.index.IndexWriterConfig
//import org.apache.lucene.queryParser.ParseException;
import org.apache.lucene.queryParser.QueryParser
import org.apache.lucene.search.BooleanQuery
import org.apache.lucene.search.BooleanClause.Occur
import org.apache.lucene.search.TermQuery
import org.apache.lucene.index.Term
import org.apache.lucene.search.IndexSearcher
import org.apache.lucene.search.Query
import org.apache.lucene.search.ScoreDoc
import org.apache.lucene.search.TopScoreDocCollector
import org.apache.lucene.store.Directory
import org.apache.lucene.store.RAMDirectory
import org.apache.lucene.store.FSDirectory
import org.apache.lucene.util.Version
import org.apache.lucene.analysis.PerFieldAnalyzerWrapper
import org.apache.lucene.index.IndexWriterConfig.OpenMode
import grizzled.slf4j.Logging
import java.io.{File,FileNotFoundException}
import scala.io.Source
import com.hp.hpl.jena.rdf.model.ModelFactory 
import com.hp.hpl.jena.vocabulary.RDFS
import com.hp.hpl.jena.vocabulary.RDF
import scala.collection.mutable.ListBuffer

class Lucene extends Logging{
  
  var analyzer = new org.apache.lucene.analysis.es.SpanishAnalyzer(Version.LUCENE_33)
  val directory = FSDirectory.open(new File("/tmp/testindex"))
  val iWriterConfig = new IndexWriterConfig(Version.LUCENE_33, new LimitTokenCountAnalyzer(analyzer,2500))
  iWriterConfig.setOpenMode(OpenMode.CREATE)
  val iwriter = new IndexWriter(directory, iWriterConfig)
  try{ 
     loadDocs(iwriter)
     }
  
    finally {
     iwriter.close()
   } 
  
    def loadDocs(iwriter: IndexWriter) {
       
    val model = ModelFactory.createDefaultModel()
    model.read("file:/C:/Users/alfonso.noriega/Documents/tabular bells/Tabels Project/tabels-core/labels_es.nt", "N-TRIPLE")
    model.read("file:/C:/Users/alfonso.noriega/Documents/tabular bells/Tabels Project/tabels-core/instance_types_es.nt", "N-TRIPLE")
    val iterator = model.listStatements(null, RDFS.label ,null)
    
    while(iterator.hasNext){
      val statement = iterator.nextStatement()
      val doc = new Document()
      doc.add(new Field("resource", statement.getSubject.getURI, Field.Store.YES,Field.Index.NOT_ANALYZED));
      doc.add(new Field("label", statement.getString, Field.Store.YES,Field.Index.ANALYZED));
      
      val typeIterator = statement.getSubject.listProperties(RDF.`type`)
      while(typeIterator.hasNext){
        val typeStatement = typeIterator.nextStatement()
        doc.add(new Field("type", typeStatement.getResource.getURI, Field.Store.YES,Field.Index.NOT_ANALYZED));
      }
      
      iwriter.addDocument(doc)
    }
      
  }
  
  def query(q : String ,strategy:String = "first", rdfType:Option[Resource] = None) : Option[Resource]  ={
   
    // Now search the index:
    val isearcher = new IndexSearcher(directory, true) // read-only=true
    // Parse a simple query that searches for "text":
    var buffList = new ListBuffer[Resource]
    val  aWrapper = new PerFieldAnalyzerWrapper(analyzer)
    		aWrapper.addAnalyzer("type", new org.apache.lucene.analysis.WhitespaceAnalyzer(Version.LUCENE_33))
 
    val parser = new QueryParser(Version.LUCENE_33,"label", aWrapper)
    try{
    	var queryLucen = rdfType match {
    	  case Some(typeResource) => val booleanQuery = new BooleanQuery()
    	                             booleanQuery.add(parser.parse(q),Occur.MUST)
    	                             booleanQuery.add(new TermQuery(new Term("type", typeResource.uri)),Occur.MUST)
    	                             booleanQuery
    	  case None => parser.parse(q)
    	}    	
	    val hits  = isearcher.search(queryLucen,10).scoreDocs
	    lazy val firstResult = Some(Resource(isearcher.doc(hits(0).doc).get("resource")))
	    return hits.length  match{
	      case 0 => None
	      case 1 => firstResult
	      case _ => strategy match{
	      			case "first" => firstResult
	      			case "single" => None
	      			case "very-best" => logger.info("Results very-best: " + isearcher.doc(hits(1).doc).get("resource") +" - "+ isearcher.doc(hits(1).doc).get("resource"))
	      			  					if (hits(0).score/hits(1).score>8/5.5) 
	      									firstResult
	      								else None
	      			case _ => throw new InvalidFucntionParameterException(strategy)
	      			
	      }
	      	
    	}
   
	}
	catch{ 
		case e : org.apache.lucene.queryParser.ParseException =>
					 logger.error ("Parsing lucene query: " + q , e)
		return None
    }
    finally{
       isearcher.close()
    }
  }

}