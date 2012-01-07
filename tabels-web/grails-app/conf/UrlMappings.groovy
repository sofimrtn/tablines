class UrlMappings {

	static mappings = {
		"/$controller/$action?/$id?"{
			constraints {
				// apply constraints here
			}
		}

        "/ws/chart"(uri:"/ws/chart.dispatch") 

		"/"(view:"/index")
		"500"(view:'/error')
	}
}
