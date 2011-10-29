<html>

  <head>
    <title>SPARQL endpoint</title>
    <meta name="layout" content="main" />
    <script type="text/javascript">
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
          format.options[2] = new Option('HTML','text/html');          
          format.selectedIndex = 2;
        }
      }
    </script>
  </head>
  
  <body>

    <h2>SPARQL endpoint</h2>

    <g:form method="post" name="sparqlQueryForm">
      <fieldset class="sparql">
        <legend>Query</legend>
        <p>
          <label for="query">Query text:</label>
          <g:textArea rows="10" cols="80" name="query" value="${query}" id="query" onchange="format_select(this)" onkeyup="format_select(this)" />
        </p>
          
        <p style="clear:both;">
          <label for="format" class="n">Return results as:</label>
          <select name="format" id="format">
            <option value="application/sparql-results+xml">XML</option>
            <option value="application/sparql-results+json">JSON</option>
            <option value="text/html" selected="selected">HTML</option>
          </select>
        </p>
          
        <p style="clear:both;">
          <g:actionSubmit value="Query" action="query" id="queryButton" />
        </p>              
      </fieldset>
    </g:form>
            
  </body>
    
</html>
