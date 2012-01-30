<html>
 
  <head> 
    <title>Tabels SPARQL Query Results</title>
    <meta name="layout" content="main" />
  </head> 

  <body> 
  
    <h2>SPARQL Results (${size})</h2>
    
   	<p style="text-align: right;">
   		Go back to the <g:link action="sparql">SPARQL form</g:link>
   	</p>    
  
    <table class="sparql">
      <thead>
        <tr>
        <g:each var="var" in="${vars}">
          <th scope="col" id="${var}">${var}</th>
        </g:each>
        </tr>
      </thead>
      <tbody>
        <g:each var="tuple" in="${tuples}" status="i">
        <tr class="${(i % 2 == 0) ? 'even' : 'odd'}">
          <g:each var="var" in="${vars}">
            <td headers="${var}">
                <g:if test="${tuple.get(var).startsWith('http://')}">
                    <a href="${tuple.get(var)}">${tuple.get(var)}</a>
                </g:if>
                <g:else>
                    ${tuple.get(var)}
                </g:else>
            </td>
          </g:each>
        </tr>
        </g:each>
      </tbody>
    </table>
    
   	<p style="text-align: right;">
   		Go back to the <a href="form">SPARQL form</a>
   	</p>
   	
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
