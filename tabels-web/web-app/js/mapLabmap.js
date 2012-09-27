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

function mapLabMap_showInfo(typeGeo, i, location, mapLabMapComponent){

	var infoMap = mapLabMapComponent.data("mapLabMapInfoMap");
	var infowindow = mapLabMapComponent.data("mapLabMapInfoWindow");
	var map = mapLabMapComponent.data("mapLabGoogleMap");
	var location ="";
	
	switch (typeGeo){
		case 0: var arrayPolygons = mapLabMapComponent.data("mapLabMapPolygons");
				var polygon = arrayPolygons[i];
				location = mapLabMap_centerElement(polygon);
				break;
				
		case 1: var arrayPolyline = mapLabMapComponent.data("mapLabMapPolylines");
			    var polyline = arrayPolyline[i];
			    location = mapLabMap_centerElement(polyline);	
				break;
		case 2: var arrayMaker = mapLabMapComponent.data("mapLabMapMakers");
				var maker = arrayMaker[i];
				location = maker.getPosition();
				location = new google.maps.LatLng(location.lat()+0.01, location.lng()+0.01);
				break;
	}

	infowindow.setContent(infoMap[typeGeo][i]);
	infowindow.setPosition(location);
	//infowindow.open(map); // TODO change after demo

	var infoBubble = mapLabMapComponent.data("mapLabMapInfoBubble");
	infoBubble.setContent(infoMap[typeGeo][i]);
	infoBubble.setPosition(location);
	infoBubble.open(map);
}

function mapLabMap_paintMap(mapLabMapComponent){

	var infoMap = new Array();
	
	var data = eval(mapLabMapComponent.data("requestData"));
	var polygons = new Array();
	var polylines = new Array();
	var makers = new Array();	
	var map = mapLabMapComponent.data("mapLabGoogleMap");
	
	infoMap[0] = new Array();
	$(data[0]).each(function(i,polygon){ 
		polygons.push(eval(polygon));
		polygons[i].setMap(map); 
		infoMap[0].push("<div class=\"phoneytext\">"+polygons[i].html+"</div>");
		console.log(data[0][i].html);
		google.maps.event.addListener(polygons[i], "click", function(){
				mapLabMap_showInfo(0,i, event.latLng, mapLabMapComponent);
		});
	}); 
	
	infoMap[1] = new Array();
	$(data[1]).each(function(i,polyline){ 
		polylines.push(eval(polyline));
		polylines[i].setMap(map);
		infoMap[1][i] = "<div class=\"phoneytext\">"+polylines[i].html+"</div>"; 
		google.maps.event.addListener(polylines[i], "click", function(){
				mapLabMap_showInfo(1,i, event.latLng, mapLabMapComponent);
		});
	} );
	
	infoMap[2] = new Array();
	$(data[2]).each(function(i,maker){ 
		makers.push(eval(maker));
		makers[i].setMap(map); 
		infoMap[2].push("<div class=\"phoneytext\">"+makers[i].html+"</div>");
				
		google.maps.event.addListener(makers[i], "click", function(){
				mapLabMap_showInfo(2,i, event.latLng, mapLabMapComponent);
		});
	});
	
	var infowindow = new google.maps.InfoWindow({
		disableAutoPan: false,
	});
	
    var infoBubble = new InfoBubble({
        position: new google.maps.LatLng(-35, 151),
        shadowStyle: 1,
        padding: 0,
        backgroundColor: 'rgb(57,57,57)',
        borderRadius: 4,
        arrowSize: 10,
        borderWidth: 1,
        borderColor: '#2c2c2c',
        hideCloseButton: true,
        arrowPosition: 30,
        backgroundClassName: 'phoney',
        arrowStyle: 0
      });
	
	mapLabMapComponent.data("mapLabMapInfoBubble", infoBubble);

	mapLabMapComponent.data("mapLabMapInfoWindow", infowindow);
	
	mapLabMapComponent.data("mapLabMapPolygons", polygons);
	mapLabMapComponent.data("mapLabMapPolylines", polylines);
	mapLabMapComponent.data("mapLabMapMakers", makers);
	mapLabMapComponent.data("mapLabMapInfoMap", infoMap);
	
	mapLabMap_centerMap(mapLabMapComponent);  
}


function mapLabMap_centerElement(geoElement){
	var bounds = new google.maps.LatLngBounds();
	
	geoElement.getPath().forEach(function (element, index){
		bounds.extend(element);
	});
	return bounds.getCenter();
}

function mapLabMap_centerMap(mapLabMapComponent){
	var polygons = mapLabMapComponent.data("mapLabMapPolygons");
	var polylines = mapLabMapComponent.data("mapLabMapPolylines");
	var makers = mapLabMapComponent.data("mapLabMapMakers");
	var map = mapLabMapComponent.data("mapLabGoogleMap");

	var bounds = new google.maps.LatLngBounds();

	$(polygons).each(function(i,fpolygon){ 
	  if (fpolygon != null){
	    $(fpolygon).each(function(j, polygon){
	    	if (polygon != null){
				var auxlng = 0;
				var auxlat = 0;
			  	polygon.getPath().forEach(function (element, index){
					bounds.extend(element);
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
			  	bounds.extend(element);
			  });
	    	}
	    });
	}
	});
	
	$(makers).each(function(i,maker){ 
	  if (maker != null){
		var element = maker.getPosition();	
		bounds.extend(element);	
	  }
	});

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