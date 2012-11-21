function  underMapsMap_initializateMap(underMapsMapComponent){

   var initialMapCenter = new google.maps.LatLng(0,0);
   var gmapsOptions = {
      zoom: underMapsMapComponent.data("settings").mapZoom,
      center: initialMapCenter, // TODO change for settings value
      mapTypeId: underMapsMapComponent.data("settings").mapType,
    };

    var map = new google.maps.Map(document.getElementById("mapGoogle"), gmapsOptions);
    
	underMapsMapComponent.data("underMapsGoogleMap", map);
}

function underMapsMap_initializateData(underMapsMapComponent){
	var settings =  underMapsMapComponent.data("settings");
	var selKeys = $("#"+settings.treeRef).data("selKeys")
	$.ajax({
		 async: false,
		 dataType: 'text',
		 //TODO Change for ws url
		 url: underMapsMapComponent.data("settings").mapAreaWs, //'ws/map2.json',
		 data: {
			 'types': selKeys,
			 'endpoint': settings.endpoint,
			 'namedgraph': settings.namedgraph
		 },
		 success: function(data) {
			 underMapsMapComponent.data("requestData", data);
		 },
	});
}

function underMapsMap_showInfo(typeGeo, i, location, underMapsMapComponent){

	var infoMap = underMapsMapComponent.data("underMapsMapInfoMap");
	var infowindow = underMapsMapComponent.data("underMapsMapInfoWindow");
	var map = underMapsMapComponent.data("underMapsGoogleMap");
	var location ="";
	
	switch (typeGeo){
		case 0: var arrayPolygons = underMapsMapComponent.data("underMapsMapPolygons");
				var polygon = arrayPolygons[i];
				location = underMapsMap_centerElement(polygon);
				break;
				
		case 1: var arrayPolyline = underMapsMapComponent.data("underMapsMapPolylines");
			    var polyline = arrayPolyline[i];
			    location = underMapsMap_centerElement(polyline);	
				break;
		case 2: var arrayMaker = underMapsMapComponent.data("underMapsMapMakers");
				var maker = arrayMaker[i];
				location = maker.getPosition();
				location = new google.maps.LatLng(location.lat()+0.01, location.lng()+0.01);
				break;
	}

	infowindow.setContent(infoMap[typeGeo][i]);
	infowindow.setPosition(location);
	//infowindow.open(map); // TODO change after demo

	var infoBubble = underMapsMapComponent.data("underMapsMapInfoBubble");
	infoBubble.setContent(infoMap[typeGeo][i]);
	infoBubble.setPosition(location);
	infoBubble.open(map);
}

function underMapsMap_paintMap(underMapsMapComponent){

	var infoMap = new Array();
	
	var data = eval(underMapsMapComponent.data("requestData"));
	var polygons = new Array();
	var polylines = new Array();
	var makers = new Array();	
	var map = underMapsMapComponent.data("underMapsGoogleMap");
	
	infoMap[0] = new Array();
	$(data[0]).each(function(i,polygon){ 
		polygons.push(eval(polygon));
		polygons[i].setMap(map); 
		infoMap[0].push("<div class=\"phoneytext\">"+polygons[i].html+"</div>");
		google.maps.event.addListener(polygons[i], "click", function(event){
				underMapsMap_showInfo(0,i, event.latLng, underMapsMapComponent);
		});
	}); 
	
	infoMap[1] = new Array();
	$(data[1]).each(function(i,polyline){ 
		polylines.push(eval(polyline));
		polylines[i].setMap(map);
		infoMap[1][i] = "<div class=\"phoneytext\">"+polylines[i].html+"</div>"; 
		google.maps.event.addListener(polylines[i], "click", function(event){
				underMapsMap_showInfo(1,i, event.latLng, underMapsMapComponent);
		});
	} );
	
	infoMap[2] = new Array();
	$(data[2]).each(function(i,maker){ 
		makers.push(eval(maker));
		makers[i].setMap(map); 
		infoMap[2].push("<div class=\"phoneytext\">"+makers[i].html+"</div>");
		google.maps.event.addListener(makers[i], "click", function(event){
				underMapsMap_showInfo(2,i, event.latLng, underMapsMapComponent);
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
	
	underMapsMapComponent.data("underMapsMapInfoBubble", infoBubble);

	underMapsMapComponent.data("underMapsMapInfoWindow", infowindow);
	
	underMapsMapComponent.data("underMapsMapPolygons", polygons);
	underMapsMapComponent.data("underMapsMapPolylines", polylines);
	underMapsMapComponent.data("underMapsMapMakers", makers);
	underMapsMapComponent.data("underMapsMapInfoMap", infoMap);
	
	underMapsMap_centerMap(underMapsMapComponent);  
}


function underMapsMap_centerElement(geoElement){
	var bounds = new google.maps.LatLngBounds();
	
	geoElement.getPath().forEach(function (element, index){
		bounds.extend(element);
	});
	return bounds.getCenter();
}

function underMapsMap_centerMap(underMapsMapComponent){
	var polygons = underMapsMapComponent.data("underMapsMapPolygons");
	var polylines = underMapsMapComponent.data("underMapsMapPolylines");
	var makers = underMapsMapComponent.data("underMapsMapMakers");
	var map = underMapsMapComponent.data("underMapsGoogleMap");

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

function underMapsMap_emptyData(underMapsMapComponent){
	var settings =  underMapsMapComponent.data("settings");
	var selKeys = $("#"+settings.treeRef).data("selKeys")
	if (selKeys.length == 0){
		return true;
	}
	return false;
}

function underMapsMap_setDefaultSettings(settings) { 
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

function underMapsReload(underMapsTreeComponent, underMapsMapComponent){
	
	var selKeys = underMapsTreeComponent.data("selKeys");
	//TODO url to ws whit selected key
	//and chage de settings ws?
	
	underMapsMap_initializateMap(underMapsMapComponent); 
	
	if (selKeys.length != 0){
	    underMapsMap_initializateData(underMapsMapComponent);
		underMapsMap_paintMap(underMapsMapComponent); 
	}
	
	//underMapsTree_paintLegend(underMapsTreeComponent);
}

// ********************************************************
// JQuery component
// ********************************************************
(function($) {
    // initialization function
   $.fn.underMapsMap = function(settings) {
	this.each(function() {
		
    	 underMapsMap_setDefaultSettings(settings);
         // save settings into the DOM tree
         $(this).data("settings", settings);
		 
        underMapsMap_initializateMap($(this)); 
        
        //if map must paint something or not
        if(!underMapsMap_emptyData($(this))){
        	underMapsMap_initializateData($(this));
        	underMapsMap_paintMap($(this)); 
        }         
	});
    return this;
   };
 })(jQuery);