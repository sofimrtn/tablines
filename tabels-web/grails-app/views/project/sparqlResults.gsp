<html>
 
  <head> 
    <title><g:message code="msg.sparql.results.title" args="${[size]}"/></title>
    <meta name="layout" content="main" />
  </head> 

  <body> 
      <ul id="crumbs" class="crumbsBlock">
        <li><g:link action="home">Home</g:link></li>
        <li><g:link action="list" id="${params.id}">Projects</g:link></li>
        <g:if test="${params.id}">
          <li><g:link action="index" id="${params.id}">Project ${params.id}</g:link></li>
        </g:if>
        <li><g:link action="sparql" params="[forceForm: '1', query: query]" id="${params.id}"><g:message code="msg.sparql.form.title"/></g:link></li>
        <li><g:message code="msg.sparql.results.title" args="${[size]}"/></li>
    </ul>
    <!--<h2><g:message code="msg.sparql.results.title" args="${[size]}"/></h2>
    
   	<p class="backLink"><g:link action="sparql" params="[forceForm: '1', query: query]" id="${params.id}"><g:message code="msg.sparql.back.to.form"/></g:link></p>-->     
   	
   	<p class="permaLink"><g:link action="sparql" params="[query: query]" id="${params.id}"><g:message code="msg.sparql.permalink"/></g:link></p>
   	
   	<p class="csvDownload"><g:link action="sparql" params="[query: query, format: 'text/csv']" id="${params.id}"><g:message code="msg.sparql.download.csv.link"/></g:link></p>
                               
    <!-- <p><g:message code="msg.sparql.result.count" args="[tuples.size()]"/></p> -->
    
    <table class="sparql">
      <thead>
        <tr>   
          <th scope="col">#</th>
        <g:each var="var" in="${vars}">
          <th scope="col" id="${var}">${var}</th>
        </g:each>
        </tr>
      </thead>
      <tbody>
        <g:each var="tuple" in="${tuples}" status="i">
        <tr class="${(i % 2 == 0) ? 'even' : 'odd'}"> 
            <td>${i+1}</td>
          <g:each var="var" in="${vars}">
            <td headers="${var}">
              <g:showRdfNode rdfNode="${tuple.get(var)}"/>
            </td>
          </g:each>
        </tr>
        </g:each>
      </tbody>
    </table>
    
   	<!--
   	<script type="text/javascript" src="../js/jquery.autolink.js"></script> 
   	
   	<script type="text/javascript"> 
   	
   		$(document).ready(function() {
   			$("table.sparql").autolink();
   		});
   	
   	</script>
   	-->
    
  </body> 

</html>
