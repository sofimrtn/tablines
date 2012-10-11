function tapinosPermalink_restoreState(component) {
   var settings = component.data("tapinosPermalinkSettings");
	if (settings.selects != undefined) {
		settings.selects.each(function(i,o) { tapinosPermalink_restoreSelect(o); });		
	}
	if (settings.radiobuttons != undefined) {
		settings.radiobuttons.each(function(i, radiobutton) { tapinosPermalink_restoreRadiobutton(radiobutton); });
	}
}

function tapinosPermalink_restoreSelect(select) {
    var rawDatasetParameter = $(document).getUrlParam(select.name);
    if (rawDatasetParameter == undefined) {
        // select the first one
        $(select).val($("option", select)[0].value, select);
    } else if ($.isArray(rawDatasetParameter)) {
    		$(select).attr("multiple", "multiple");
    		$(select).val($(rawDatasetParameter).map(function() { return decodeURIComponent(this); }));
    } else {
    		$(select).val(decodeURIComponent(rawDatasetParameter));
    }
}

function tapinosPermalink_restoreRadiobutton(radiobutton) {
    var rawDatasetParameter = decodeURIComponent($(document).getUrlParam(radiobutton.name));
    if (rawDatasetParameter == undefined) {
    		// do nothing
    } else if (radiobutton.value == rawDatasetParameter) {
    		$(radiobutton).attr("checked", true);
    }
}

function tapinosPermalink_autobind(component) {
	var settings = component.data("tapinosPermalinkSettings");
	if (settings.autobindSelects == true && settings.selects != undefined) {
		settings.selects.each(function(i,select) {
			$(select).change(tapinosPermalink_onSelectChangeFunc(component));
		});
	}
	if (settings.autobindRadiobuttons == true && settings.radiobuttons != undefined) {
		settings.radiobuttons.each(function(i,radiobutton) {
			$(radiobutton).change(tapinosPermalink_onSelectChangeFunc(component));
		});
	}
}

function tapinosPermalink_onSelectChangeFunc(component) {
	return function(event) {
		tapinosPermalink_refresh(component);
	};
}

function tapinosPermalink_refresh(component) {
	var traditional = true;
	var settings = component.data("tapinosPermalinkSettings");
	var params = {};
	if (settings.selects != undefined) {
		settings.selects.each(function(i,o) {
			var selectedValues = $.makeArray($("option:selected", o).map(function(index, o) { return o.value; }));
			params[o.name] = selectedValues;
		});
	}
	if (settings.radiobuttons != undefined) {
		settings.radiobuttons.each(function(i,radiobutton) {
			if ($(radiobutton).is(":checked")) {
				params[radiobutton.name] = radiobutton.value;
			}	
		});
	}
	if (settings.tapinosCombos != undefined) {
		settings.tapinosCombos.each(function(i,o) {
			$.extend(params, $(o).tapinosCombosGetStateParams());
		});
	}
	var url = "?" + $.param(params, traditional);
	component.attr("href", url);	
}

function tapinosPermalink_setDefaultSettings(settings) {
	if (settings.selects == undefined) {
		settings.selects = $("select.tapinosPermalink-autodetect");
	}
	if (settings.radiobuttons == undefined) {
		settings.radiobuttons = $("input.tapinosPermalink-autodetect[type='radio']");
	}
	if (settings.autobindSelects == undefined) {
		settings.autobindSelects = true;
	}
	if (settings.autobindRadiobuttons == undefined) {
		settings.autobindRadiobuttons = true;
	}
}

(function($) {
    
    // initialization function
   $.fn.tapinosPermalink = function(settings) {
	   if (settings == undefined) { settings = {}; }
	   tapinosPermalink_setDefaultSettings(settings);
     this.each(function() {
         // save settings into the DOM tree
    	 $(this).data("tapinosPermalinkSettings", settings);
    	 tapinosPermalink_restoreState($(this));
    	 tapinosPermalink_refresh($(this));
    	 tapinosPermalink_autobind($(this));
     }); // each
     return this;
   };

   // refresh the link
   $.fn.tapinosPermalinkRefresh = function() {	   
	   this.each(function() {
		   tapinosPermalink_refresh($(this));
	   });
	   return this;
    };   
   
})(jQuery);
