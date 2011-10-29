<html>
 
  <head> 
    <title>Tabels SPARQL Query</title>
    <meta name="layout" content="main" />
  </head> 

  <body> 
  
    <h2>SPARQL Results (${size})</h2>
    
   	<p style="text-align: right;">
   		Go back to the <g:link action="form">SPARQL form</g:link>
   	</p>    
  
    <table class="sparql" border="1">
      <thead>
        <tr>
        <g:each var="var" in="${vars}">
          <th scope="col" id="${var}">${var}</th>
        </g:each>
        </tr>
      </thead>
      <tbody>
        <g:each var="tuple" in="${tuples}">
        <tr>
          <g:each var="var" in="${vars}">
            <td headers="${var}">${tuple.get(var)}</td>
          </g:each>
        </tr>
        </g:each>
      </tbody>
    </table>
    
   	<p style="text-align: right;">
   		Go back to the <a href="form">SPARQL form</a>
   	</p>
   	
   	<script type="text/javascript" src="../js/jquery-1.5.2.js"></script> 
   	<script type="text/javascript" src="../js/jquery.autolink.js"></script> 
   	
   	<script type="text/javascript"> 
   	
   		$(document).ready(function() {
   			$("table.sparql").autolink();
   		});
   	
   	</script>
    
  </body> 

</html>
