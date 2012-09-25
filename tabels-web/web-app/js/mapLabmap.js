function  mapLabMap_initializateMap(mapLabMapComponent){

   var initialMapCenter = new google.maps.LatLng(0,0);
   var gmapsOptions = {
      zoom: mapLabMapComponent.data("settings").mapZoom,
      center: initialMapCenter, // TODO change for settings value
      mapTypeId: mapLabMapComponent.data("settings").mapType,
    };

    var map = new google.maps.Map(document.getElementById("mapGoogle"), gmapsOptions);
    
	mapLabMapComponent.data("mapLabGoogleMap", map);
}

function mapLabMap_initializateData(mapLabMapComponent){
	var settings =  mapLabMapComponent.data("settings");
	var selKeys = $("#"+settings.treeRef).data("selKeys")
	console.log(selKeys)
	$.ajax({
		 async: false,
		 dataType: 'text',
		 //TODO Change for ws url
		 url: mapLabMapComponent.data("settings").mapAreaWs, //'ws/map2.json',
		 data: {
			 'types': selKeys,
			 'endpoint': settings.endpoint,
			 'namedgraph': settings.namedgraph
		 },
		 success: function(data) {
			 mapLabMapComponent.data("requestData", data);
		 }
	});
}

function mapLabMap_paintMap(mapLabMapComponent){

	var data = eval(mapLabMapComponent.data("requestData"));
	var polygons = new Array();
	var polylines = new Array();
	var makers = new Array();	
	var map = mapLabMapComponent.data("mapLabGoogleMap");
	
	for (var i=0; i<data[0].length; i++ ){
		polygons.push(eval(data[0][i]));
		polygons[i].setMap(map); 
	} 
	
	for (var i=0; i<data[1].length; i++ ){
		polylines.push(eval(data[1][i]));
		polylines[i].setMap(map); 
	} 
	
	for (var i=0; i<data[2].length; i++ ){
		makers.push(eval(data[2][i]));
		makers[i].setMap(map); 
	} 
	
	mapLabMapComponent.data("mapLabMapPolygons", polygons);
	mapLabMapComponent.data("mapLabMapPolylines", polylines);
	mapLabMapComponent.data("mapLabMapMakers", makers);
	mapLabMap_centerMap(mapLabMapComponent);  
}

function mapLabMap_centerMap(mapLabMapComponent){
	var polygons = mapLabMapComponent.data("mapLabMapPolygons");
	var polylines = mapLabMapComponent.data("mapLabMapPolylines");
	var makers = mapLabMapComponent.data("mapLabMapMakers");
	var map = mapLabMapComponent.data("mapLabGoogleMap");

	var mayorLat = 0;
	var menorLat = 0;
	var mayorLng = 0;
	var menorLng = 0;
	var primer = true;

	$(polygons).each(function(i,fpolygon){ 
	  if (fpolygon != null){
	    $(fpolygon).each(function(j, polygon){
	    	if (polygon != null){
				var auxlng = 0;
				var auxlat = 0;
			  	polygon.getPath().forEach(function (element, index){
					
			  	 if (primer && index == 0 ){
			  		primer = false;
			  		mayorLat = element.lat();
			  		menorLat = element.lat();
			  		mayorLng = element.lng();
			  		menorLng = element.lng();
			  	 }else{
			  		auxlat = element.lat();
			  		auxlng = element.lng();
			  		if (auxlat < menorLat) menorLat = auxlat;
			  		if (auxlat > mayorLat) mayorLat = auxlat;
			  		if (auxlng < menorLng) menorLng = auxlng;
			  		if (auxlng > mayorLng) mayorLng = auxlng;
			  	 }
			  });
	    	}
	    });
	}
	});
	
	$(polylines).each(function(i,fpolyline){ 
	  if (fpolyline != null){
	    $(fpolyline).each(function(j, polyline){
	    	if (polyline != null){
				var auxlng = 0;
				var auxlat = 0;
			  	polyline.getPath().forEach(function (element, index){
					
			  	 if (primer && index == 0 ){
			  		primer = false;
			  		mayorLat = element.lat();
			  		menorLat = element.lat();
			  		mayorLng = element.lng();
			  		menorLng = element.lng();
			  	 }else{
			  		auxlat = element.lat();
			  		auxlng = element.lng();
			  		if (auxlat < menorLat) menorLat = auxlat;
			  		if (auxlat > mayorLat) mayorLat = auxlat;
			  		if (auxlng < menorLng) menorLng = auxlng;
			  		if (auxlng > mayorLng) mayorLng = auxlng;
			  	 }
			  });
	    	}
	    });
	}
	});
	
	$(makers).each(function(i,maker){ 
	  if (maker != null){
		var element = maker.getPosition();
			
	  	 if (primer){
	  		primer = false;
	  		mayorLat = element.lat();
	  		menorLat = element.lat();
	  		mayorLng = element.lng();
	  		menorLng = element.lng();
	  	 }else{
	  		if (element.lat() < menorLat) menorLat = element.lat();
	  		if (element.lat() > mayorLat) mayorLat = element.lat();
	  		if (element.lng() < menorLng) menorLng = element.lng();
	  		if (element.lng() > mayorLng) mayorLng = element.lng();
	  	 }
	}
	});

	var nw = new google.maps.LatLng(mayorLat, menorLng);
	var se = new google.maps.LatLng(menorLat, mayorLng);
	var bounds = new google.maps.LatLngBounds(new google.maps.LatLng(mayorLat, menorLng), new google.maps.LatLng(menorLat, mayorLng));
	  
	map.setCenter(bounds.getCenter());
	map.fitBounds(bounds);
}

function mapLabMap_emptyData(mapLabMapComponent){
	var settings =  mapLabMapComponent.data("settings");
	var selKeys = $("#"+settings.treeRef).data("selKeys")
	if (selKeys.length == 0){
		return true;
	}
	return false;
}

function mapLabMap_setDefaultSettings(settings) { 
    // set default values for settings
	if(settings.mapAreaWs == undefined) {
		settings.mapAreaWs = "ws/mapArea";
	}
	
	if(settings.mapZoom == undefined) {
		settings.mapZoom = 0;
	}

	if(settings.mapType == undefined) {
		settings.mapType = google.maps.MapTypeId.ROADMAP;
	}
	
	if(settings.treeRef == undefined){
		settings.treeRef = "tree";
	}
	if(settings.namedgraph == undefined){
		settings.namedgraph = "";
	}
}

function mapLabReload(mapLabTreeComponent, mapLabMapComponent){
	
	var selKeys = mapLabTreeComponent.data("selKeys");
	//TODO url to ws whit selected key
	//and chage de settings ws?
	
	mapLabMap_initializateMap(mapLabMapComponent); 
	
	if (selKeys.length != 0){
	    mapLabMap_initializateData(mapLabMapComponent);
		mapLabMap_paintMap(mapLabMapComponent); 
	}
	
	//mapLabTree_paintLegend(mapLabTreeComponent);
}

// ********************************************************
// JQuery component
// ********************************************************
(function($) {
    // initialization function
   $.fn.mapLabMap = function(settings) {
	this.each(function() {
		
    	 mapLabMap_setDefaultSettings(settings);
         // save settings into the DOM tree
         $(this).data("settings", settings);
		 
        mapLabMap_initializateMap($(this)); 
        
        //if map must paint something or not
        if(!mapLabMap_emptyData($(this))){
        	mapLabMap_initializateData($(this));
        	mapLabMap_paintMap($(this)); 
        }         
	});
    return this;
   };
 })(jQuery);