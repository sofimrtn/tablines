// Tapinos dynamic comboboxes component

var tapinosCombosConfig = {
    freeVarPrefix: "__FREE_VAR__"
}

function getChartParameters(tapinosCombosComponent) {
    var settings = tapinosCombosComponent.data("tapinosCombosSettings");
    var data = tapinosCombosComponent.data("tapinosRawDimensions");
    var seriesSwitchDiv = settings.seriesSwitchDiv;
    var seriesSwitchLink = settings.seriesSwitchLink;
    var dimensionConstraints =  tapinosCombos_getDimensionConstraints(tapinosCombosComponent);
    var userFreeVars = tapinosCombos_getFreeVars(tapinosCombosComponent);
    var freeVars = userFreeVars;
    var valueVar = freeVars[0];
    var chartConfig = {
        'valueVar': valueVar,
//        'yMin': 0,
//        'yMax': 100,
        'dimensionConstraints': dimensionConstraints,
        'dataset': tapinosCombosComponent.data("tapinosCombosDataset")
    }

    if (settings.series != undefined) {
   		var series = settings.series();
   		chartConfig['seriesVar'] = series.seriesVar;
   		chartConfig['seriesValues'] = series.seriesValues;
    }

    if (userFreeVars.length == 2 && chartConfig.seriesVar == undefined) {
    	chartConfig['seriesVar'] = freeVars[1];
    }

    // switch free vars if requested by the user
   if (seriesSwitchDiv != null &&  seriesSwitchLink.data("checked")==true) {
	   var swp = chartConfig['seriesVar'];
	   chartConfig['seriesVar'] = chartConfig['valueVar'];
	   chartConfig['valueVar'] = swp;
	}

    // shows or hides the series switch div
    if (seriesSwitchDiv != null) {
		if (chartConfig['seriesVar'] != undefined) {
		    var seriesLabel = tapinosCombos_getDimensionLabel(data, chartConfig['seriesVar']);
		    var valueVarLabel = tapinosCombos_getDimensionLabel(data, chartConfig['valueVar']);
		    tapinosCombos_updateSwitchingMessages(seriesSwitchDiv, seriesLabel, valueVarLabel, settings);
			seriesSwitchDiv.slideDown("slow");
		} else {
			seriesSwitchDiv.slideUp("slow");
		}
    }

	// this must be done at the end (after series switching)
    chartConfig['chartType'] = tapinosCombos_chooseChartType(chartConfig, data);

    if ($(".tapinosCombos_simplifiedSelect", tapinosCombosComponent).length > 0) {
    	chartConfig['minDimensionValueWeight'] = settings.minDimensionValueWeight;
    }

    return chartConfig;
}

function tapinosCombos_chooseChartType(chartConfig, data) {
	if (chartConfig['valueVar'] != undefined && tapinosCombos_isContinuousDimension(chartConfig['valueVar'], data)) {
		return 'LINES';
	} else {
		return 'VERTICAL_BARS';
	}
}


function tapinosCombos_getDimensionValues(tapinosCombosComponent) {
   return $.makeArray($("select > option:selected", tapinosCombosComponent).map(function() {
       return $(this).val();
       } ));
}

function tapinosCombos_getFreeVars(tapinosCombosComponent) {
  var freeValueVar = tapinosCombosComponent.data("tapinosCombosSettings").freeValueVar;
  var freeVarsFromConfig = freeValueVar != null ? [freeValueVar]: [];
  var freeVarsFromCombos = tapinosCombos_getDimensionValues(tapinosCombosComponent).filter(function(d) {
    return d.indexOf(tapinosCombosConfig.freeVarPrefix) == 0; }).map(function(d) {
      return d.slice(tapinosCombosConfig.freeVarPrefix.length);
    });
  return freeVarsFromConfig.concat(freeVarsFromCombos);
}

function tapinosCombos_getDimensionConstraints(tapinosCombosComponent) {
    var dimensionsConstraintsFromCombos = tapinosCombos_getDimensionValues(tapinosCombosComponent).filter(function(d) {
        return d.indexOf(tapinosCombosConfig.freeVarPrefix) == -1;
    });
    var fixedVars = tapinosCombosComponent.data("tapinosCombosSettings").fixedVars();
    var dimensionsConstraintsFromConfig = fixedVars.map(function(d) { return d.value; });
    return dimensionsConstraintsFromCombos.concat(dimensionsConstraintsFromConfig);
}

function tapinosCombos_isContinuousDimension(dimensionUri, rawDimensions) {
    return rawDimensions.filter(function(d) { return d.id == dimensionUri; })[0].continuous;
}

function tapinosCombos_getDimensionLabel(data, dimensionUri) {
    for ( dimensionIndex in data ) {
        if (data[dimensionIndex].id == dimensionUri) {
            return data[dimensionIndex].label;
        }
    }
    return undefined;
}

function tapinosCombos_populateDefaultSettings(settings) {
    if(settings.fixedVars == null) {
   	 	settings.fixedVars = function() { return []; };
    }
    if (settings.initiallyFreeVars == undefined) {
        settings.initiallyFreeVars = 1;
    }
    if (settings.minDimensionValueWeight == undefined) {
    	settings.minDimensionValueWeight = 0;
    }
}

function tapinosCombos_getRequestData(component, datasetURI) {
    var settings = component.data("tapinosCombosSettings");
	var requestData = {'endpoint': component.data("tapinosCombosSettings").endpoint,
			'namedgraph': component.data("tapinosCombosSettings").namedgraph,
			'dataset': datasetURI, 'dimensionConstraint': [] };
	if (settings.fixedVars != undefined) {
		var fixedVars = settings.fixedVars();
		$.each(fixedVars, function(index, value) {
			requestData.dimensionConstraint.push(value.value);
		});
	}
	if (settings.series != undefined) {
		var series = settings.series();
		if (series.seriesValues.length > 1) {
		      $.extend(requestData, series);
		}
	}
	return requestData;
}

// ********************************************************
// HTML injection
// ********************************************************





function tapinosCombos_injectCombosHierarchical(component, json, position_to_change) {

    // numero de selects que hay en el DIV
    var select_size = component.find('select').length;

    // limpiar los selects que hay hacia adelante de la posicion que hay que cambiar
    for (var i=select_size;i>=position_to_change;i--) {
        component.find('select').eq(i).remove();
    }

    var s = $("<select />");
    var future_options = [];
    if (position_to_change == 0){ // caso especial, el primer combo
        for(var i=0;i<json.rows.length;i++){
            var obj = json.rows[i].key;
            if (obj[0] === 1) {
             future_options.push(obj[1]);
            }
        }
    } else {
        for(var i=0;i<json.rows.length;i++){
            var nivel = json.rows[i].key[0];
            var key_value = json.rows[i].key[1];
            var select_value = component.find('select').eq(position_to_change-1).val();
            if ((nivel === position_to_change) && (key_value == select_value)) {
                future_options = json.rows[i].value;
            }
        }

    }

    // Añadir solo si tiene contenido
    if (future_options.length != 0){
        $.each(future_options, function(index, value) {
            $("<option />", {value: value, text: value}).appendTo(s);
        });

        s.appendTo(component);

        // que no haya una opción seleccionada por defecto
        s.prop("selectedIndex", -1);

        s.change(function() {
			tapinosCombos_injectCombosHierarchical(component, json, position_to_change+1);
            // Valor listo para enviarse al servidor
            var selectedVal = $(component).find(':selected').val();
        });
    }
}


function tapinosCombos_injectCombos(component) {
    var data = $(component).data("tapinosRawDimensions");
    var settings = $(component).data("tapinosCombosSettings");
    var freeValueVar = settings.freeValueVar;
    var initiallyFreeVars = settings.initiallyFreeVars;
    var fixedVarsFunc = settings.fixedVars();
    var constrainedVarsFromConfig = fixedVarsFunc.map(function(d) { return d.dimension; });

    component.empty();

    for ( dimensionIndex in data ) {
    	var varUri = data[dimensionIndex].id;
  	    if ( constrainedVarsFromConfig.indexOf(varUri) != -1) { continue; } // this var fixed by external constraints
  	    if ( varUri == freeValueVar ) { continue; } // this var is the free value var
  	    if ( settings.series != undefined && varUri == settings.series().seriesVar ) { continue; } // skip series var
  	    if ( data[dimensionIndex].hierarchical == true ) {

			var label = $("<label>").text(data[dimensionIndex].label);
			var div = $("<div>").attr("id", "selectDimension_" + dimensionIndex).append(label);
	        component.append(div);

			tapinosCombos_injectCombosHierarchical(div, data[dimensionIndex].dimensionValues, 0);

  	    } else {

			var label = $("<label>").attr("for", "selectDimension_" + dimensionIndex).text(data[dimensionIndex].label);
			var select = $("<select>").attr("id", "selectDimension_" + dimensionIndex);
			select.data('dimensionValues', data[dimensionIndex].dimensionValues);
			select.data('dimensionUri', varUri);
			select.data('tapinosCombos', component);
			tapinosCombos_injectOptions(select, settings.minDimensionValueWeight);
			if (data[dimensionIndex].dimensionValues.length < 2) {
        		select.attr("disabled", true);
			} else if (initiallyFreeVars > 0) {
        		$(".tapinosCombos_freeVarOption", select).attr("selected", true);
				initiallyFreeVars--;
			}

			component.append($("<span>").append(label).append(select));

			// switch to advanced options
			if (select.hasClass("tapinosCombos_simplifiedSelect") == true) {
        		var spanAdv = $("<span>").addClass("advOpt").text(" - List: ");
				var spanVFull = $("<span>").addClass("fullVersion").text("Complete");
				var spanVSep = $("<span>").addClass("sepVersion").text(" / ");
				var spanVLite = $("<span>").addClass("liteVersion").text("Reduce");

				label.append(spanAdv).append(spanVFull).append(spanVSep).append(spanVLite);
				var a = $("<a>").live().addClass('toggleOptionsLink').click(tapinosCombos_onToggleAdvancedOptionsClick);
				a.data('select', select);
				$('.fullVersion').wrap(a);
			}
		} // else
    }

}

function tapinosCombos_injectOptions(select, minDimensionValueWeight) {
	var dimensionValues = select.data('dimensionValues');
	var dimensionUri = select.data('dimensionUri');

	// regular options
	for ( dimensionValueIndex in dimensionValues ) {
        var dimensionValue = dimensionValues[dimensionValueIndex];
        if (dimensionValue.weight == undefined || minDimensionValueWeight == undefined || dimensionValue.weight >= minDimensionValueWeight ) {
        	var option = $("<option>").val(dimensionValue.value).text(dimensionValue.label);
        	select.append(option);
        } else {
        	select.addClass("tapinosCombos_simplifiedSelect");
        }
    }

    // compare option
    if (dimensionValues.length > 1) {
        var option = $("<option>").addClass('tapinosCombos_freeVarOption').val(tapinosCombosConfig.freeVarPrefix + dimensionUri).text("* Compare");
        select.append(option);
    }
}

function tapinosCombos_injectSeriesSwitchDivCode(component, seriesSwitchDiv) {
    var settings = component.data("tapinosCombosSettings");
	var alternativeSeriesClass = "tapinosCombos-alternativeSeries";
    var html =
    "<p class='tapinosCombos-seriesSwitchMessage'>You are looking data " +
    "<span class='tapinosCombos-currentSeries'>ActualSerie</span>. You can also " +
    "look them <a class='"+alternativeSeriesClass+"' href='javascript:void(0)'>AnotherSerie</a>.</p>";
    seriesSwitchDiv.html(html);

    tapinosCombos_populateDefaultSeriesSwitchLink(settings, alternativeSeriesClass);

    var seriesSwitchLink = settings.seriesSwitchLink;
    seriesSwitchLink.data("componentRef", component);
    seriesSwitchLink.data("checked", false);
    seriesSwitchLink.click(tapinosCombos_onSeriesSwitchLinkClick);
}

function tapinosCombos_updateSwitchingMessages(tapinosCombosSwitchDiv, currentSeriesLabel, alternativeSeriesLabel, settings) {
    var currentSeriesNode = $(".tapinosCombos-currentSeries", tapinosCombosSwitchDiv);
    var alternativeSeriesNode = settings.seriesSwitchLink;
    currentSeriesNode.html(currentSeriesLabel);
    alternativeSeriesNode.html(alternativeSeriesLabel);
}


// ********************************************************
// User interface callbacks
// ********************************************************

function tapinosCombos_onDimensionChange(event) {
    // $this is the select that has changed
    var combosDiv = $(this).parent().parent();
    var settings = combosDiv.data("tapinosCombosSettings");
    var seriesValues = [];
	if (event.type != "click"){
	        settings.seriesSwitchDiv.hide();
			settings.seriesSwitchLink.data("checked", false);
	}

    if ( settings.series != undefined ) {
    		seriesValues = settings.series().seriesValues;
    }
    if (tapinosCombos_getFreeVars(combosDiv).length > 1 && seriesValues.length > 1) {
        userError(settings.errorMsgsDiv,'*Compare only available in one variable');
        clearDivs(settings.toClearIfFailure);
    } else if (tapinosCombos_getFreeVars(combosDiv).length > 2) {
        userError(settings.errorMsgsDiv,'*Compare only available in a maximum of two variables');
        clearDivs(settings.toClearIfFailure);
    } else {
        settings.errorMsgsDiv.html("");
        // User input is valid at this point. Pass the control back to
        // him to handle chart parameters as he wishes.
        var callback = settings.callback;
        callback(getChartParameters(combosDiv));
    }
}

function tapinosCombos_onToggleAdvancedOptionsClick() {
	var select = $(this).data('select');
	var component = select.data('tapinosCombos');
	var settings = component.data('tapinosCombosSettings');
	var prevSelectedOption = select.val();
	select.empty();

	var a = $("<a>").live().addClass('toggleOptionsLink').click(tapinosCombos_onToggleAdvancedOptionsClick);
	a.data('select', select);

	if (select.hasClass("tapinosCombos_simplifiedSelect")) {
		select.removeClass("tapinosCombos_simplifiedSelect");
		tapinosCombos_injectOptions(select, undefined);
		$('.liteVersion').wrap(a);
		$('.fullVersion').unwrap();
	} else {
		select.addClass("tapinosCombos_simplifiedSelect");
		tapinosCombos_injectOptions(select, settings.minDimensionValueWeight);
		$('.liteVersion').unwrap();
		$('.fullVersion').wrap(a);
	}
	select.val(prevSelectedOption);
}

function tapinosCombos_onSeriesSwitchLinkClick() {
	// $this is the a element
    var component = $(this).data("componentRef");

    var previous_checked = $(this).data("checked");
    $(this).data("checked",!previous_checked);

    var select = $("select", component)[0];
    select.hack = tapinosCombos_onDimensionChange;
    select.hack(event);
    return;
}


// ********************************************************
// AJAX service callbacks
// ********************************************************

function tapinosCombos_dimensionServiceCallback(data) {
	// "this" is the component
    var settings = $(this).data("tapinosCombosSettings");
	if ($.isArray(data) && data.length == 0) {
		clearDivs($(this));
		clearDivs(settings.toClearIfFailure);
		userError(settings.errorMsgsDiv, "There are no data available for this indicator, try again with some of the related indicators");
	} else if (data != null) {
        // save the new data
        $(this).data("tapinosRawDimensions", data);

        tapinosCombos_injectCombos($(this));
        tapinosCombos_restoreState($(this));

        // Attach event handlers dynamically
        $("select", this).each(function() {
            $(this).bind('change', tapinosCombos_onDimensionChange);
        });
        // Simulate a change in the first combo
        $("select:first", this).change();
    }
}


// ********************************************************
// State save and restore
// ********************************************************

function tapinosCombos_restoreState(component) {
    // only on the first reload
    if(component.data("skipRestoreState") == false) {
    	component.data("skipRestoreState", true);
    	$("select", component).each(function(i,select) {
    		var initValueForThisSelect = $(document).getUrlParam(select.id);
    		if (initValueForThisSelect != undefined) {
    			$(select).val(decodeURIComponent(initValueForThisSelect));
    		}
       	});
    }
}
function tapinosCombos_populateDefaultSeriesSwitchLink (settings, alternativeSeriesClass){
	settings.seriesSwitchLink = $("."+alternativeSeriesClass, settings.seriesSwitchDiv); // Fixed default value
}
// ********************************************************
// JQuery component
// ********************************************************

(function($) {
   $.fn.tapinosCombos = function(settings) {
     this.each(function() {
         tapinosCombos_populateDefaultSettings(settings);
        $(this).data("tapinosCombosSettings", settings);
    	$(this).data("skipRestoreState", false);
        $(this).addClass("tapinosCombos");

        if (settings.seriesSwitchDiv != null) {
            settings.seriesSwitchDiv.addClass("tapinosCombos-seriesSwitch");
            tapinosCombos_injectSeriesSwitchDivCode($(this), settings.seriesSwitchDiv);
        } // if
     }); // each
     return this;
   };

  $.fn.tapinosCombosReload = function(datasetURI) {
     this.each(function() {
        divLoading($(this));
        $(this).data("tapinosCombosDataset", datasetURI);
        $.ajax({
          context: this,
          url: $(this).data("tapinosCombosSettings").ws,
          dataType: 'json',
          data: tapinosCombos_getRequestData($(this), datasetURI),
          traditional: true,
          success: tapinosCombos_dimensionServiceCallback,
          error: function() {
            networkError($(this).data("tapinosCombosSettings").errorMsgsDiv);
            clearDivs($(this).data("tapinosCombosSettings").toClearIfFailure);
            return; }
        });
     });
     return this;
   };

  $.fn.tapinosCombosGetStateParams = function() {
	  var state = {};
	  this.each(function() {
		  $("select", $(this)).each(function(i,select) {
			  var selectId = $(select).attr("id");
			  var selectedOptions = $.makeArray($("option:selected", this).map(function(i2,o) { return o.value; }));
		  	  state[selectId] = selectedOptions;
		  });
	  });
	  return state;
  };

 })(jQuery);
