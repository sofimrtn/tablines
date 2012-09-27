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
	dynatree {
		dependsOn 'jquery'
		dependsOn 'jquery-ui'
		resource url:'http://wwwendt.de/tech/dynatree/src/skin/ui.dynatree.css'
		resource url:'http://wwwendt.de/tech/dynatree/src/jquery.dynatree.js'
		resource url:'http://wwwendt.de/tech/dynatree/jquery/jquery.cookie.js'
		resource url:'css/dynatree.custom.css'
	}
	maplab {
		dependsOn 'jquery'
		resource url:'js/mapLabmap.js'
		resource url:'js/mapLabTree.js'
		resource url:'css/maplab.css'
	}
	googlemaps {
		resource url:'http://maps.google.com/maps/api/js?sensor=true', attrs:[type:'js']
	}
	
	infobubble{
		resource url:'http://google-maps-utility-library-v3.googlecode.com/svn/trunk/infobubble/src/infobubble.js'
		resource url:'css/infobubble.custom.css'
	}
}