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
import java.io.{File,FileNotFoundException,FileInputStream,FileOutputStream}
import java.net.URL
import org.apache.commons.compress.compressors.bzip2.BZip2CompressorInputStream
import org.apache.commons.io.FileUtils
import scala.io.Source
import com.hp.hpl.jena.rdf.model.{ModelFactory,Model}
import com.hp.hpl.jena.vocabulary.RDFS
import com.hp.hpl.jena.vocabulary.RDF
import com.hp.hpl.jena.tdb.TDBFactory
import scala.collection.mutable.ListBuffer
import collection.JavaConversions._

class Lucene extends Logging{
  
  var analyzer = new org.apache.lucene.analysis.es.SpanishAnalyzer(Version.LUCENE_33)
  val dumpsDir = new File(Config.tabelsDir, "dbpedia-dumps")
  val indexDir = new File(Config.tabelsDir, "dbpedia-index")
  val modelDir = new File(Config.tabelsDir, "dbpedia-model")
  
  logger.info("Checking index directory " + indexDir)
  FileUtils.forceMkdir(indexDir)
  lazy val directory = FSDirectory.open(indexDir)
  if (indexDir.list().length == 0) {
      logger.info("The index directory " + indexDir + " is empty, regenerating index")
      val iWriterConfig = new IndexWriterConfig(Version.LUCENE_33, new LimitTokenCountAnalyzer(analyzer,2500))
      iWriterConfig.setOpenMode(OpenMode.CREATE)
      val iwriter = new IndexWriter(directory, iWriterConfig)
      try{ 
         loadDocs(iwriter)
         }
        finally {
         iwriter.close()
       } 
  }
  else {
      logger.info("Skipping index generation, reusing indexes from " + indexDir)
  }
  
    def loadDocs(iwriter: IndexWriter) {
       
    //val model = ModelFactory.createDefaultModel()
    logger.info("Creating temporary Jena model in dir " + modelDir)
    FileUtils.forceMkdir(modelDir)
    val model = TDBFactory.createModel(modelDir.getAbsolutePath)
    loadIntoModel("en/labels_en.nt", model)
    loadIntoModel("en/instance_types_en.nt", model)
    loadIntoModel("en/redirects_en.nt", model)
    logger.info("The model size is " + model.size() + " triples")
    val iterator = model.listStatements(null, RDFS.label ,null)
    
    val wikiPageRedirects = model.getProperty("http://dbpedia.org/ontology/wikiPageRedirects")
    while(iterator.hasNext){
      val statement = iterator.nextStatement()
      if (model.contains(statement.getSubject, wikiPageRedirects, null)) {
          logger.debug("Skipping " + statement.getSubject.getURI + " because it is a redirect")
      } else {
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
    
    // FIXME: delete Jena model
      
  }
  
  def loadIntoModel(filename : String, model : Model) {
      logger.debug("Ensuring directory " + dumpsDir + " exists")
      FileUtils.forceMkdir(dumpsDir)
      val file = new File(dumpsDir, filename.replace("/", "-"))
      logger.debug("Checking if file " + file + " already exists")
      if (!file.exists()) {
          logger.info("Downloading and unpacking " + file + " from DBPedia")
          // code snippet from http://stackoverflow.com/questions/2322944/uncompress-bzip2-archive
          val in = new URL("http://downloads.dbpedia.org/3.7/" + filename + ".bz2").openStream();
          val out = new FileOutputStream(file)
          val bzIn = new BZip2CompressorInputStream(in)
          val buffersize = 1024 * 1024
          val buffer = new Array[Byte](buffersize)
          var n = bzIn.read(buffer, 0, buffersize);
         
          while (n != -1) {
            out.write(buffer, 0, n);
            System.out.print(".") // DEBUG
            n = bzIn.read(buffer, 0, buffersize)
          }
          out.close();
          bzIn.close();
          logger.info("Finished downloading and unpacking " + file)
      } else {
          logger.info("File " + file + " already exists")
      }
      logger.info("Loading file " + file + " into the RDF model")
      model.read(new FileInputStream(file), null, "N-TRIPLE")
      // FIXME: delete downloaded dump file
  }
  
 
    def query(ec:EvaluationContext ,q : String ,strategy:String = "first", rdfType:Option[NamedResource] = None) : NamedResource  ={
   
    // Now search the index:
    val isearcher = new IndexSearcher(directory, true) // read-only=true
    // Parse a simple query that searches for "text":
    var buffList = new ListBuffer[NamedResource]
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
	    
	    lazy val firstResult = NamedResource(isearcher.doc(hits(0).doc).get("resource"))
	    val resourceNotDisambiguated = NamedResource("http://example.org/ResourceNotDisambiguated?query="+q.toString)
	    
	    lazy val auxSeqResource = new ListBuffer[NamedResource]
         hits.foreach{hit =>auxSeqResource+=NamedResource(isearcher.doc(hit.doc).get("resource"))}
	    lazy val infoDisambiguation =  new ResourceUnDisambiguated(q.toString, hits.length, auxSeqResource,"DBPedia",strategy)
	    
	    return hits.length  match{
	      case 0 => resourceNotDisambiguated
	      case 1 => firstResult
	      case _ => strategy match{
	      			case "first" => firstResult
	      			case "single" =>
	      			  //FIXME: Add info for undisambiguated resource to workarea
	      			  				//ec.workingArea.mapUnDisambiguted.put(resourceNotDisambiguated,infoDisambiguation) 
	      			  				resourceNotDisambiguated
	      			case "very-best" => logger.info("Results very-best: " + isearcher.doc(hits(1).doc).get("resource") +" - "+ isearcher.doc(hits(1).doc).get("resource"))
	      			  					if (hits(0).score/hits(1).score>8/5.5) 
	      									firstResult
	      								else{ 
	      						//FIXME: Add info for undisambiguated resource to workarea
	      									//ec.workingArea.mapUnDisambiguted.put(resourceNotDisambiguated,infoDisambiguation) 
	      									 resourceNotDisambiguated
	      								}
	      			case _ => throw new InvalidFucntionParameterException(strategy)
	      			
	      }
	      	
    	}
   
	}
	catch{ 
		case e : org.apache.lucene.queryParser.ParseException =>
					 //logger.error ("Parsing lucene query: " + q , e)
					 throw new LuceneQueryException(q.toString())
		return NamedResource("http://example.org/ResourceNotDisambiguated?query="+q.toString)
    }
    finally{
       isearcher.close()
    }
  }

}