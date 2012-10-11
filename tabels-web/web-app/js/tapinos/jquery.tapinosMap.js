// Tapinos map component

function tapinosMap_onDataReload(data) {
    var tapinosMapComponent = $(this);
	var settings = tapinosMapComponent.data("tapinosMapSettings");
    tapinosMapComponent.data("data", data);
    if (data != null) { 
        tapinosMap_repaintSlider(tapinosMapComponent, data);
        tapinosMap_refreshMapForCurrentSeriesValue(tapinosMapComponent);
        if (settings.onDataReload != undefined ) {
        	settings.onDataReload(tapinosMapComponent, data);
        }
    }
}

function tapinosMap_onDataOverlayReload(dataOverlay) {
    var tapinosMapComponent = $(this);
	var settings = tapinosMapComponent.data("tapinosMapSettings");
    tapinosMapComponent.data("dataOverlay", dataOverlay);
    if (dataOverlay != null) { 
    	tapinosMap_refreshOverlay(tapinosMapComponent, dataOverlay);
    	if (settings.onDataOverlayReload != undefined ) {
    		settings.onDataOverlayReload(tapinosMapComponent, dataOverlay);
    	}
    }
}

function tapinosMap_refreshMapForCurrentSeriesValue(tapinosMapComponent) {
	var settings = tapinosMapComponent.data("tapinosMapSettings");
	var seriesIndex = 0;
	if (settings.sliderVar != undefined && settings.slider.size() != 0) {
	    var sliderBar = $(".tapinosMap-sliderBar", $(settings.slider));
	    var selectedYear = tapinosMapComponent.data("selectedYear");
		var baseYear = sliderBar.slider("option", "min");
	    var data = tapinosMapComponent.data("data");
	    seriesIndex = selectedYear-baseYear;
	}
	tapinosMap_paintMap(tapinosMapComponent, seriesIndex);
}

function tapinosMap_onSvgClickCallback(settings) {
	// $this is the SVG shape where the click has taken place
    return function() {
        var userCallbackFunc = settings.userCallbackFunc;
        var uri = $(this).data("uri");
        var label = $(this).data("label");
        userCallbackFunc(uri, label); 
    }
}

function tapinosMap_paintMap(tapinosMapComponent, seriesIndex) {
    var data = tapinosMapComponent.data("data");
    var settings = tapinosMapComponent.data("tapinosMapSettings");
    var seriesData = data.series[seriesIndex];
	var colorScale = pv.Scale.linear(data.yMin, data.yMax).range(settings.minColor, settings.maxColor);

	var points = seriesData.points;
    var svgDocument = $("object",tapinosMapComponent).get(0).contentDocument;
    var svgElement = svgDocument.getElementsByTagName("svg")[0];
    if (svgElement == undefined) {
    		// the SVG data has not been loaded yet
    } else {
	    // enable browser re-scaling: http://stackoverflow.com/questions/644896/how-do-i-scale-a-stubborn-svg-embedded-with-the-object-tag
	    svgElement.removeAttribute('width');
	    svgElement.removeAttribute('height');    
		for (pointIndex in points) {
			var point = points[pointIndex];
			var pointLabel = point.x;
			var svgId = tapinosMap_getSvgIdForUri(settings.uriToSvgIdResolver, point.xUri);
			if (svgId != null) {
				var svgNode = svgDocument.getElementById(svgId);
				if (svgNode == null) {
					// there is not any SVG node with that ID
				} else {
					var color = colorScale(point.y).color;
					if (point.y == 0 && settings.colorForZero != undefined) {
						color = settings.colorForZero;
					}
	                $(svgNode).data("fillColor", color);
	                var title = tapinosCommon_formatPoint(point, seriesData, data, settings);
	                $(svgNode).data("title", title);
	                $(svgNode).removeCSS("fill");
				    $(svgNode).animate({svgFill: color}, 500);
					$(svgNode).unbind();
					$(svgNode).data("label", point.x);
					$(svgNode).data("uri", point.xUri);
					if (settings.userCallbackFunc) {
					    $(svgNode).click(tapinosMap_onSvgClickCallback(settings));
						$(svgNode).css("cursor", "pointer");
				    }
				    var tooltip = $(".tapinosMap-tooltip", tapinosMapComponent);
					$(svgNode).mouseover(function(event) {
					    tooltip.text($(event.target).data("title")).show();
					    $(this).attr({'fill': '#ffffff'});
					    $(this).clearQueue().animate({svgFill: $(this).data("fillColor")}, 1000); }
					    );
					$(svgNode).mouseout(function() {
					    tooltip.clearQueue().hide();
					    });
					$(svgNode).mousemove(function(event) {
						// event coordinates are relative to the upper-left corner of the "object" element
						if ( event.pageX < $("object",tapinosMapComponent).width()/2 ) { // left half
							x = event.pageX + 10;
						} else { // right half
							x = event.pageX - 10 - tooltip.width();
						}
						tooltip.css({position: "relative", top: event.pageY - $("object",tapinosMapComponent).height() + 10, left: x});
					    });
				}
			}
		}
    }
	
	tapinosMap_repaintLegend(tapinosMapComponent, data, seriesData, colorScale);
}

function tapinosMap_refreshOverlay(tapinosMapComponent) {
    var data = tapinosMapComponent.data("dataOverlay");
    var settings = tapinosMapComponent.data("tapinosMapSettings");
    var seriesData = data.series[0];	 // always first series
	var radiusScale = pv.Scale.linear(data.yMin, data.yMax).range(settings.minRadius, settings.maxRadius);
    var svgDocument = $("object",tapinosMapComponent).get(0).contentDocument;    
	var points = seriesData.points;
	for (pointIndex in points) {
		var point = points[pointIndex];
		var pointLabel = point.x;
		var svgId = tapinosMap_getSvgIdForUri(settings.uriToSvgIdResolverOverlay, point.xUri);
		if (svgId != null) {
			var svgNode = svgDocument.getElementById(svgId);
			if (svgNode == null) {
				// there is not any SVG node with that ID
			} else {
                var radius = radiusScale(point.y);
				if (point.y == 0 && settings.radiusForZero != undefined) {
					radius = settings.radiusForZero;
				}
                var title = tapinosCommon_formatPoint(point, seriesData, data, settings);
                $(svgNode).data("title", title);
				$(svgNode).attr("r", radius);
				$(svgNode).unbind();
				$(svgNode).data("label", point.x);
				$(svgNode).data("uri", point.xUri);
			    var tooltip = $(".tapinosMap-tooltip", tapinosMapComponent);
				$(svgNode).mouseover(function(event) {
				    tooltip.text($(event.target).data("title")).show();
					});
				$(svgNode).mouseout(function() {
				    tooltip.clearQueue().hide();
				    });
				$(svgNode).mousemove(function(event) {
				    tooltip.css({position: "relative", top: event.pageY - $("object").height() + 10, left: event.pageX + 10});
				    });
			}
		}
	}
}

function tapinosMap_getSvgIdForUri(uriToSvgIdResolver, x) {
	if (uriToSvgIdResolver == undefined) {
		return x;
	} else if (typeof uriToSvgIdResolver == "string" && x.startsWith(uriToSvgIdResolver)) {
		return x.substring(uriToSvgIdResolver.length);
	} else if ($.isFunction(uriToSvgIdResolver)) {
		return uriToSvgIdResolver(x);
	} else if ($.isPlainObject(uriToSvgIdResolver)) {
		return uriToSvgIdResolver[x];
	} else {
		alert("TapinosMap: Cannot determine the type of uriToSvgIdResolver");
		return undefined;
	}
}

function tapinosMap_repaintLegend(tapinosMapComponent, chart, series, colorScale) {
    var settings = $(tapinosMapComponent).data("tapinosMapSettings");
    var yMin = chart.yMin;
    var yMax = chart.yMax;
	settings.legend.each(function() {
	    var height = Math.max($(this).height() - 30 - 5, 1);
	    var width = Math.max($(this).width() - 5 - 5, 1);
	    var seriesdata = pv.range(yMin, yMax, (yMax-yMin) / width);
	    var xScale = pv.Scale.linear(yMin, yMax).range(0,width);
	
	   var vis = new pv.Panel()
	           .height(height)
	           .width(width)
	           .bottom(5)
	           .top(30)
	           .left(20)
	           .right(20);
	;
	   vis.add(pv.Bar)
	           .data(seriesdata)
	           .top(3)
				.left(xScale)
	           .fillStyle(colorScale);
	
	   vis.add(pv.Rule)
	           .data(xScale.ticks(4))                
	           .strokeStyle("lightgray")
	           .left(xScale)
	           .anchor('top').add(pv.Label).text(function (d) { return tapinosCommon_formatPoint({y: d}, series, chart, settings); } );
	
	   vis.canvas(this).render(); 
	});
}

function tapinosMap_prepareMapRequest(settings) {
	var request = {
        'valueVar': tapinosCommon_getStringParameter(settings.geoVar),
		'dataset': tapinosCommon_getStringParameter(settings.dataset),
		'dimensionConstraints': tapinosCommon_getArrayParameter(settings.dimensionConstraints),
	};
	if (settings.sliderVar != undefined) {
		request["seriesVar"] = tapinosCommon_getStringParameter(settings.sliderVar);
	}
	if (settings.seriesVar != undefined) {
		request["seriesVar"] = tapinosCommon_getStringParameter(settings.seriesVar);
	}
	if (settings.seriesAggregateFunction != undefined) {
		request["seriesAggregateFunction"] = tapinosCommon_getStringParameter(settings.seriesAggregateFunction);
	}
	if (settings.seriesValues != undefined) {
		request["seriesValues"] = tapinosCommon_getArrayParameter(settings.seriesValues);
	}
	return request;
}


function tapinosMap_prepareMapOverlayRequest(settings) {
	var request = {
        'valueVar': tapinosCommon_getStringParameter(settings.geoVar),
		'dataset': tapinosCommon_getStringParameter(settings.datasetOverlay),
		'dimensionConstraints': tapinosMap_getDimensionConstraintsAsList(settings.dimensionConstraintsOverlay),
	};
	if (settings.sliderVar != undefined) {
		request["seriesVar"] = tapinosCommon_getStringParameter(settings.sliderVar);
	}
	return request;
}

function tapinosMap_getDimensionConstraintsAsList(dimensionConstraints) {
	if ($.isFunction(dimensionConstraints)) {
		return dimensionConstraints();
	} else if ($.isArray(dimensionConstraints)) {
		return dimensionConstraints;
	} else {
		alert('Unable to get dimension constraints as list: ' + dimensionConstraints);
	}
}

// ********************************************************
// slider
// ********************************************************

function tapinosMap_repaintSlider(tapinosMapComponent, data) {
	var settings = $(tapinosMapComponent).data("tapinosMapSettings");
	var minYear = parseInt(data.series[0].seriesLabel);
	var maxYear = parseInt(data.series[data.series.length-1].seriesLabel);
	settings.slider.each(function() {
		var sliderBar = $(".tapinosMap-sliderBar", $(this));
		// by default, the slider is at the end of the scale
		$(tapinosMapComponent).data("selectedYear", maxYear);
		sliderBar.slider({
			min: minYear,
			max: maxYear,
			step: 1,
			slide: tapinosMap_onSliderChange,
			animate: true,
		});
	    sliderBar.slider('value', $(tapinosMapComponent).data("selectedYear"));
	});
}

// callback for the manual slide of the slider
function tapinosMap_onSliderChange(event, ui) {
    // $this is the slider
    var tapinosMapComponent = $(this).parent().data("tapinosMapComponent");
    var settings = tapinosMapComponent.data("tapinosMapSettings");
    var data = tapinosMapComponent.data("data");
    // changes the state
    tapinosMapComponent.data("selectedYear", ui.value);
	tapinosMap_refreshMapForCurrentSeriesValue(tapinosMapComponent);
    // invoke the user callback
    if(settings.onSliderChangeFunc != null) {
        settings.onSliderChangeFunc(ui.value);
    }
}

// callback for the slider animation button
function tapinosMap_onPlayButtonClick() {
    // $this is the start/stop button
    var playButton = $(this);
    var mapComponent = playButton.parent().data("tapinosMapComponent");
    var timer = playButton.data("timer");
    var data = mapComponent.data("data");
    var settings = mapComponent.data("tapinosMapSettings");
    settings.slider.each(function() {
	    var sliderBar = $(".tapinosMap-sliderBar", $(this));
	    var minYear = sliderBar.slider("option", "min");
	    var maxYear = sliderBar.slider("option", "max");
	    var selectedYear = $(mapComponent).data("selectedYear");
	    if (timer != undefined) {
	        tapinosMap_stopTimer(playButton, timer);
	    } else {
	        // auto-rewind if play button is pressed at the end of the scale
	        if (selectedYear == maxYear) {
	            selectedYear = minYear;
	            mapComponent.data("selectedYear", selectedYear);
	        }
	        // set the value of the slider
	        sliderBar.slider('value', selectedYear);
	        playButton.attr("src", 'images/stop.png');
	        // updates the map
	        tapinosMap_refreshMapForCurrentSeriesValue(mapComponent);
	        // sets the timer
	        var newTimer = setInterval(tapinosMap_incrementSlider, settings.delay, $(this)); // FIXME: extra params may be a problem for non-Mozilla browsers
	        playButton.data("timer", newTimer);
	    }
    });
}

// stop the animation
function tapinosMap_stopTimer(playButton, timer) {
    clearInterval(timer);
    playButton.data("timer", null);
    playButton.attr("src", 'images/play.png');
}

function tapinosMap_incrementSlider(slider) {
    var mapComponent = slider.data("tapinosMapComponent");
    var sliderBar = $(".tapinosMap-sliderBar", slider);
    var maxYear = sliderBar.slider("option", "max");
    var selectedYear = mapComponent.data("selectedYear");
    var settings = mapComponent.data("tapinosMapSettings");
    // stop the timer if the end of the scale is reached
    if (selectedYear >= maxYear) {
        var playButton = $(".tapinosMap-playButton", slider);
        tapinosMap_stopTimer(playButton, playButton.data("timer"));
     } else {
         selectedYear++;
         mapComponent.data("selectedYear", selectedYear);
         sliderBar.slider('value', selectedYear);
         tapinosMap_refreshMapForCurrentSeriesValue(mapComponent);
     }
     // invoke the user callback
     if (settings.onSliderChangeFunc != null) {
         settings.onSliderChangeFunc(selectedYear);
     }
}


// ********************************************************
// HTML injection
//********************************************************

function tapinosMap_injectHTML(component) {
    var settings = $(component).data("tapinosMapSettings");
    
    $("object", component).after("<div class='tapinosMap-tooltipRumo'><span class='tapinosMap-tooltip'></span></div>");
    $(".tapinosMap-tooltip", component).hide().css({position: "absolute", left:0, top:0, width: 200});
    
    settings.slider.each(function() {
        var html =
            '<img class="tapinosMap-playButton" src="images/play.png" alt="Play" title="Iniciar o parar la animaci&oacute;n"/>' +
            "<div class='tapinosMap-sliderBar'></div>";
        $(this).html(html);
    
        var playButton = $(".tapinosMap-playButton", $(this));
        playButton.data("timer", undefined);
        playButton.click(tapinosMap_onPlayButtonClick);
    
        var slider = $(".tapinosMap-sliderBar", $(this)); 
    		slider.slider({value: 0, min: 0, max: 0, step: 1});
	});
}

// ********************************************************
// JQuery component
// ********************************************************

function tapinosMap_setDefaultSettings(settings) {
    // set default values for settings
	if(settings.ws == undefined) {
		settings.ws = "ws/chart";
	}
    if(settings.dimensionConstraints == undefined) {
  	 	 settings.dimensionConstraints = function() { return []; };
    }
    if(settings.dimensionConstraintsOverlay == undefined) {
 	 	 settings.dimensionConstraintsOverlay = function() { return []; };
   }
    if(settings.delay == undefined) {
        settings.delay = 900;
    }
    if(settings.minColor == undefined) {
        settings.minColor = 'orange';
    }
    if(settings.maxColor == undefined) {
        settings.maxColor = 'darkgreen';
    }
    if (settings.slider == undefined) {
        settings.slider = $(".tapinosMap-slider");
    }
    if (settings.legend == undefined) {
        settings.legend = $(".tapinosMap-legend");
    }
	if (settings.decimalDigits == undefined) {
		settings.decimalDigits = 2;
	}
	if (settings.numberSuffix == undefined) {
		settings.numberSuffix = "";
	}
	if (settings.appendYLabel == undefined) {
		settings.appendYLabel = true;
	}
	if (settings.appendMeasurementUnits == undefined) {
		settings.appendMeasurementUnits = true;
	}
	if (settings.prependXLabel == undefined) {
		settings.prependXLabel = true;
	}
	if (settings.minRadius == undefined) {
		settings.minRadius = 10;
	}
	if (settings.maxRadius == undefined) {
		settings.maxRadius = 40;
	}
}

(function($) {
    
    // initialization function
   $.fn.tapinosMap = function(settings) {
     this.each(function() {
    	 tapinosMap_setDefaultSettings(settings);
         // save settings into the DOM tree
        $(this).data("tapinosMapSettings", settings);
        tapinosMap_injectHTML(this);
        if (settings.slider != undefined) {
            $(settings.slider).data("tapinosMapComponent", $(this));
        }
        if (settings.legend != undefined) {
            $(settings.legend).data("tapinosMapComponent", $(this));
        }
     }); // each
     return this;
   };

   // launch a data reload
  $.fn.tapinosMapReloadData = function() {
     this.each(function() {
        // divLoading($(this));
        var settings = $(this).data("tapinosMapSettings");
        if (settings == undefined) {
        	alert('El componente de mapas aún no está inicializado');
        } else {
	        $.ajax({
	            context: this,
	            url: settings.ws,
	            dataType: 'json',
	            data: tapinosMap_prepareMapRequest(settings),
	            traditional: true,
	            success: tapinosMap_onDataReload,
	          });
	        if (settings.datasetOverlay != undefined) {
		        $.ajax({
		            context: this,
		            url: settings.ws,
		            dataType: 'json',
		            data: tapinosMap_prepareMapOverlayRequest(settings),
		            traditional: true,
		            success: tapinosMap_onDataOverlayReload,
		          });
	       }
        }
     });
     return this;
   };

  $.fn.tapinosMapGetState = function() {
      var statuses = this.map(function() {
          var status = {};
          status["selectedYear"] = $(this).data("selectedYear");
          return status;
      });
      return statuses;
  };

 })(jQuery);
