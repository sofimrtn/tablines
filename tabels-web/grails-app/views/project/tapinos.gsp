<html>
<head>
	<title>Chart view</title>
    <meta name="layout" content="main" />
    <r:require modules="tapinos-js,fancybox,protovis,highcharts,jquery-tipsy,jquery-datatables,jquery-geturlparam,jquery-tooltip" />
</head>
<body>
       
	<r:script>
	
    function getCurrentDataset() {
      return $("#dataset > option:selected").val();
    }	

    $(document).ready(function() {
      $("#dataset").each(function() { 
        $(this).bind('change', function() {
          $("#dimensions").tapinosCombosReload(getCurrentDataset());
        });
      });

      $("#chart").tapinosChart({
        // jQuery object: a Tapinos Table to refresh (opt)
        ws: "../ws/chart",
        table: $("#table"),
      });

      $("#dimensions").tapinosCombos({
        // String: Dimension Web Service URI
        ws: "../ws/dimensions",
        // jQuery object: A div to insert error messages
        errorMsgsDiv: $("#errors"),
        // jQuery object: A checkbox to switch values/series variables (opt)
        //seriesSwitchCheckbox: $("#seriesSwitchCheckbox"),
        seriesSwitchDiv: $("#seriesSwitchDiv"),
        // Array of jQuery objects to empty() if failure {userError, networkError} (opt)
        toClearIfFailure: $("#chart, #table"),
        URLFieldSavedState: "tapinosCombos",
        // Callback to execute on dimension change events
        // (Combos --> Chart --> Table)
        callback: function(chartParameters) { 
            chartParameters.yLabel = "Porcentaje (%)";
            $("#chart").tapinosChartDraw(chartParameters); 
            $(".permalink").tapinosPermalinkRefresh();
            return; 
            }
      });

      $(".permalink").tapinosPermalink({
          selects: $("#dataset"),
          tapinosCombos: $("#dimensions"),
      });

      // Populate initial values
      $("#dimensions").tapinosCombosReload(getCurrentDataset());

      // Link builder
      function tapinosCombosState() {
         return $("#dimensions").tapinosCombosGetState()[0].toSource();
      }

      function datasetState() {
          return getCurrentDataset().toSource();
      }

      //var linkBuilder = new TapinosPermLinkBuilder({
      //            'tapinosCombos': function () {
      //                return $("#dimensions").tapinosCombosGetState()[0].toSource();
      //              },
      //            'dataset': function() {
      //                return getCurrentDataset().toSource();
      //            }});
    });
	</r:script>
            
	<h2>Chart view</h2>
	
	<p class="backLink"><g:link action="index"><g:message code="msg.back.to.project.link"/></g:link></p>
	
	<h3>Seleccione los datos</h3>

<p><label for="dataset">Conjunto de datos:</label>

<select id="dataset" name="dataset" onchange="javascript:onDatasetChange(event)">
<g:each var="dataset" in="${datasets}">
  <option value="${dataset.uri}">${dataset.label}</option>
</g:each>
</select>  	

</p>

<fieldset>
	<legend>Seleccione los valores a consultar</legend>
	<div id="dimensions">
	</div>
  	<div id="errors"></div>
</fieldset>

<div id="seriesSwitchDiv"></div>

<h3>Visualización gráfica</h3>

<div id="chart">
</div>

<h3>Visualización en forma de tabla</h3>
<div id="table">
</div>

</body>
</html>