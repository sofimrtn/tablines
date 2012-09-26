class UrlMappings {

	static mappings = {
		"/"(controller: "project", action: "list")
		"/createProject"(controller: "project", action: "create")
		name globalSparql: "/sparql"(controller: "project", action: "sparql")
		name globalTapinos: "/tapinos"(controller: "project", action: "tapinos")
		"/project/$id/input/$filename?"(controller: "project") {
		    action = [GET: "listInputs", POST: "uploadInput", DELETE: "deleteInput"]
		}
		
		name newMap: "/map"(controller: "project", action: "map")
		"/project/$id/kml/$filename"(controller: "project", action: "kml")
		"/project/$id/json/$filename"(controller: "project", action: "json")
		name projectSpecific: "/project/$id/$action?"(controller: "project")
		"/project/$id/resource/$localName**"(controller: "project", action: "resourceRedirect")
		"/project/$id/data/$localName**"(controller: "project", action: "resourceData")
		"/project/$id/page/$localName**"(controller: "project", action: "resourcePage")
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

		"500"(view:'/error')
	}
}
