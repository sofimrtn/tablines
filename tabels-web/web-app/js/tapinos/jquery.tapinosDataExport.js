function tapinosDataExport_restoreState(component) {
   var settings = component.data("tapinosDataExportSettings");
	if (settings.selects != undefined) {
		settings.selects.each(function(i,o) { tapinosDataExport_restoreSelect(o); });
	}
}

function tapinosDataExport_restoreSelect(select) {
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

function tapinosDataExport_autobind(component) {
	var settings = component.data("tapinosDataExportSettings");
	if (settings.autobindSelects == true && settings.selects != undefined) {
		settings.selects.each(function(i,select) {
			$(select).change(tapinosDataExport_onSelectChangeFunc(component));
		});
	}
}

function tapinosDataExport_onSelectChangeFunc(component) {
	return function(event) {
		tapinosDataExport_refresh(component);
	};
}

function tapinosDataExport_refresh(component) {
	var traditional = true;
	var settings = component.data("tapinosDataExportSettings");
	var params = {};
	if (settings.endpoint != undefined){
		params["endpoint"] = settings.endpoint;
	}
	if (settings.selects != undefined) {
		settings.selects.each(function(i,o) {
			var selectedValues = $.makeArray($("option:selected", o).map(function(index, o) { return o.value; }));
			params[o.name] = selectedValues;
		});
	}
	var url = settings.ws + "?" + $.param(params, traditional);
	component.attr("href", url);
}

function tapinosDataExport_setDefaultSettings(settings) {
	if (settings.selects == undefined) {
		settings.selects = $("select.tapinosDataExport-autodetect");
	}

	if (settings.autobindSelects == undefined) {
		settings.autobindSelects = true;
	}

	if (settings.ws == undefined) {
		settings.ws = "ws/dataExport";
	}
}

(function($) {

    // initialization function
   $.fn.tapinosDataExport = function(settings) {
	   if (settings == undefined) { settings = {}; }
	   tapinosDataExport_setDefaultSettings(settings);
     this.each(function() {
         // save settings into the DOM tree
    	 $(this).data("tapinosDataExportSettings", settings);

    	 tapinosDataExport_restoreState($(this));
    	 tapinosDataExport_refresh($(this));
    	 tapinosDataExport_autobind($(this));

     }); // each
     return this;
   };

   // refresh the link
   $.fn.tapinosDataExportRefresh = function() {
	   this.each(function() {
		   tapinosDataExport_refresh($(this));
	   });
	   return this;
    };

})(jQuery);
