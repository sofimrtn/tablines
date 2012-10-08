<html>
<head>
	<title>Chart view</title>
    <meta name="layout" content="main" />
    <r:require modules="tapinos-js,fancybox,protovis,highcharts,jquery-tipsy,jquery-datatables,jquery-geturlparam,jquery-tooltip" />
    <link rel="stylesheet" href="${resource(dir:'css',file:'tapinos.css')}" />
</head>
<body>
    <ul id="crumbs" class="crumbsBlock">
        <li><g:link action="home">Home</g:link></li>
        <li><g:link action="list" id="${params.id}">Projects</g:link></li>
        <li><g:link action="index" id="${params.id}">Project ${params.id}</g:link></li>
        <li>Chart view</li>
    </ul>
    <script type="text/javascript" src="https://www.google.com/jsapi"></script>
	<r:script>
       function getCurrentDataset() {
         return $("#dataset > option:selected").val();
       }    

       $(document).ready(function() {
  // initialize(); //only for googleMaps
         $("#dataset").each(function() { 
           $(this).bind('change', function() {
             $("#dimensions").tapinosCombosReload(getCurrentDataset());
           });
         });

   $("#GooglechartsChart").tapinosChart({
           // jQuery object: a Tapinos Table to refresh (opt)
           ws: "<g:resource dir='ws' file='chart'/>",
           endpoint: "${endpoint}",
           namedgraph: "${namedgraph}",
           table: $("#table"),
   chartService: "google",
         });

        

         $("#dimensions").tapinosCombos({
           // String: Dimension Web Service URI
           ws: "<g:resource dir='ws' file='dimensions'/>",
           endpoint: "${endpoint}",
           namedgraph: "${namedgraph}",
           // jQuery object: A div to insert error messages
           errorMsgsDiv: $("#errors"),
           // jQuery object: A checkbox to switch values/series variables (opt)
           //seriesSwitchCheckbox: $("#seriesSwitchCheckbox"),
           seriesSwitchDiv: $("#seriesSwitchDiv"),
           // Array of jQuery objects to empty() if failure {userError, networkError} (opt)
           toClearIfFailure: $("#table"), //TODO: checking if googlecharts should be added
           URLFieldSavedState: "tapinosCombos",
           // Callback to execute on dimension change events
           // (Combos --> Chart --> Table)
           callback: function(chartParameters) { 
               // chartParameters.yLabel = "Porcentaje (%)";
   $("#GooglechartsChart").tapinosChartDraw(chartParameters); 
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
         //            'tapinosCombos': function () {
         //                return $("#dimensions").tapinosCombosGetState()[0].toSource();
         //              },
         //            'dataset': function() {
         //                return getCurrentDataset().toSource();
         //            }});
       });
</r:script>
            
	<!--<h2>Chart view</h2>
	
	<p class="backLink">
	   <g:link action="index" id="${params.id}"><g:message code="msg.back.to.project.link"/></g:link>
	</p>-->
	
	<g:if test="${datasets}">
	
    	<h3>Seleccione los datos</h3>

        <p><label for="dataset">Conjunto de datos:</label>

        <select id="dataset" name="dataset" onchange="javascript:onDatasetChange(event)" class="inputbutton white medium">
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

        <div id="GooglechartsChart">
        </div>

        <h3>Visualización en forma de tabla</h3>
        <div id="table">
        </div>
    </g:if>
    <g:else>
        <div class="messagebox"><p><g:message code="msg.why.nothing.in.tapinos.msg"/></p></div>
    </g:else>

</body>
</html>