class UrlMappings {

	static mappings = {
		"/"(controller: "project", action: "list")
		"/project/create"(controller: "project", action: "create")
		"/project/$id/$action?"(controller: "project")
/*		"/$controller/$action?/$id?"{
			constraints {
				// apply constraints here
			}
		}*/

        "/ws/chart"(uri:"/ws/chart.dispatch") 
        "/ws/dimensions"(uri:"/ws/dimensions.dispatch") 

		"500"(view:'/error')
	}
}
