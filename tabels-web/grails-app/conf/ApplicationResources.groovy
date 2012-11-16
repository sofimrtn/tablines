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
	
	googlejsapi {
        resource url: 'https://www.google.com/jsapi', attrs: [type: 'js']
	}

	googlemaps {
		// HACK until new version of resource grails plugin
		// DOC http://jira.grails.org/browse/GPRESOURCES-131
		// COMMIT https://github.com/grails-plugins/grails-resources/commit/7e8781eb5f13dc0b7f6abbbd65645d7f9ed749aa
		resource url: 'http://maps.googleapis.com/maps/api/js', attrs: [type: 'js'], wrapper: { link -> org.apache.commons.lang.StringUtils.replaceOnce(link, '/js', '/js?sensor=false') }
        resource url:'js/tapinos/jquery.undermaps.gmaps.js'	
        resource url:'css/undermaps.css'			
	}
	
	tree{
		dependsOn 'dynatree'
        resource url:'js/tapinos/jquery.undermaps.tree.js'
	}
	
	maps {
		dependsOn 'jquery'
		dependsOn 'infobubble'
		dependsOn 'googlemaps'
		dependsOn 'tree'
	}
	
	infobubble{
		resource url:'http://google-maps-utility-library-v3.googlecode.com/svn/trunk/infobubble/src/infobubble.js'
		resource url:'css/infobubble.custom.css'
	}

    'tapinos-js' {
        dependsOn 'jquery,fancybox,protovis,jquery-ui,jquery-tipsy,jquery-datatables,jquery-geturlparam,jquery-tooltip,googlejsapi'
//        defaultBundle 'ui'
        resource url:'js/tapinos/jquery.tapinosCommon.js'
        resource url:'js/tapinos/jquery.tapinosChart.js'
        resource url:'js/tapinos/jquery.tapinosCombos.js'
        resource url:'js/tapinos/jquery.tapinosPermLinkBuilder.js'
        resource url:'js/tapinos/jquery.tapinosDataExport.js'
        resource url:'js/tapinos/jquery.tapinosSelectSearch.js'
        resource url:'js/tapinos/jquery.tapinosTable.js'
        resource url:'css/tapinos.css'
        resource url:'css/undermaps.css'
    }
    'contactable'{
    	 dependsOn 'jquery'
    	 resource url:'js/jquery.contactable.min.js'
    }

}