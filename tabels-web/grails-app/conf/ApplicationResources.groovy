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
	   dependsOn 'jquery'
	   dependsOn 'modernizr'
	   dependsOn 'jquery-ui'
	   resource url:'http://imakewebthings.com/deck.js/core/deck.core.js'
       resource url:'http://imakewebthings.com/deck.js/extensions/status/deck.status.js'
       resource url:'http://imakewebthings.com/deck.js/extensions/navigation/deck.navigation.js'
	   }
}