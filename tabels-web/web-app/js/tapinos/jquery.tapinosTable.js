// Tapinos Table component

/*
 * The following two functions were taken from http://www.datatables.net/plug-ins/sorting
 * in order to allow sorting of percentages (use "sType": "percent")
 */

jQuery.fn.dataTableExt.oSort['percent-asc']  = function(a,b) {
	var x = (a == "-") ? 0 : a.replace( /%/, "" );
	var y = (b == "-") ? 0 : b.replace( /%/, "" );
	x = parseFloat( x );
	y = parseFloat( y );
	return ((x < y) ? -1 : ((x > y) ?  1 : 0));
};

jQuery.fn.dataTableExt.oSort['percent-desc'] = function(a,b) {
	var x = (a == "-") ? 0 : a.replace( /%/, "" );
	var y = (b == "-") ? 0 : b.replace( /%/, "" );
	x = parseFloat( x );
	y = parseFloat( y );
	return ((x < y) ?  1 : ((x > y) ? -1 : 0));
};

/*
 * The following two functions were taken from the same source in
 * order to allow sorting of formatted numbers
 */

jQuery.fn.dataTableExt.oSort['formatted-num-asc'] = function(x,y){
	 x = x.replace(/[^\d\-\.\/]/g,'');
	 y = y.replace(/[^\d\-\.\/]/g,'');
	 if(x.indexOf('/')>=0)x = eval(x);
	 if(y.indexOf('/')>=0)y = eval(y);
	 return x/1 - y/1;
};

jQuery.fn.dataTableExt.oSort['formatted-num-desc'] = function(x,y){
	 x = x.replace(/[^\d\-\.\/]/g,'');
	 y = y.replace(/[^\d\-\.\/]/g,'');
	 if(x.indexOf('/')>=0)x = eval(x);
	 if(y.indexOf('/')>=0)y = eval(y);
	 return y/1 - x/1;
};

/*
 * These functions are usually the last choice
 */

jQuery.fn.dataTableExt.oSort['digits-only-asc'] = function(x,y){
	 x = x.replace(/[^\d\-\.\/]/g,'');
	 y = y.replace(/[^\d\-\.\/]/g,'');
	 if(x.indexOf('/')>=0)x = eval(x);
	 if(y.indexOf('/')>=0)y = eval(y);
	 return x/1 - y/1;
};

jQuery.fn.dataTableExt.oSort['digits-only-desc'] = function(x,y){
	 x = x.replace(/[^\d\-]/g,'');
	 y = y.replace(/[^\d\-]/g,'');
	 if(x.indexOf('/')>=0)x = eval(x);
	 if(y.indexOf('/')>=0)y = eval(y);
	 return y/1 - x/1;
};


function tapinosTable_getTableBody(data, settings) {
  var rows = [];
  for(seriesIndex in data.series) {
    var row = new Array(data.sortedX.length+1);
    for (var colIndex = 1; colIndex < data.sortedX.length + 1; colIndex++) {
    		row[colIndex] = "-"; // default value
    }
    if(data.series[0].seriesLabel != null) {
      row[0] = data.series[seriesIndex].seriesLabel;
    } else {
    	  row[0] = "";
    }
    var series = data.series[seriesIndex];
    for(pointIndex in series.points) {
    	  var point = series.points[pointIndex];
    	  var formattedValue = tapinosCommon_formatPoint(point, series, data, settings);
    	  var colIndex = data.sortedX.indexOf(point.x) + 1;
      row[colIndex] = formattedValue;
    }
    rows.push(row);
  }
  return rows;
}

function tapinosTable_getTableHeaders(data, settings) {
  var headers = [];

  if(data.series.length > 1) {
    headers.push({"sTitle": data.seriesVar.label != null ? data.seriesVar.label : "Series"});
  } else if (data.title != undefined) {
	  headers.push({"sTitle": data.title});
  } else {
	  headers.push("");
  }
  for(index in data.sortedX) {
	  var column = { "sTitle": data.sortedX[index], "sClass": "center" };
	  if (settings.sType != undefined) {
		  column["sType"] = settings.sType;
	  }
	  headers.push(column);
  }
  return headers;
}

function tapinosTable_setDefaultSettings(settings) {
	if (settings.decimalDigits == undefined) {
		settings.decimalDigits = 2;
	}
	if (settings.numberSuffix == undefined) {
		settings.numberSuffix = "";
	}
}

// ******************************************************************

(function($) {
	$.fn.tapinosTableDraw = function(data, settings) {
		tapinosTable_setDefaultSettings(settings);
     divLoading($(this));
     this.each(function() {
        divLoading($(this));
        $(this).html('<table cellpadding="0" cellspacing="0" border="0" class="display" id="tapinosTable"></table>');
        $("> table", $(this)).dataTable({
          "bDestroy": true,
          "bFilter": false,
          "bInfo": false,
          "bPaginate": false,
          "aaData": tapinosTable_getTableBody(data, settings),
          "aoColumns": tapinosTable_getTableHeaders(data, settings),
          });
     });
     return this;
   };
 
 })(jQuery);
