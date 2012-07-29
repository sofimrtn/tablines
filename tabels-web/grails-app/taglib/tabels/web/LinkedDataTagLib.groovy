package tabels.web

import com.hp.hpl.jena.rdf.model.Resource
import com.hp.hpl.jena.rdf.model.Literal
import com.hp.hpl.jena.vocabulary.RDF
import com.hp.hpl.jena.vocabulary.RDFS
import com.hp.hpl.jena.vocabulary.OWL

class LinkedDataTagLib {
    
    def showRdfNode = { args, body ->
		switch(args.rdfNode) {
			case Resource:
				def resource = args.rdfNode as Resource
				if (resource.getURI() == null) {
					out << "Blank node (${resource.toString()})"
				} else {
	        		out << "<a href='${resource.getURI()}'>${shortUri(resource)}</a>"
				}
				break
			case Literal:
				def literal = args.rdfNode as Literal
				out << literal.toString()
				break
		}
    }

	def wellKnownPrefixes = [
		"rdf": RDF.getURI(),
		"owl": OWL.getURI(),
		"rdfs": RDFS.getURI(),
		"dcat": "http://www.w3.org/ns/dcat#",
		"dct": "http://purl.org/dc/terms/",
		"dc": "http://purl.org/dc/elements/1.1/",
		"foaf": "http://xmlns.com/foaf/0.1/",
		"sioc": "http://rdfs.org/sioc/ns#"
	]

	String shortUri(Resource resource) {
		if (resource.getURI().equals(RDF.type.getURI())) {
			return "a"
		} else {
			def matchedPrefix = wellKnownPrefixes.find { resource.getURI().startsWith(it.value) }
			if (matchedPrefix) {
				return "${matchedPrefix.key}:${resource.toString().substring(matchedPrefix.value.length())}"
			} else {
				return resource.toString()
			}
		}
	}

}
