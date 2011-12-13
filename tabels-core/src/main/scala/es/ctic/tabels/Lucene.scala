package es.ctic.tabels
import org.apache.lucene.analysis.standard.StandardAnalyzer
import org.apache.lucene.analysis.LimitTokenCountAnalyzer
import org.apache.lucene.document.Document
import org.apache.lucene.document.Field
import org.apache.lucene.index.IndexWriter
import org.apache.lucene.index.IndexWriterConfig
//import org.apache.lucene.queryParser.ParseException;
import org.apache.lucene.queryParser.QueryParser
import org.apache.lucene.search.IndexSearcher
import org.apache.lucene.search.Query
import org.apache.lucene.search.ScoreDoc
import org.apache.lucene.search.TopScoreDocCollector
import org.apache.lucene.store.Directory
import org.apache.lucene.store.RAMDirectory
import org.apache.lucene.store.FSDirectory
import org.apache.lucene.util.Version
import grizzled.slf4j.Logging
import java.io.{File,FileNotFoundException}
import scala.io.Source
import com.hp.hpl.jena.rdf.model.ModelFactory 
import com.hp.hpl.jena.vocabulary.RDFS
import scala.collection.mutable.ListBuffer

class Lucene extends Logging{
  
  var analyzer = new org.apache.lucene.analysis.es.SpanishAnalyzer(Version.LUCENE_33)
  val directory = FSDirectory.open(new File("/tmp/testindex"))
  val iWriterConfig = new IndexWriterConfig(Version.LUCENE_33, new LimitTokenCountAnalyzer(analyzer,2500))
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
    val iterator = model.listStatements(null, RDFS.label ,null)
    
    while(iterator.hasNext()){
      val statement = iterator.nextStatement()
      val doc = new Document()
      doc.add(new Field("resource", statement.getSubject.getURI, Field.Store.YES,Field.Index.NOT_ANALYZED));
      doc.add(new Field("label", statement.getString, Field.Store.YES,Field.Index.ANALYZED));
      iwriter.addDocument(doc)
    }
      
  }
  
  def query(q : String, workMode:String = "option", index:Int = 2) : Option[Seq[Resource]]  ={
   
    // Now search the index:
    val isearcher = new IndexSearcher(directory, true) // read-only=true
    // Parse a simple query that searches for "text":
    var buffList = new ListBuffer[Resource]
    val parser = new QueryParser(Version.LUCENE_33,"label", analyzer)
    try{
    	val queryLucen = parser.parse(q)
        
	    val hits  = isearcher.search(queryLucen,index).scoreDocs
	    return hits.length  match{
	      case 0 => None
	      case _ => hits.foreach(h =>buffList +=Resource(isearcher.doc(h.doc).get("resource")))
	      			workMode match{
	      			case "best" => Some(buffList.toList)
	      			case "option" => Some(Seq(buffList.remove(index-1))) 
	      			
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