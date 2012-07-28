package tabels.web

import com.hp.hpl.jena.rdf.model.Resource
import com.hp.hpl.jena.rdf.model.Literal

class LinkedDataTagLib {
    
    def showRdfNode = { args, body ->
		switch(args.rdfNode) {
			case Resource:
	        	out << "<a href='${args.rdfNode.toString()}'>${args.rdfNode.toString()}</a>"
				break
			case Literal:
				out << args.rdfNode.toString()
				break
		}
    }

}
