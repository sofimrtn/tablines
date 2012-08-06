package es.ctic.tabels

import scala.util.matching.Regex
import grizzled.slf4j.Logging
import com.hp.hpl.jena.rdf.model.{Model,ModelFactory,AnonId}
import com.hp.hpl.jena.datatypes.xsd.XSDDatatype
import com.hp.hpl.jena.reasoner.rulesys.{GenericRuleReasoner,Rule}
import com.hp.hpl.jena.query.DatasetFactory
import com.hp.hpl.jena.update.{UpdateAction,GraphStoreFactory}
import java.io.{BufferedReader,StringReader}

class JenaDataOutput(prefixes : Map[String,NamedResource] = Map()) extends DataOutput with Logging {

  val model : Model = ModelFactory.createDefaultModel()
  prefixes.foreach { case (prefix,ns) => model.setNsPrefix(prefix, ns.uri) }
  
  override def generateOutput(statement: Statement){ 
   model.add(model.createStatement(createSubject(statement.subject),createProperty(statement.property),createObject(statement.obj)))
  }
  
  override def postProcess(program : S) {
	  program.directives.foreach(_ match {
		case FetchDirective(resourceUriRe) => fetchDescriptions(resourceUriRe)
		case JenaRuleDirective(jenaRule) => executeJenaRule(jenaRule)
		case LoadDirective(url) => fetchDescription(url)
		case SparqlDirective(sparqlQuery) => executeSparql(sparqlQuery)
	  })
  }
  
  def fetchDescriptions(resourceUriRe : Regex) {
      logger.info("Fetching descriptions of resources that match RE: " + resourceUriRe)
      var resourcesToFetch = Set[String]()
      val subjectIterator = model.listSubjects()
      while (subjectIterator.hasNext()) {
          val resource = subjectIterator.nextResource()
          if (resource.getURI() != null) {
              resourcesToFetch = resourcesToFetch + resource.getURI()
          }
      }
      val objectIterator = model.listObjects()
      while (objectIterator.hasNext()) {
          val resource = objectIterator.nextNode()
          if (resource.isResource() && resource.asResource().getURI() != null) {
              resourcesToFetch = resourcesToFetch + resource.asResource().getURI()
          }
      }
      resourcesToFetch = resourcesToFetch filter { uri => resourceUriRe.pattern.matcher(uri).find }
      logger.debug("The descriptions of the following resources will be retrieved from the web: " + resourcesToFetch)
      if (Config.proxyHost != null) {
          logger.info("Changing HTTP proxy host to " + Config.proxyHost)
          scala.sys.props.put("http.proxyHost", Config.proxyHost)
      }
      if (Config.proxyPort != null) {
          logger.info("Changing HTTP proxy port to " + Config.proxyPort)
          scala.sys.props.put("http.proxyPort", Config.proxyPort)
      }
      resourcesToFetch foreach { resourceUri =>
          fetchDescription(resourceUri)
      }
      // FIXME: restore system properties
  }
  
    def executeJenaRule(jenaRule : String) {
        def prefixDecls = prefixes map { case (prefix, namespace) => "@prefix %s: <%s>.\n".format(prefix, namespace.uri)} mkString "\n"
        def ruleWithPrefixes = prefixDecls + jenaRule
        logger.info("Parsing Jena Rules: " + ruleWithPrefixes)
        // the following line looks pretty complicated, but that's the way it is, see
        // http://tech.groups.yahoo.com/group/jena-dev/message/36663
        def parsedRules = Rule.parseRules(Rule.rulesParserFromReader(new BufferedReader(new StringReader(ruleWithPrefixes))))
        def reasoner = new GenericRuleReasoner(parsedRules)
        def inferredModel = ModelFactory.createInfModel(reasoner, model)
        model.add(inferredModel)
    }
    
    def executeSparql(sparqlQuery : String) {
        val dataSource = DatasetFactory.create()
        dataSource.setDefaultModel(model)
        val graphStore = GraphStoreFactory.create(dataSource)
        UpdateAction.parseExecute(sparqlQuery, graphStore)
    }
  
    def fetchDescription(resourceUri : String) {
        logger.info("Fetching description of resource " + resourceUri)
        var attempt = 1
        val maxRetries = 3
        var success = false
        while (!success) {  
            try {
                model.read(resourceUri)
                success = true
                if (attempt > 1) {
                    logger.info("Retrieved URI " + resourceUri + " after " + attempt + " failed attempts")
                }
            } catch {
                case e : Exception =>
                    logger.error("While fetching URI " + resourceUri + " (attempt=" + attempt + ")", e)
                    if (attempt == maxRetries) {
                        throw new ResourceCannotBeRetrievedException(resourceUri)
                    } else {
                        attempt = attempt + 1
                    }
            }
        }
    }
  
  def createSubject(s : RDFNode) : com.hp.hpl.jena.rdf.model.Resource = {
    s match {
    	case NamedResource(uri) => model.createResource(uri)
    	case bn : BlankNode => createBlankNode(bn)
    	case Literal(value, _, _) => throw new TemplateInstantiationException("Unable to convert literal "+value+ " to RDF resource in the subject of a triple" )
    						
    }
  }

  def createProperty(s : RDFNode) : com.hp.hpl.jena.rdf.model.Property = {
    s match {
    	case NamedResource(uri) => model.createProperty(uri) 
    	case BlankNode(_) => throw new TemplateInstantiationException("Unable to convert blank node to RDF named resource in the predicate of a triple")
    	case Literal(value, _, _) => throw new TemplateInstantiationException("Unable to convert literal "+value+ " to RDF resource in the predicate of a triple" )
    }
  }

  def createObject(s : RDFNode) : com.hp.hpl.jena.rdf.model.RDFNode = {
    s match {
    	case NamedResource(uri) => model.createResource(uri) 
    	case bn : BlankNode => createBlankNode(bn)
    	case Literal(value, XSD_STRING, "") => model.createLiteral(value.toString) // untyped
    	case Literal(value, XSD_STRING, langTag) => model.createLiteral(value.toString, langTag) // with language tag
    	case Literal(value, XSD_BOOLEAN, _) => model.createTypedLiteral(value, XSDDatatype.XSDboolean)
    	case Literal(value, XSD_INT, _) => model.createTypedLiteral(value, XSDDatatype.XSDint)
    	case Literal(value, XSD_DOUBLE, _) => model.createTypedLiteral(value, XSDDatatype.XSDdouble)
    	case Literal(value, XSD_FLOAT, _) => model.createTypedLiteral(value, XSDDatatype.XSDfloat)
    	case Literal(value, XSD_DECIMAL, _) => model.createTypedLiteral(value, XSDDatatype.XSDdecimal)
    	case Literal(value, XSD_DATE, _) => model.createTypedLiteral(value, XSDDatatype.XSDdate)
    }
  }
  
  def createBlankNode(blankNode : BlankNode) : com.hp.hpl.jena.rdf.model.Resource = model.createResource(new AnonId(blankNode.id match {
      case Left(internalId) => internalId
      case Right(n) => "_" + n
    }))

}