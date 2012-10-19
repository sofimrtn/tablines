class UrlMappings {

	static mappings = {
		"/"(controller: "project", action: "home")
		"/list"(controller: "project", action: "list")
		"/team"(controller: "project", action: "team")
		"/disclaimer"(controller: "project", action: "disclaimer")
		"/faq"(controller: "project", action: "faq")
		"/createProject"(controller: "project", action: "create")
		"/clearCacheMap"(controller: "project", action: "clearCacheMap")
		name globalSparql: "/sparql"(controller: "project", action: "sparql")
		name globalTapinos: "/tapinos"(controller: "project", action: "tapinos")
		"/project/$id/input/$filename?"(controller: "project") {
		    action = [GET: "listInputs", POST: "uploadInput", DELETE: "deleteInput"]
		}
		
		name newMap: "/map"(controller: "project", action: "map")
		name projectSpecific: "/project/$id/$action?"(controller: "project")
		"/project/$id/resource/$localName**"(controller: "project", action: "resourceRedirect")
		"/project/$id/data/$localName**"(controller: "project", action: "resourceData")
		"/project/$id/page/$localName**"(controller: "project", action: "resourcePage")
		
		"/project/$id/$filetype/$filename"{
		   controller="project"
		   action="file"
		   constraints{
		      filetype(matches:/kml|json|svg/)
		   }
		 }
		
/*		"/$controller/$action?/$id?"{
			constraints {
				// apply constraints here
			}
		}*/
		
		"/upload/$action/$id?"(controller: "upload")

        "/ws/chart"(uri:"/tapinos/chart.dispatch")
        "/ws/dimensions"(uri:"/tapinos/dimensions.dispatch")
        "/ws/mapArea"(uri:"/tapinos/mapArea.dispatch")
        "/ws/sanityChecker"(uri:"/tapinos/sanityChecker.dispatch")
		
		"/ws/mapArea"(uri:"/maplab/map.dispatch")
		"/ws/tree"(uri:"/maplab/tree.dispatch")
		"/sendFeedback"(controller: "project", action: "sendFeedback")

		"500"(view:'/error')
	}
}
