// Tapinos Chart component
function tapinosChart_injectNoData(component, data) {
	var html = "<p class='tapinosChart-noData'>There are no data available</p>";
	component.html(html);
}

function tapinosChart_injectBigNumber(component, data) {
	var settings = component.data("tapinosChartSettings");
	var bigNumberSettings = $.extend(true, {}, settings);
	bigNumberSettings["prependXLabel"] = false;
	var bigNumberFormatted = tapinosCommon_formatPoint(data.series[0].points[0], data.series[0], data, bigNumberSettings);
	var html = "<p class='tapinosChart-bigNumber'>" + bigNumberFormatted +
		settings.bigNumberSuffix + "</p>";
	component.html(html);
	component.hide().fadeIn(800);
}

function tapinosChart_prepareChartRequest(component, chartConfig) {
	var settings = component.data("tapinosChartSettings");
	var params = $.extend(true, {}, chartConfig);
	if (settings.yMin != undefined) {
		params.yMin = settings.yMin;
	}
	if (settings.yMax != undefined) {
		params.yMax = settings.yMax;
	}
	if (settings.valueVar != undefined) {
		params.valueVar = tapinosCommon_getStringParameter(settings.valueVar);
	}
	//Used in tapinosCombos.js
	if (settings.seriesVar != undefined) {
		params.seriesVar = tapinosCommon_getStringParameter(settings.seriesVar);
	}
	//Used in tapinosCombos.js
	if (settings.seriesValues != undefined) {
		params.seriesValues = tapinosCommon_getArrayParameter(settings.seriesValues);
	}
	//Only used in tapinosMap.js lines 219 and 220 and this two lines
	if (settings.seriesAggregateFunction != undefined) {
		params.seriesAggregateFunction = tapinosCommon_getStringParameter(settings.seriesAggregateFunction);
	}
	if (settings.chartType != undefined) {
		params.chartType = tapinosCommon_getStringParameter(settings.chartType);
	}
	if (settings.dimensionConstraints != undefined) {
		params.dimensionConstraints = tapinosCommon_getArrayParameter(settings.dimensionConstraints);
	}
	if (settings.dataset != undefined) {
		params.dataset = tapinosCommon_getStringParameter(settings.dataset);
	}
	
	if (settings.endpoint != undefined) {
		params.endpoint = tapinosCommon_getStringParameter(settings.endpoint);
	}
	if (settings.namedgraph != undefined) {
		params.namedgraph = tapinosCommon_getStringParameter(settings.namedgraph);
	}
	
	return params;
}


// ********************************************************
// AJAX services callback
// ********************************************************

function tapinosChart_onChartServiceCallback(component) {
	return function(data) {
		var settings = component.data("tapinosChartSettings");
        if (data != null) {
				clearDivs(settings.errorMsgsDiv);
    			clearDivs(settings.interactiveButtonsDiv);
        		if (settings.doNotPaintChart == false) {
	        		if (data.series.length == 0 || (data.series.length == 1 && data.series[0].points.length == 0)) {
	        			tapinosChart_injectNoData(component, data);
	        		} else if (data.valueVar == undefined || (data.series.length == 1 && data.series[0].points.length == 1)) {
	        			tapinosChart_injectBigNumber(component, data);
	        		} else {

				 		if (settings.interactiveButtonsDiv != null) {
				            settings.interactiveButtonsDiv.addClass("tapinosChart-interactiveButtons");
				            tapinosChart_injectInteractiveButtonsDivCode($(this), settings.interactiveButtonsDiv);
			        	} // if


							tapinosChart_paintGoogleChart(component, data);
	
							$(window).bind("resize", function(event) {
								tapinosChart_setGraphSizeGoogleChart(component);
								tapinosChart_drawGoogleChart(component);							
							});
							
	        		}
        		}
        		// Update the attached table (if available)
        		if (settings.table != null) {
        			var tableSettings = $.extend(true, {}, settings);
        			tableSettings["prependXLabel"] = false;
        			settings.table.tapinosTableDraw(data, tableSettings);
        		}
        		// call the user-defined callback
        		if (settings.onRepaintCallback != undefined) {
        			settings.onRepaintCallback(component, data);
        		}
        }
    };
}


//********************************************************
// HTML inject 
//********************************************************

function tapinosChart_updateDownloadLinks(ws, chartConfig, linkElements, format) {
	var traditional = true;
	var params = $.extend(true, { "format": format }, chartConfig); 
	linkElements.each(function() {
		$(this).attr("href", ws + "?" + $.param(params, traditional));
	});
}


// ********************************************************
// Default settings
// ********************************************************

function tapinosChart_setDefaultSettings(settings) {
	if (settings.ws == undefined) {
		settings.ws = "ws/chart";
	}
	if (settings.downloadJSONLink == undefined) {
		settings.downloadJSONLink = $(".downloadJSON");
	}
	if (settings.downloadCSVLink == undefined) {
		settings.downloadCSVLink = $(".downloadCSV");
	}
	if (settings.decimalDigits == undefined) {
		settings.decimalDigits = 2;
	}
	if (settings.numberSuffix == undefined) {
		settings.numberSuffix = "";
	}
	if (settings.bigNumberSuffix == undefined) {
		settings.bigNumberSuffix = "";		
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
	if (settings.doNotPaintChart == undefined) {
		settings.doNotPaintChart = false;
	}
	if (settings.animationTime == undefined){
		settings.animationTime = 250;
	}	
	//It would be double of animationTime
	if (settings.animationTimeFirst == undefined){
		settings.animationTimeFirst = 500;
	}
	if (settings.interpolateNulls == undefined){
		settings.interpolateNulls = false;
	}
		
	if (settings.switchChart == undefined){
		settings.switchChart = "switchChart";
	}
	
	if (settings.animateButton == undefined){
		settings.animateButton = "animateButton";
	}
}


//********************************************************
//Google Charts Component
//********************************************************

//Load the Visualization API and the piechart package.
google.load('visualization', '1.0', {'packages':['corechart']});


function tapinosChart_paintGoogleChart(component, data) {
	var settings = component.data("tapinosChartSettings");
	var chart = new google.visualization.ColumnChart(document.getElementById(component.attr('id')));
	var typechart = "ColumnChart";
	if ( data.chartType=='LINES' ) {
		typechart = "LineChart";
		chart = new google.visualization.LineChart(document.getElementById(component.attr('id')));
	} else if ( data.chartType=='PIE' ) {
		chart = new google.visualization.PieChart(document.getElementById(component.attr('id')));
		typechart = "PieChart";	  
	}
	component.data("typeChart", typechart);
	
      var options = {
           	title: '', 
           	 
            hAxes:[
                     {title: data.xLabel,
                      textPosition : 'out'}
            ],
            vAxes:[{
             	 title: data.yLabel,
             	 minValue : data.yMin,
             	 maxValue : data.yMax, 
             	 textPosition: 'out',
                  
            }],
            pointSize: 5,
            
            legend: {
            	position: 'bottom',
            	textStyle: {fontSize: 10}
            },
            
            chartArea:{
             	left:"10%",
             	top:"10%",
             	width:"80%",
             	height: "75%"
            },
            
            animation:{
	            duration: settings.animationTime,
	            easing: 'linear',
            },
            
            //is3D is an options for Google PieChart
            is3D: true,
            
            //interpolateNulls is an option for Google LineChart
            interpolateNulls: settings.interpolateNulls,

        };

		if (data.chartType=='PIE')
		{
			options.title = data.title;
		}

      var isMobile = tapinosCommon_isMobile(navigator.userAgent);

      if (isMobile){
    	  options.hAxes[0].textPosition = 'in';
    	  options.vAxes[0].textPosition = 'in';
      }
      
      $(component).data("options", options);
      $(component).data("GoogleChart", chart);
      $(component).data("GoogleChartData", data);

      isMobile ? tapinosChart_dataGoogleChartMobile (component, data) : tapinosChart_dataGoogleChart(component, data);   
      
      	
      if (data.chartType=='VERTICAL_BARS' ){
      		var switchChartButton = settings.switchChart;
      		$("." + switchChartButton).show();
      } else{
      		var switchChartButton = settings.switchChart;
      		$("." + switchChartButton).hide();
      }
}

function tapinosChart_drawGoogleChart(component) {
	view = new google.visualization.DataView(dataTable);
	component.data("GoogleChart").draw(view, component.data("options"));
}
 
function tapinosChart_setGraphSizeGoogleChart(component) {
	//TODO: change the fixed number
	component.height((59*$(window).height())/100);
	component.width((59 *$(window).width())/100);
}

//********************************************************
// DATA
//********************************************************
function tapinosChart_dataGoogleChart (component, data){
	
	var settings = component.data("tapinosChartSettings");
	var time = settings.animationTime;
	
	dataTable = new google.visualization.DataTable();

	dataTable.addColumn('string', data.xLabel);

	tapinosChart_pushColumnsGoogleChart(dataTable, data);

    //initial value for row count is 0
	tapinosChart_animatebypointsGoogleChart (0, component, dataTable, time, data);
	
	var chart = component.data("GoogleChart");
	component.data("dataTable", dataTable);

	google.visualization.events.addListener(chart, 'select',  function() {
		
		selectHandle(component);
		
	});

}
  
function selectHandle(component){
  	var typechart = component.data("typeChart");	
  	var dataTable = component.data("dataTable");
	var options = $(component).data("options");
	var chart = component.data("GoogleChart");
	var data = component.data("GoogleChartData");
	
	if ( typechart != "PieChart" ) {

	    var selectedItem = chart.getSelection();
	    
		var item = selectedItem[0];
    
		if (typeof item.column != undefined) {
	    	var columnIndex = item.column;  
	
	    	if (dataTable.getValue(0, columnIndex) != undefined){
	    		for (var i=0; i<dataTable.getNumberOfRows(); i++){
	    			dataTable.setCell(i, columnIndex, null);
	    		}
	    		tapinosChart_drawGoogleChart(component);
	    	}else{
	        	for (var i=0; i<dataTable.getNumberOfRows(); i++){
	        		dataTable.setCell(i,columnIndex, data.series[columnIndex-1].points[i].y);
	        	}
	        	tapinosChart_drawGoogleChart(component);		        
	        }
	    }
	}
}

function tapinosChart_pushColumnsGoogleChart(dataTable, data){
	for (var i=0; i<data.series.length; i++){
		var seriesLabel = data.series[i].seriesLabel?data.series[i].seriesLabel:'';  
		//TODO: why always numbers?  	
		dataTable.addColumn ('number', seriesLabel);
	}
}

function tapinosChart_pushRowsGoogleChart(dataTable, data){
	 var j=0;
		for (seriesXLabel in data.sortedX){
			dataTable.addRows(1);

			dataTable.setCell(j, 0, data.sortedX[j]);
			
			j++;
		}
}


function tapinosChart_animatebyseriesGoogleChart(c, component, dataTable, time, data){

		var seriesLabel = data.series[c].seriesLabel?data.series[c].seriesLabel:'';    	
		dataTable.addColumn ('number', seriesLabel);

  		for ( pointIndex in data.series[c].points ) {
  			
  			var point = data.series[c].points[pointIndex];
  			var index = data.sortedX.indexOf(point.x);
  		
  			if ( data.chartType!='PIE' || point.y!=0 ) { 
  				dataTable.setCell(index, c+1, point.y);
  			}
  		}
  		
  		view = new google.visualization.DataView(dataTable);
  		tapinosChart_setGraphSizeGoogleChart(component);
  		tapinosChart_drawGoogleChart(component);
  		
  		if (c < data.series.length-1){
  			 window.setTimeout(function(){
  				tapinosChart_animatebyseriesGoogleChart (c+1, component, dataTable, time, data);
             },time);
  		}	
}

function tapinosChart_animatebypointsGoogleChart (cont, component, dataTable, time, data){
		
	   dataTable.addRows(1);
	   dataTable.setCell(cont, 0, data.sortedX[cont]); //setLabel
	   
	   //TODO: improve performance of this code
	   for (var i=0; i < data.series.length; i++) {
			var value = null; //default value
			for (var j=0; j<data.series[i].points.length; j++)
			{
				if (data.series[i].points[j].x == data.sortedX[cont]) {
					value = data.series[i].points[j].y;
					break;
				}
			}
			
			dataTable.setCell(cont, i+1, value);
		}
		
		view = new google.visualization.DataView(dataTable);
		tapinosChart_setGraphSizeGoogleChart(component);
		tapinosChart_drawGoogleChart(component);
		
		var setTime = 0;
		if (cont == 0 ){
			setTime = component.data("tapinosChartSettings").animationTimeFirst;
		}else{
			setTime = time;
		}
		
		if (cont < data.sortedX.length-1){
			 window.setTimeout(function(){
				 tapinosChart_animatebypointsGoogleChart (cont+1, component, dataTable, time, data);
         },setTime);
		}
	
}
//********************************************************
//Animation
//********************************************************
function tapinosChart_animategraphic(){ 	
	var component = $(this).data("componentRef");	
	component.data("GoogleChart").clearChart();
	tapinosChart_dataGoogleChart(component, component.data("GoogleChartData"));
}
//********************************************************
// BUTTONS 
//********************************************************
function tapinosChart_injectInteractiveButtonsDivCode(component){
	var settings = component.data("tapinosChartSettings");
	var interactiveButtonsDiv = settings.interactiveButtonsDiv;
	var html =
	'<a class="'+ settings.animateButton+'" title="Animate chart" href="javascript:void(0)"><img alt="Animate" src="images/play.png"></a>' +
	'<a class="'+ settings.switchChart+'" title="Switch columns chart" href="javascript:void(0)"><img alt="Switch" src="images/chart.png"></a>';
	 
	interactiveButtonsDiv.html(html);	
	
	var changeChartButton = $("."+settings.switchChart);
    changeChartButton.data("componentRef", component);
    changeChartButton.click(tapinosChart_onChangeChartButtonClick);
    changeChartButton.css("display", "none");
    
    var animateButton = $(".animateButton");
    animateButton.data("componentRef", component);
    animateButton.click(tapinosChart_animategraphic);
}


function tapinosChart_onChangeChartButtonClick() {
	// $this is the a element
    var component = $(this).data("componentRef");
	tapinosChart_reverseChart(component);
}


function tapinosChart_reverseChart(component){
	var typeChart = component.data("typeChart");
	var chart = component.data("GoogleChart");
	var settings = component.data("tapinosChartSettings");

	if (typeChart == "ColumnChart"){
		component.data("typeChart", "BarChart");
		chart = new google.visualization.BarChart(document.getElementById(component.attr('id')));
	}
	if (typeChart == "BarChart"){
		//options.animation.easing = "in";
		component.data("typeChart", "ColumnChart");
		chart = new google.visualization.ColumnChart(document.getElementById(component.attr('id')));
	}
	
	component.data("GoogleChart", chart);
	google.visualization.events.addListener(chart, 'select',  function() {
		selectHandle(component);	
	});

	tapinosChart_drawGoogleChart(component);
}
   
//********************************************************
// MOBILE 
//********************************************************
  function tapinosChart_dataGoogleChartMobile (component, data){
	dataTable = new google.visualization.DataTable();

	dataTable.addColumn('string', data.xLabel);

	tapinosChart_pushColumnsGoogleChart(dataTable, data);
	
	var c = 0;
	for (seriesXLabel in data.sortedX){
		dataTable.addRows(1);
		dataTable.setCell(c, 0, data.sortedX[c]);
		
		for (var index=0; index < data.series.length; index++) {
				
				var point = data.series[index].points[c];
			
				if ( data.chartType!='PIE' || point.y!=0 ) { 
					dataTable.setCell(c, index+1, point.y);
				}
			}		
		c++;
	}	
		view = new google.visualization.DataView(dataTable);
		tapinosChart_setGraphSizeGoogleChart(component);
		tapinosChart_drawGoogleChart(component);
}
  
//********************************************************
//JQuery Component 
//********************************************************

(function($) {
 $.fn.tapinosChart = function(settings) {
	  tapinosChart_setDefaultSettings(settings);
     this.each(function() {
   	var component = $(this);
       $(this).data("tapinosChartSettings", settings);
    });
    return this;
  };

 $.fn.tapinosChartDraw = function(chartConfig) {
    this.each(function() {
		var settings = $(this).data("tapinosChartSettings");
		if (settings == undefined) {
			alert('The Chart Component is not yet initialized');
		} else {
	 		var requestData = tapinosChart_prepareChartRequest($(this), chartConfig);
	 		if (settings.doNotPaintChart == false) {
	 			divLoading($(this));
	 		}

	        $.ajax({
	            url: settings.ws,
	            context: this,
	            dataType: 'json',
	            data: requestData,
	            traditional: true,
	            success: tapinosChart_onChartServiceCallback($(this)),
	            error: function() { 
	                networkError($(this).data("tapinosChartSettings").errorMsgsDiv); 
	                clearDivs($(this).data("tapinosChartSettings").toClearIfFailure);
	                return; }
	          });
	    	// Update download links (if available)
	        tapinosChart_updateDownloadLinks(settings.ws, requestData, settings.downloadJSONLink, "json");
	        tapinosChart_updateDownloadLinks(settings.ws, requestData, settings.downloadCSVLink, "csv");
		}
    });

    return this;

  };
  
  $.fn.tapinosChartIsBigNumber = function() {
	   var bigNumberParagraph = $("p", $(this));
	   return bigNumberParagraph.length > 0;
  };

})(jQuery);
