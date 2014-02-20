function  undermapsMap_initializateMap(undermapsMapComponent){

   var initialMapCenter = new google.maps.LatLng(0,0);
   var gmapsOptions = {
      zoom: undermapsMapComponent.data("settings").mapZoom,
      center: initialMapCenter, // TODO change for settings value
      mapTypeId: undermapsMapComponent.data("settings").mapType,
    };

    var map = new google.maps.Map(document.getElementById("mapGoogle"), gmapsOptions);

	undermapsMapComponent.data("undermapsGoogleMap", map);
}

function undermapsMap_initializateData(undermapsMapComponent){
	var settings =  undermapsMapComponent.data("settings");
	var selKeys = $("#"+settings.treeRef).data("selKeys")
	$.ajax({
		 async: false,
		 dataType: 'text',
		 //TODO Change for ws url
		 url: undermapsMapComponent.data("settings").mapAreaWs, //'ws/map2.json',
		 data: {
			 'types': selKeys,
			 'endpoint': settings.endpoint,
			 'namedgraph': settings.namedgraph
		 },
		 success: function(data) {
			 undermapsMapComponent.data("requestData", data);
		 },
	});
}

function undermapsMap_showInfo(typeGeo, i, location, undermapsMapComponent){

	var infoMap = undermapsMapComponent.data("undermapsMapInfoMap");
	var infowindow = undermapsMapComponent.data("undermapsMapInfoWindow");
	var map = undermapsMapComponent.data("undermapsGoogleMap");
	var location ="";

	switch (typeGeo){
		case 0: var arrayPolygons = undermapsMapComponent.data("undermapsMapPolygons");
				var polygon = arrayPolygons[i];
				location = undermapsMap_centerElement(polygon);
				break;

		case 1: var arrayPolyline = undermapsMapComponent.data("undermapsMapPolylines");
			    var polyline = arrayPolyline[i];
			    location = undermapsMap_centerElement(polyline);
				break;
		case 2: var arrayMaker = undermapsMapComponent.data("undermapsMapMakers");
				var maker = arrayMaker[i];
				location = maker.getPosition();
				location = new google.maps.LatLng(location.lat()+0.01, location.lng()+0.01);
				break;
	}

	infowindow.setContent(infoMap[typeGeo][i]);
	infowindow.setPosition(location);
	//infowindow.open(map); // TODO change after demo

	var infoBubble = undermapsMapComponent.data("undermapsMapInfoBubble");
	infoBubble.setContent(infoMap[typeGeo][i]);
	infoBubble.setPosition(location);
	infoBubble.open(map);
}

function undermapsMap_paintMap(undermapsMapComponent){

	var infoMap = new Array();

	var data = eval(undermapsMapComponent.data("requestData"));
	var polygons = new Array();
	var polylines = new Array();
	var makers = new Array();
	var map = undermapsMapComponent.data("undermapsGoogleMap");

	infoMap[0] = new Array();
	$(data[0]).each(function(i,polygon){
		polygons.push(eval(polygon));
		polygons[i].setMap(map);
		infoMap[0].push("<div class=\"phoneytext\">"+polygons[i].html+"</div>");
		google.maps.event.addListener(polygons[i], "click", function(event){
				undermapsMap_showInfo(0,i, event.latLng, undermapsMapComponent);
		});
	});

	infoMap[1] = new Array();
	$(data[1]).each(function(i,polyline){
		polylines.push(eval(polyline));
		polylines[i].setMap(map);
		infoMap[1][i] = "<div class=\"phoneytext\">"+polylines[i].html+"</div>";
		google.maps.event.addListener(polylines[i], "click", function(event){
				undermapsMap_showInfo(1,i, event.latLng, undermapsMapComponent);
		});
	} );

	infoMap[2] = new Array();
	$(data[2]).each(function(i,maker){
		makers.push(eval(maker));
		makers[i].setMap(map);
		infoMap[2].push("<div class=\"phoneytext\">"+makers[i].html+"</div>");
		google.maps.event.addListener(makers[i], "click", function(event){
				undermapsMap_showInfo(2,i, event.latLng, undermapsMapComponent);
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

	undermapsMapComponent.data("undermapsMapInfoBubble", infoBubble);

	undermapsMapComponent.data("undermapsMapInfoWindow", infowindow);

	undermapsMapComponent.data("undermapsMapPolygons", polygons);
	undermapsMapComponent.data("undermapsMapPolylines", polylines);
	undermapsMapComponent.data("undermapsMapMakers", makers);
	undermapsMapComponent.data("undermapsMapInfoMap", infoMap);

	undermapsMap_centerMap(undermapsMapComponent);
}


function undermapsMap_centerElement(geoElement){
	var bounds = new google.maps.LatLngBounds();

	geoElement.getPath().forEach(function (element, index){
		bounds.extend(element);
	});
	return bounds.getCenter();
}

function undermapsMap_centerMap(undermapsMapComponent){
	var polygons = undermapsMapComponent.data("undermapsMapPolygons");
	var polylines = undermapsMapComponent.data("undermapsMapPolylines");
	var makers = undermapsMapComponent.data("undermapsMapMakers");
	var map = undermapsMapComponent.data("undermapsGoogleMap");

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

function undermapsMap_emptyData(undermapsMapComponent){
	var settings =  undermapsMapComponent.data("settings");
	var selKeys = $("#"+settings.treeRef).data("selKeys")
	if (selKeys.length == 0){
		return true;
	}
	return false;
}

function undermapsMap_setDefaultSettings(settings) {
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

function undermapsReload(undermapsTreeComponent, undermapsMapComponent){

	var selKeys = undermapsTreeComponent.data("selKeys");
	//TODO url to ws whit selected key
	//and chage de settings ws?

	undermapsMap_initializateMap(undermapsMapComponent);

	if (selKeys.length != 0){
	    undermapsMap_initializateData(undermapsMapComponent);
		undermapsMap_paintMap(undermapsMapComponent);
	}

	//undermapsTree_paintLegend(undermapsTreeComponent);
}

// ********************************************************
// JQuery component
// ********************************************************
(function($) {
    // initialization function
   $.fn.undermapsMap = function(settings) {
	this.each(function() {

    	 undermapsMap_setDefaultSettings(settings);
         // save settings into the DOM tree
         $(this).data("settings", settings);

        undermapsMap_initializateMap($(this));

        //if map must paint something or not
        if(!undermapsMap_emptyData($(this))){
        	undermapsMap_initializateData($(this));
        	undermapsMap_paintMap($(this));
        }
	});
    return this;
   };
 })(jQuery);
