class UrlMappings {

	static mappings = {
		"/$controller/$action?/$id?"{
			constraints {
				// apply constraints here
			}
		}

        "/ws/chart"(uri:"/ws/chart.dispatch") 
        "/ws/dimensions"(uri:"/ws/dimensions.dispatch") 

		"/"(view:"/index")
		"500"(view:'/error')
	}
}
