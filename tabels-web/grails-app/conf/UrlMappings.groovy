class UrlMappings {

	static mappings = {
		"/"(controller: "project", action: "list")
		"/createProject"(controller: "project", action: "create")
		name globalSparql: "/sparql"(controller: "project", action: "sparql")
		name globalTapinos: "/tapinos"(controller: "project", action: "tapinos")
		"/project/$id/input/$filename?"(controller: "project") {
		    action = [GET: "listInputs", POST: "uploadInput", DELETE: "deleteInput"]
		}
		name projectSpecific: "/project/$id/$action?"(controller: "project")
		"/project/$id/resource/$localName"(controller: "project", action: "resourceRedirect")
		"/project/$id/data/$localName"(controller: "project", action: "resourceData")
		"/project/$id/page/$localName"(controller: "project", action: "resourcePage")
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

		"500"(view:'/error')
	}
}
