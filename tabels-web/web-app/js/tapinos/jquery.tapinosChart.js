// Tapinos Chart component

function tapinosChart_paintHighChartsChart(element, data) {  
      var settings = element.data("tapinosChartSettings");
      seriesType = 'column';
      if ( data.chartType=='LINES' ) {
      	seriesType = 'line';
      } else if ( data.chartType=='PIE' ) {
      	seriesType = 'pie';
      }
      var options = {
      	 title: {
      	 	text: ''
      	 },
         chart: {
            renderTo: element.attr("id"),
            marginBottom: 50,
            defaultSeriesType: seriesType,
            zoomType: 'xy'            
         },
         xAxis: {
        	 title: { text: data.xLabel }
         },
         yAxis: {
        	 title: { text: data.yLabel },
        	 min: data.yMin,
        	 max: data.yMax,    
         },
		 legend: {},
         series: {},
         tooltip: {
			borderWidth: 0,
			formatter: function() {
				var point = { "x": this.point.name, "y": this.y };
				var series = { "seriesLabel": this.series.name };
				return tapinosCommon_formatPoint(point, series, data, settings);
			}
         },      
         plotOptions: {
         	column: {},
         	bar: {},
         	line: {
	        	series: {
    	        	marker: {
        	        	lineWidth: 2,
            	    	lineColor: null // inherit from series
            		}
        		}
    	 	},
    	 	pie: {
    	 		dataLabels: {
    	 			formatter: function() {
						if ( this.y>0 ) {
							var point = { "x": this.point.name, "y": this.y };
							var series = { "seriesLabel": this.series.name };
							return "<strong>"+tapinosCommon_formatPoint(point, series, data, settings)+"</strong>";
                  		} else {
                  			return "";
                  		}
               		}
    	 		}
    	 	},
    	 	series: {
    	 		animation: {
					duration: 1000,
					easing: 'linear',
				}
    	 	}
    	 },
    	 exportButtons: {
    	 	enabled: true
    	 },
    	 exporting: {
   			url: "export.do?uuid="+(new Date().getTime())
   		 }
	  };	
	
	// Asignamos los valores de la grÃ¡fica
    //options.title.text = data.title;
    
    options.series = new Array();
    // Si solo tenemos una serie la partimos para simular varias y utilizar la leyenda en vez de etiqueta en el eje X (que da problemas con nombres largos)
    if ( data.series.length==1 && seriesType=='column' && !data.xLabel.match(/^Territorio de la estad/) ) {
   		for ( var pointIndex=0; pointIndex<data.series[0].points.length; pointIndex++ ) {
   			var seriesLabel = data.series[0].points[pointIndex].x?data.series[0].points[pointIndex].x:'';   		    	   	
   			var series = new Array( data.series[0].points.length );
   			for( var seriesIndex=0; seriesIndex<series.length; seriesIndex++ ) {
   				if ( seriesIndex==pointIndex ) {
   					series[pointIndex] = data.series[0].points[pointIndex].y;
   				} else {
   					series[seriesIndex] = null;
   				}
   			}
   			options.series.push({
   				name: seriesLabel,
   				data: series
   			});
   		}
    	options.xAxis["categories"] = data.sortedX;
    	options.xAxis['labels'] = { 'enabled': false };
    	options.xAxis['tickWidth'] = 0;    	
    	options.plotOptions.column['pointPadding'] = -0.4;
    	options.plotOptions.bar['pointPadding'] = -0.4;
    } else {    	
    	if (data.chartType == "LINES" && settings.parseXAsDates == true) {
    		options.xAxis["type"] = "datetime";
    		/*if (settings.xTickInterval != undefined) { // undocumented option
    			options.xAxis["tickInterval"] = settings.xTickInterval;
    		}//*/
    		// Si trabajamos con fechas indicamos intervalos anuales (365 dias * 24 horas * 3600 seg * 1000ms)
    		options.xAxis["tickInterval"] = 31536000000;
    	} else {
    		options.xAxis["categories"] = data.sortedX;
    	}
    	for( seriesIndex in data.series ) {
    		// Si tenemos el nombre de la serie lo asignamos
    		var seriesLabel = data.series[seriesIndex].seriesLabel?data.series[seriesIndex].seriesLabel:'';    		
      		var thisSeries = new Array(data.sortedX.length);
      		for ( pointIndex in data.series[seriesIndex].points ) {
      			var point = data.series[seriesIndex].points[pointIndex];
      			var index = data.sortedX.indexOf(point.x);
      			// Para grÃ¡ficos de tarta y con valor 0 no se aÃ±ade para que no salga en la leyenda
      			if ( data.chartType!='PIE' || point.y!=0 ) { 
      				// Otros grÃ¡ficos o si tiene valor distinto de 0 se aÃ±ade para que salga en la leyenda
      				thisSeries[index] = new Array();
      				thisSeries[index].push(data.chartType == "LINES" && settings.parseXAsDates == true ? Date.parse(point.x) : point.x);
      				thisSeries[index].push(point.y);
      			}
      		}
      		options.series.push({
      			name: seriesLabel,
      			data: cleanArray(thisSeries) // remove "undefined" points
      		});
		}
		options['plotOptions']['column']['groupPadding'] = 0.1;
	}
    if (options.series.length == 1) {
    	options['legend']['enabled'] = (seriesType=='pie');
    	options['plotOptions']['column']['colorByPoint'] = true;
    	options['plotOptions']['bar']['colorByPoint'] = true;
    } else {
    	options['plotOptions']['column']['colorByPoint'] = false;
    	options['plotOptions']['bar']['colorByPoint'] = false;
    	options.chart.marginBottom = 60 + (10*options.series.length);
    }
    var chart = $(element).data("highchartsChart");
    
    if (chart != undefined && typeof chart=='Object') {
    		chart.destroy();
    }
    $(element).data("options", options);
	chart = new Highcharts.Chart(options);
	$(element).data("highchartsChart", chart);
	

	$('.highcharts-legend text').tooltip({ 
	    	bodyHandler: function() { 
	        	return $($(this).find('tspan')[1]).text(); 
	    	}, 
	    	showURL: false 
	});
	$('.highcharts-axis text').tooltip({ 
	    	bodyHandler: function() { 
	        	return $($(this).find('tspan')[1]).text(); 
	    	}, 
	    	showURL: false 
	});
	
	if ( data.chartType=='LINES' ) {
		settings.toggleChartTypeButton.hide();
		settings.animateButton.show();
	} else if ( data.chartType=='VERTICAL_BARS' ){
		settings.toggleChartTypeButton.show();
		settings.animateButton.hide();
	} else {
		settings.animateButton.hide();
		settings.toggleChartTypeButton.hide();
	}
	return;
}

function tapinosChart_injectNoData(component, data) {
	var html = "<p class='tapinosChart-noData'>No existen datos</p>";
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
	if (settings.seriesVar != undefined) {
		params.seriesVar = tapinosCommon_getStringParameter(settings.seriesVar);
	}
	if (settings.seriesValues != undefined) {
		//params.seriesValues = tapinosCommon_getArrayParameter(settings.seriesValues);
		params.seriesValues =  tapinosCommon_getArrayParameter(settings.dimensionConstraints);
	}
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
        		if (settings.doNotPaintChart == false) {
	        		if (data.series.length == 0 || (data.series.length == 1 && data.series[0].points.length == 0)) {
	        			tapinosChart_injectNoData(component, data);
	        			settings.maximizeButton.hide();
	        			settings.toggleChartTypeButton.hide();
	        		} else if (data.valueVar == undefined || (data.series.length == 1 && data.series[0].points.length == 1)) {
	        			tapinosChart_injectBigNumber(component, data);
	        			settings.maximizeButton.hide();
	        			settings.toggleChartTypeButton.hide();
	        		} else {
						if (settings.chartService == "highcharts"){
							tapinosChart_paintHighChartsChart(component, data);
							settings.maximizeButton.show();
						} else { //default google charts
							if (settings.chartService != "google"){
								//TODO: add log
							}
							
							tapinosChart_paintGoogleChart(component, data);
	
							$(window).bind("resize", function(event) {
								tapinosChart_setGraphSizeGoogleChart(component);
								tapinosChart_drawGoogleChart(component);
							});
														
						}

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
// Maximization
//********************************************************

function tapinosChart_initializeMaximization(component) {
	var settings = component.data("tapinosChartSettings");
    settings.maximizeButton.fancybox({
		"autoDimensions": false, "width": "95%", "height": "95%",
		titleShow: false,
		onComplete: tapinosChart_onCompleteMaximizationFunc(component),
		onClosed: tapinosChart_onClosedMaximizationFunc(component),
    });
}

function tapinosChart_onCompleteMaximizationFunc(component) {
	return function() {
		var settings = component.data("tapinosChartSettings");
	    var chart = component.data("highchartsChart");
	    var maximizeOptions = jQuery.extend(true, null, chart.options);
	    if ( maximizeOptions.chart.defaultSeriesType=='bar' ) {
	    // Si son barras horizontales le damos mÃ¡s margen izquierdo
	    	maximizeOptions.chart.marginLeft = 250;
      		maximizeOptions.xAxis.title.margin= 195;
	    }
	    settings.maximizeContainer.height('100%');       
	    maximizeOptions.height = null;
	    maximizeOptions.width = null;
	    maximizeOptions.chart.renderTo = settings.maximizeContainer.attr("id");
	    maximizeOptions.plotOptions.series.animation.duration = 1000;
	    maximizeOptions['legend']['itemStyle']['fontSize'] = '10px';
	    maximizeOptions['legend']['itemHiddenStyle']['fontSize'] = '10px';
	    maximizeOptions['legend']['itemHoverStyle']['fontSize'] = '10px';
	  	maximized_chart = new Highcharts.Chart(maximizeOptions);
	  	component.data("highchartsMaximizedChart", maximized_chart);	
	};
}

function tapinosChart_onClosedMaximizationFunc(component) {
	return function() {
		var settings = component.data("tapinosChartSettings");
	    var maximized_chart = component.data("highchartsMaximizedChart");
      	maximized_chart.destroy();
      	settings.maximizeContainer.height('0');
	};	
}

//********************************************************
//Animation
//********************************************************

function tapinosChart_initializeAnimation(component) {
	var settings = component.data("tapinosChartSettings");
    settings.animateButton.click(function() {
		var settings = component.data("tapinosChartSettings");
	    var options = component.data("options");
	    options.chart.renderTo = component.attr("id");
	    options.plotOptions.series['animation']['duration'] = 5000;
	    var chart = component.data("highchartsChart");
	    chart.destroy();
	    chart = new Highcharts.Chart(options);
	    component.data("highchartsChart", chart);
      });
}

//********************************************************
//Toggle chart type
//********************************************************

function tapinosChart_initializeToggleChartType(component) {
	var settings = component.data("tapinosChartSettings");
    settings.toggleChartTypeButton.click(function() {
		var settings = component.data("tapinosChartSettings");
	    var chart = component.data("highchartsChart");
      	if ( chart.options.chart.defaultSeriesType=='column' ) {
      		var columnOptions = chart.options;
      		chart.destroy();
      		columnOptions.chart.renderTo = component.attr("id");
			columnOptions.chart.defaultSeriesType='bar';
      		columnOptions.chart.marginLeft = 100;
      		// Al cambiar a tipo bar se permutan los ejes (ahora X es el eje vertical)
      		columnOptions.xAxis.title.margin= 80;
      		chart = new Highcharts.Chart(columnOptions);      		
      	} else if ( chart.options.chart.defaultSeriesType=='bar' ) {
      		var barOptions = chart.options;
      		chart.destroy();
      		barOptions.chart.renderTo = component.attr("id");
      		barOptions.chart.defaultSeriesType='column';
      		barOptions.chart.marginLeft = 70;
      		barOptions.xAxis.title.margin= 5;
      		barOptions.xAxis.title.align= "middle";
      		chart = new Highcharts.Chart(barOptions);
      	}
      	component.data("highchartsChart", chart);
      	$('.highcharts-legend text').tooltip({ 
	    		bodyHandler: function() { 
	        		return $($(this).find('tspan')[1]).text(); 
	    		}, 
	    		showURL: false 
			});
		$('.highcharts-axis text').tooltip({ 
	    		bodyHandler: function() { 
	        		return $($(this).find('tspan')[1]).text(); 
	    		}, 
	    		showURL: false 
			});	    
      });
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
	} else { // DEPRECATED
		settings.numberSuffix = settings.bigNumberSuffix;
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
	if (settings.toggleChartTypeButton == undefined) {
		settings.toggleChartTypeButton = $([]);
	}
	if (settings.animateButton == undefined) {
		settings.animateButton = $([]);
	}
	if (settings.maximizeButton == undefined) {
		settings.maximizeButton = $([]);
	}
	if (settings.doNotPaintChart == undefined) {
		settings.doNotPaintChart = false;
	}
	if (settings.chartService == undefined) {
		settings.chartService = "google";
	}
}


//********************************************************
//Google Charts Component
//********************************************************
var time = 0;
//Load the Visualization API and the piechart package.
google.load('visualization', '1.0', {'packages':['corechart']});


function tapinosChart_paintGoogleChart(element, data) {

	  var chart = new google.visualization.ColumnChart(document.getElementById(element.attr('id')));
	  if ( data.chartType=='LINES' ) {
		  chart = new google.visualization.LineChart(document.getElementById(element.attr('id')));
	  } else if ( data.chartType=='PIE' ) {
		  chart = new google.visualization.PieChart(document.getElementById(element.attr('id')));
		  time = 1000;
	  }
	  
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
	            duration: 500,
	            easing: 'linear',
            },
            
            //is3D is an options for Google PieChart
            is3D: true,
            
            //interpolateNulls is an option for Google LineChart
            interpolateNulls: true,

        };

		if (data.chartType=='PIE')
		{
			options.title = data.title;
		}
      isMobile = false;
      var a = navigator.userAgent;
      if (/android.+mobile|avantgo|bada\/|blackberry|blazer|compal|elaine|fennec|hiptop|iemobile|ip(hone|od)|iris|kindle|lge |maemo|midp|mmp|netfront|opera m(ob|in)i|palm( os)?|phone|p(ixi|re)\/|plucker|pocket|psp|symbian|treo|up\.(browser|link)|vodafone|wap|windows (ce|phone)|xda|xiino/i.test(a)|| /1207|6310|6590|3gso|4thp|50[1-6]i|770s|802s|a wa|abac|ac(er|oo|s\-)|ai(ko|rn)|al(av|ca|co)|amoi|an(ex|ny|yw)|aptu|ar(ch|go)|as(te|us)|attw|au(di|\-m|r |s )|avan|be(ck|ll|nq)|bi(lb|rd)|bl(ac|az)|br(e|v)w|bumb|bw\-(n|u)|c55\/|capi|ccwa|cdm\-|cell|chtm|cldc|cmd\-|co(mp|nd)|craw|da(it|ll|ng)|dbte|dc\-s|devi|dica|dmob|do(c|p)o|ds(12|\-d)|el(49|ai)|em(l2|ul)|er(ic|k0)|esl8|ez([4-7]0|os|wa|ze)|fetc|fly(\-|_)|g1 u|g560|gene|gf\-5|g\-mo|go(\.w|od)|gr(ad|un)|haie|hcit|hd\-(m|p|t)|hei\-|hi(pt|ta)|hp( i|ip)|hs\-c|ht(c(\-| |_|a|g|p|s|t)|tp)|hu(aw|tc)|i\-(20|go|ma)|i230|iac( |\-|\/)|ibro|idea|ig01|ikom|im1k|inno|ipaq|iris|ja(t|v)a|jbro|jemu|jigs|kddi|keji|kgt( |\/)|klon|kpt |kwc\-|kyo(c|k)|le(no|xi)|lg( g|\/(k|l|u)|50|54|e\-|e\/|\-[a-w])|libw|lynx|m1\-w|m3ga|m50\/|ma(te|ui|xo)|mc(01|21|ca)|m\-cr|me(di|rc|ri)|mi(o8|oa|ts)|mmef|mo(01|02|bi|de|do|t(\-| |o|v)|zz)|mt(50|p1|v )|mwbp|mywa|n10[0-2]|n20[2-3]|n30(0|2)|n50(0|2|5)|n7(0(0|1)|10)|ne((c|m)\-|on|tf|wf|wg|wt)|nok(6|i)|nzph|o2im|op(ti|wv)|oran|owg1|p800|pan(a|d|t)|pdxg|pg(13|\-([1-8]|c))|phil|pire|pl(ay|uc)|pn\-2|po(ck|rt|se)|prox|psio|pt\-g|qa\-a|qc(07|12|21|32|60|\-[2-7]|i\-)|qtek|r380|r600|raks|rim9|ro(ve|zo)|s55\/|sa(ge|ma|mm|ms|ny|va)|sc(01|h\-|oo|p\-)|sdk\/|se(c(\-|0|1)|47|mc|nd|ri)|sgh\-|shar|sie(\-|m)|sk\-0|sl(45|id)|sm(al|ar|b3|it|t5)|so(ft|ny)|sp(01|h\-|v\-|v )|sy(01|mb)|t2(18|50)|t6(00|10|18)|ta(gt|lk)|tcl\-|tdg\-|tel(i|m)|tim\-|t\-mo|to(pl|sh)|ts(70|m\-|m3|m5)|tx\-9|up(\.b|g1|si)|utst|v400|v750|veri|vi(rg|te)|vk(40|5[0-3]|\-v)|vm40|voda|vulc|vx(52|53|60|61|70|80|81|83|85|98)|w3c(\-| )|webc|whit|wi(g |nc|nw)|wmlb|wonu|x700|xda(\-|2|g)|yas\-|your|zeto|zte\-/i.test(a.substr(0, 4))) 
    	  	isMobile =  true;
      
      if (isMobile){
    	  options.hAxes[0].textPosition = 'in';
    	  options.vAxes[0].textPosition = 'in';
      }
      
      $(element).data("options", options);
      $(element).data("GoogleChart", chart);

      isMobile ? tapinosChart_dataGoogleChartMobile (element, data) : tapinosChart_dataGoogleChart(element, data);   
}

function tapinosChart_drawGoogleChart(element) {
	view = new google.visualization.DataView(dataTable);
	element.data("GoogleChart").draw(view, element.data("options"));
}
 
function tapinosChart_setGraphSizeGoogleChart(element) {
	//Cambiar los valores
	element.height((59*$(window).height())/100);
	element.width((59 *$(window).width())/100);
}

/*************************************************DATOS**************************************************************/
function tapinosChart_dataGoogleChart (element, data){
	dataTable = new google.visualization.DataTable();

	dataTable.addColumn('string', data.xLabel);

	tapinosChart_pushColumnsGoogleChart(dataTable, data);
      
    if (time == 0 )
   		time = 250;
    
    //initial value for row count is 0
	tapinosChart_animatebypointsGoogleChart (0, element, dataTable, time, data);
	
	//TODO: review selectHandler function in other txt file
	//google.visualization.events.addListener(element.data("GoogleChart"), 'select', selectHandler(element, data, dataTable));

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


function tapinosChart_animatebyseriesGoogleChart(c, element, dataTable, time, data){

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
  		tapinosChart_setGraphSizeGoogleChart(element);
  		tapinosChart_drawGoogleChart(element);
  		
  		if (c < data.series.length-1){
  			 window.setTimeout(function(){
  				tapinosChart_animatebyseriesGoogleChart (c+1, element, dataTable, time, data);
             },time);
  		}	
}

function tapinosChart_animatebypointsGoogleChart (cont, element, dataTable, time, data){
		
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
		tapinosChart_setGraphSizeGoogleChart(element);
		tapinosChart_drawGoogleChart(element);
		
		if (cont < data.sortedX.length-1){
			 window.setTimeout(function(){
				 tapinosChart_animatebypointsGoogleChart (cont+1, element, dataTable, time, data);
         },time);
		}
	
}
/************************************************* FIN DATOS ************************************************************/

  
/******************************************************MOVIL**************************************************************/
  function tapinosChart_dataGoogleChartMobile (element, data){
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
		tapinosChart_setGraphSizeGoogleChart(element);
		tapinosChart_drawGoogleChart(element);
}
  
/**************************************************FIN MOVIL**************************************************************/
//********************************************************
//JQuery Component 
//********************************************************

(function($) {
 $.fn.tapinosChart = function(settings) {
	  tapinosChart_setDefaultSettings(settings);
     this.each(function() {
   	var component = $(this);
       $(this).data("tapinosChartSettings", settings);
       tapinosChart_initializeMaximization($(this));
       tapinosChart_initializeAnimation($(this));
       tapinosChart_initializeToggleChartType($(this));
    });
    return this;
  };

 $.fn.tapinosChartDraw = function(chartConfig) {
    this.each(function() {
		var settings = $(this).data("tapinosChartSettings");
		if (settings == undefined) {
			alert('El componente de gráficos no está inicializado');
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
