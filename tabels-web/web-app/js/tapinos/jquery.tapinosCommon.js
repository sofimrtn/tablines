// Tapinos Common functions

function userError(component, msg) {
  if(component != null) {
    html = "<p class='userError'><img class='userErrorIcon' src='images/skin/exclamation.png' width='16' height='16' alt='User error'/>" + msg + "</p>";
    component.html(html);
  }
  return;
}

function networkError(component) {
  html = "<p class='networkError'><img class='networkErrorIcon' src='images/skin/exclamation.png' width='16' height='16' alt='Network error'/>"
  html += "Server communication error.</p>"
  component.html(html);
  return;
}

function divLoading(component) {
  html = "<p><img class='loadingIcon' src='images/spinner.gif' width='16' height='16' alt='Animated loading icon'/>";
  html += "Loading parameters . Wait, please.</p>";
  component.html(html);
  return;
} 

function clearDivs(divs) {
  if( divs != undefined ) {
	  divs.empty();
  }
  return;
}
// receive a navigator.userAgent string
function tapinosCommon_isMobile(navigatorUserAgent) {
    var a = navigatorUserAgent;
    var isMobile = false;
    if (/android.+mobile|avantgo|bada\/|blackberry|blazer|compal|elaine|fennec|hiptop|iemobile|ip(hone|od)|iris|kindle|lge |maemo|midp|mmp|netfront|opera m(ob|in)i|palm( os)?|phone|p(ixi|re)\/|plucker|pocket|psp|symbian|treo|up\.(browser|link)|vodafone|wap|windows (ce|phone)|xda|xiino/i.test(a)|| /1207|6310|6590|3gso|4thp|50[1-6]i|770s|802s|a wa|abac|ac(er|oo|s\-)|ai(ko|rn)|al(av|ca|co)|amoi|an(ex|ny|yw)|aptu|ar(ch|go)|as(te|us)|attw|au(di|\-m|r |s )|avan|be(ck|ll|nq)|bi(lb|rd)|bl(ac|az)|br(e|v)w|bumb|bw\-(n|u)|c55\/|capi|ccwa|cdm\-|cell|chtm|cldc|cmd\-|co(mp|nd)|craw|da(it|ll|ng)|dbte|dc\-s|devi|dica|dmob|do(c|p)o|ds(12|\-d)|el(49|ai)|em(l2|ul)|er(ic|k0)|esl8|ez([4-7]0|os|wa|ze)|fetc|fly(\-|_)|g1 u|g560|gene|gf\-5|g\-mo|go(\.w|od)|gr(ad|un)|haie|hcit|hd\-(m|p|t)|hei\-|hi(pt|ta)|hp( i|ip)|hs\-c|ht(c(\-| |_|a|g|p|s|t)|tp)|hu(aw|tc)|i\-(20|go|ma)|i230|iac( |\-|\/)|ibro|idea|ig01|ikom|im1k|inno|ipaq|iris|ja(t|v)a|jbro|jemu|jigs|kddi|keji|kgt( |\/)|klon|kpt |kwc\-|kyo(c|k)|le(no|xi)|lg( g|\/(k|l|u)|50|54|e\-|e\/|\-[a-w])|libw|lynx|m1\-w|m3ga|m50\/|ma(te|ui|xo)|mc(01|21|ca)|m\-cr|me(di|rc|ri)|mi(o8|oa|ts)|mmef|mo(01|02|bi|de|do|t(\-| |o|v)|zz)|mt(50|p1|v )|mwbp|mywa|n10[0-2]|n20[2-3]|n30(0|2)|n50(0|2|5)|n7(0(0|1)|10)|ne((c|m)\-|on|tf|wf|wg|wt)|nok(6|i)|nzph|o2im|op(ti|wv)|oran|owg1|p800|pan(a|d|t)|pdxg|pg(13|\-([1-8]|c))|phil|pire|pl(ay|uc)|pn\-2|po(ck|rt|se)|prox|psio|pt\-g|qa\-a|qc(07|12|21|32|60|\-[2-7]|i\-)|qtek|r380|r600|raks|rim9|ro(ve|zo)|s55\/|sa(ge|ma|mm|ms|ny|va)|sc(01|h\-|oo|p\-)|sdk\/|se(c(\-|0|1)|47|mc|nd|ri)|sgh\-|shar|sie(\-|m)|sk\-0|sl(45|id)|sm(al|ar|b3|it|t5)|so(ft|ny)|sp(01|h\-|v\-|v )|sy(01|mb)|t2(18|50)|t6(00|10|18)|ta(gt|lk)|tcl\-|tdg\-|tel(i|m)|tim\-|t\-mo|to(pl|sh)|ts(70|m\-|m3|m5)|tx\-9|up(\.b|g1|si)|utst|v400|v750|veri|vi(rg|te)|vk(40|5[0-3]|\-v)|vm40|voda|vulc|vx(52|53|60|61|70|80|81|83|85|98)|w3c(\-| )|webc|whit|wi(g |nc|nw)|wmlb|wonu|x700|xda(\-|2|g)|yas\-|your|zeto|zte\-/i.test(a.substr(0, 4))) 
  	  	isMobile =  true;
	return isMobile;
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

function tapinosCommon_numberformatter(point, decimalDigits, decimal, group)
{
	var format = point.y;
	
	format = format.toFixed(decimalDigits); 
	
	return format;
}	

function tapinosCommon_formatPoint(point, series, chart, settings) {
	if (settings == undefined) { settings = { "empty": "settings" }; }
	var decimalDigits = settings.decimalDigits != undefined ? tapinosCommon_getNumberParameter(settings.decimalDigits, point, series, chart, settings) : 0;
	var prependSeriesLabel = settings.prependSeriesLabel != undefined ? settings.prependSeriesLabel : false;
	var prependXLabel = settings.prependXLabel != undefined ? settings.prependXLabel : false;
	var appendMeasurementUnits = settings.appendMeasurementUnits != undefined ? settings.appendMeasurementUnits : false;
	var appendYLabel = settings.appendYLabel != undefined ? settings.appendYLabel : false;
	var title = tapinosCommon_numberformatter (point, decimalDigits, ",", ".");
	
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
