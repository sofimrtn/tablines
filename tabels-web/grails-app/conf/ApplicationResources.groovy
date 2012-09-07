modules = {
    application {
        resource url:'js/application.js'
	}
	codemirror {
	    dependsOn 'jquery'
		resource url:'js/codemirror/codemirror.js'
		resource url:'js/codemirror/sparql.js'
		resource url:'js/codemirror/tabels.js'
		resource url:'js/codemirror/javahint.js'
		resource url:'js/codemirror/simplehint.js'
	}
	trace {
	   resource url:'http://ajax.googleapis.com/ajax/libs/jqueryui/1.8/themes/south-street/jquery-ui.css'
	   resource id:'style-theme-link', url:'http://imakewebthings.com/deck.js/themes/style/web-2.0.css', disposition: 'head'
	   resource id:'transition-theme-link', url:'http://imakewebthings.com/deck.js/themes/transition/horizontal-slide.css', disposition: 'head'
       resource url:'https://ajax.googleapis.com/ajax/libs/jquery/1.7.2/jquery.min.js'
       resource url:'http://imakewebthings.com/deck.js/modernizr.custom.js'
       resource url:'http://imakewebthings.com/deck.js/core/deck.core.js'
       resource url:'http://imakewebthings.com/deck.js/core/deck.core.js'
       resource url:'https://ajax.googleapis.com/ajax/libs/jqueryui/1.8.18/jquery-ui.min.js'
       resource url:'http://imakewebthings.com/deck.js/extensions/status/deck.status.js'
       resource url:'http://imakewebthings.com/deck.js/extensions/navigation/deck.navigation.js'
    }
}