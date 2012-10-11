// Tapinos Common functions

function userError(element, msg) {
  if(element != null) {
    html = "<p class='userError'><img class='userErrorIcon' src='images/skin/exclamation.png' width='16' height='16' alt='User error'/>" + msg + "</p>";
    element.html(html);
  }
  return;
}

function networkError(element) {
  html = "<p class='networkError'><img class='networkErrorIcon' src='images/skin/exclamation.png' width='16' height='16' alt='Network error'/>"
  html += "Error de comunicaci&oacute;n con el servidor.</p>"
  element.html(html);
  return;
}

function divLoading(element) {
  html = "<p><img class='loadingIcon' src='images/spinner.gif' width='16' height='16' alt='Animated loading icon'/>";
  html += "Cargando par&aacute;metros. Espere, por favor.</p>";
  element.html(html);
  return;
} 

function clearDivs(divs) {
  if( divs != undefined ) {
	  divs.empty();
  }
  return;
}

function tapinosCommon_getStringParameter(p, arg) {
	if ($.isFunction(p)) {
		return p(arg);
	} else {
		return p;
	}
}

function tapinosCommon_getNumberParameter(p, arg1, arg2, arg3, arg4) {
	if ($.isFunction(p)) {
		return p(arg1, arg2, arg3, arg4);
	} else {
		return p;
	}	
}

function tapinosCommon_getArrayParameter(p, arg) {
	if ($.isFunction(p)) {
		return p(arg);
	} else {
		return p;
	}
}

function tapinosCommon_bindToggleMultiple(toggleButton, select, callback) {
	toggleButton.click(function() {
		if (select.attr("multiple") == true) {
			// close multiple
			select.removeAttr("multiple");
			toggleButton.attr("src", "images/open-multiple.png");
			select.change(); // maybe go from multi selection to single selection
    		if (callback != undefined) {
    		    callback(select, false);
    		}
		} else {
			// open multiple
			select.attr("multiple", "multiple");
			toggleButton.attr("src", "images/close-multiple.png");
    		if (callback != undefined) {
    		    callback(select, true);
    		}
		}
	});
}
	
function tapinosCommon_formatPoint(point, series, chart, settings) {
	if (settings == undefined) { settings = { "empty": "settings" }; }
	var decimalDigits = settings.decimalDigits != undefined ? tapinosCommon_getNumberParameter(settings.decimalDigits, point, series, chart, settings) : 0;
	var prependSeriesLabel = settings.prependSeriesLabel != undefined ? settings.prependSeriesLabel : false;
	var prependXLabel = settings.prependXLabel != undefined ? settings.prependXLabel : false;
	var appendMeasurementUnits = settings.appendMeasurementUnits != undefined ? settings.appendMeasurementUnits : false;
	var appendYLabel = settings.appendYLabel != undefined ? settings.appendYLabel : false;
	var format = pv.Format.number();
	format.decimal(",");
	format.group(".");
	var title = format.fractionDigits(decimalDigits).format(point.y);
	if (series != undefined && series.seriesLabel != undefined && prependSeriesLabel == true) {
		title = series.seriesLabel + ": " + title;
	}
	if (point.x != undefined && prependXLabel == true) {
		title = point.x + ": " + title;
	}
	if (settings.numberSuffix != undefined) {
		title = title + settings.numberSuffix;
	}
	if (point.valuesMeasuredIn != undefined && appendMeasurementUnits == true) {
		title = tapinosCommon_addMeasurementUnits(title, point.valuesMeasuredIn, settings);
	} else if (chart != undefined && chart.yLabel != undefined && appendYLabel == true) {
		title = tapinosCommon_addMeasurementUnits(title, chart.yLabel, settings);
    }	
	return title;
}

// this function adds the measurement units to the figure, and recognizes a few special cases
function tapinosCommon_addMeasurementUnits(title, measurementUnits, settings) {
	var separator = " ";
	if (measurementUnits == "€" || measurementUnits == "$" || measurementUnits == "%") {
		separator = "";
	}
	return title + separator + measurementUnits;
}

// taken (and fixed) from http://stackoverflow.com/questions/955030/remove-css-from-a-div-using-jquery
jQuery.fn.extend
({
    removeCSS: function(cssName) {
        return this.each(function() {
            return $(this).attr('style',
            jQuery.grep($(this).attr('style').split(";"),
                    function(curCssName) {
                        if (curCssName.toUpperCase().indexOf(cssName.toUpperCase() + ':') < 0)
                            return curCssName;
                    }).join(";"));
        });
    }
});

// taken (and fixed) from http://stackoverflow.com/questions/281264/remove-empty-elements-from-an-array-in-javascript
function cleanArray(actual){
  var newArray = new Array();
  for(var i = 0; i<actual.length; i++){
      if (actual[i] != undefined){
        newArray.push(actual[i]);
    }
  }
  return newArray;
}
