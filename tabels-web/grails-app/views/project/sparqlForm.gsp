<html>
  <head>
    <title><g:message code="msg.sparql.form.title"/></title>
	<r:require modules="codemirror"/>
    <meta name="layout" content="main" />
    <r:script>
      var last_format = 1;
      function format_select(query_obg) {
        var query = query_obg.value; 
        var format = query_obg.form.format;
      
        if ((query.match(/construct/i) || query.match(/describe/i))) {
          for(var i = format.options.length; i > 0; i--) { format.options[i] = null; }
          format.options[0] = new Option('RDF/XML','application/rdf+xml');
          format.options[1] = new Option('N3','text/rdf+n3');
          format.options[2] = new Option('Turtle','application/x-turtle');
          format.selectedIndex = 0;
        }

        if (!(query.match(/construct/i) || query.match(/describe/i))) {
          for(var i = format.options.length; i > 0; i--) { format.options[i] = null; }
          format.options[0] = new Option('XML','application/sparql-results+xml');
          format.options[1] = new Option('JSON','application/sparql-results+json');
          format.options[2] = new Option('CSV','text/csv');          
          format.options[3] = new Option('TSV','text/tab-separated-fields');          
          format.options[4] = new Option('HTML','text/html');          
          format.selectedIndex = 4;
        }
      }

		$(document).ready(function() {
			CodeMirror.fromTextArea(query, {
				mode: "sparql",
				lineNumbers: true,
				matchBrackets: true
			});
		});
    </r:script>
  </head>
  
  <body>
    <ul id="crumbs" class="crumbsBlock">
        <li><g:link action="list" id="${params.id}">Home</g:link></li>
        <li><g:link action="list" id="${params.id}">Projects</g:link></li>
        <g:if test="${params.id}">
          <li><g:link action="index" id="${params.id}">Project ${params.id}</g:link></li>
        </g:if>
        <li><g:message code="msg.sparql.form.title"/></li>
    </ul>
    <!--<h2><g:message code="msg.sparql.form.title"/></h2>

	<p class="backLink">
	    <g:if test="${params.id}">
	        <g:link action="index" id="${params.id}"><g:message code="msg.back.to.project.link"/></g:link>
            <g:set var="formMapping" value="projectSpecific"/>
	    </g:if>
	    <g:else>
	        <g:link action="list"><g:message code="msg.back.to.project.list.link"/></g:link>
            <g:set var="formMapping" value="globalSparql"/>
	    </g:else>
	</p>-->

    <g:form method="post" name="sparqlQueryForm" mapping="${formMapping}" id="${params.id}">
		<g:if test="${namedGraphs.size() > 0}">
	        <p><g:message code="msg.sparql.available.named.graphs"/></p>
	        <ul>
	            <g:each in="${namedGraphs}" var="namedGraph">
	                <li>${namedGraph}</li>
	            </g:each>
	        </ul>
		</g:if>
    
        <p>
          <label for="query"><g:message code="form.sparql.query.text"/></label>
          <g:textArea rows="10" cols="80" name="query" value="${query}" id="query" onchange="format_select(this)" onkeyup="format_select(this)" />
        </p>
          
        <p style="clear:both;">
          <label for="format" class="n"><g:message code="form.sparql.result.format"/></label>
          <select name="format" id="format" class="inputbutton white medium">
            <option value="application/sparql-results+xml">XML</option>
            <option value="application/sparql-results+json">JSON</option>
            <option value="text/csv">CSV</option>
            <option value="text/tab-separated-fields">TSV</option>
            <option value="text/html" selected="selected">HTML</option>
          </select>
        </p>
          
        <p style="clear:both;">
            <g:actionSubmit value="${message(code: 'form.sparql.query.button')}" action="sparql" id="queryButton" class="inputbutton white medium"/>
        </p>              
    </g:form>
            
  </body>
    
</html>
