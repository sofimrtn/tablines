function underMapsLayers(underMapsLayersComponent){
	if (underMapsLayersComponent.css('display')=="none" || underMapsLayersComponent.css('opacity')==0){
		underMapsLayersComponent.slideToggle("slow");
	}else{
		underMapsLayersComponent.slideToggle("slow");
	}
}

function  underMapsTree_initializateTree(underMapsTreeComponent){
	var settings=underMapsTreeComponent.data("settings");
	underMapsTreeComponent.dynatree({
		checkbox: true,
		// Override class name for checkbox icon, so radio buttons are displayed:
		//classNames: {checkbox: "dynatree-radio"},
		// Select mode 3: multi-hier
		selectMode: 3,
		initAjax: {
			url: settings.treeWs,//"ws/tree.json",
			data: {
				key: "root", // Optional arguments to append to the url
			  	mode: "all",
			  	endpoint: settings.endpoint,
			  	namedgraph: settings.namedgraph
			}
	    },
		
		// The following options are only required, if we have more than one tree on one page:
		// initId: "treeData",
		cookieId: "dynatree-Cb3",
		idPrefix: "dynatree-Cb3-",
		onSelect: function(select, node) {
			// Get a list of all selected nodes, and convert to a key array:
			var selKeys = $.map(node.tree.getSelectedNodes(), function(node){
				return node.data.key;
			});
			
			//$("#echoSelection").text(selKeys.join(", "));
			
			underMapsTreeComponent.data("selKeys", selKeys);
			//selKeys contains all selected keys 
			//alert (selKeys);			
		},
		strings: {
        	loading: "Loading…",
       		loadError: "No geospatial data found"
    	}
	
	});
	
	var selKeys = new Array();
	underMapsTreeComponent.data("selKeys", selKeys);
}

function underMapsTree_paintLegend (underMapsTreeComponent){
	var textLegend = "";	
	var selKeys = underMapsTreeComponent.data("selKeys");
	var settings = underMapsTreeComponent.data("settings");
	var map = $("#" + settings.mapRef);
	var legend = $("#" + setting.legendRef);

	if ($("div#legend").find("ul")!= null) {
		$("div#legend").find("ul").empty();
	}
	
	/*if ($("div#legend").find("p")!= null) {
		$("div#legend").find("p").empty();
	}*/
		
	if (selKeys.length == 0){
		textLegend = textLegend + "<ul><li>Nothing selected</li></ul>";
	}else{		
		textLegend = textLegend + "<ul class='legendlist'>";
	
		var data = map.data("requestData");
		
		
		for (var i=0; i < selKeys.length; i++){		
			var aux = eval ("data.series["+i+"]." + selKeys[i] );
			textLegend = textLegend + "<li>";
			textLegend = textLegend + "<img src='icons/"+ aux +"'/>";	
			textLegend = textLegend + "</li>";
		}
			
		textLegend = textLegend + "</ul>";
	}
	
	legend.append(textLegend);

}

function underMapsTree_setDefaultSettings(settings){
	if(settings.legendRef == undefined){
		settings.legendRef = "legend";
	}
	if(settings.mapRef == undefined){
		settings.mapRef = "mapGoogle";
	}
	if(settings.namedgraph == undefined){
		settings.namedgraph = "";
	}
}

// ********************************************************
// JQuery component
// ********************************************************	

(function($) {
// initialization function
   $.fn.underMapsTree = function(settings) {

	   $.ui.dynatree.nodedatadefaults["icon"] = false; // Turn off icons by default	
   			
    	underMapsTree_setDefaultSettings(settings);
    	// save settings into the DOM tree
        $(this).data("settings", settings);
         
	   	this.each(function() {
	   		underMapsTree_initializateTree($(this));
		});
	}
})(jQuery);