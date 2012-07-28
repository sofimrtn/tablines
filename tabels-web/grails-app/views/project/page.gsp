<html>
    <head>
        <title>Description of ${currentResource}</title>
        <meta name="layout" content="main" />	   
    </head>
    <body>

        <h2>Description of ${currentResource}</h2>

		<table>
		<g:each in="${directStatements}" var="statement">
			<tr>
				<td>${statement.predicate}</td>
				<td>${statement.object}</td>
			</tr>
		</g:each>
		<g:each in="${inverseStatements}" var="statement">
			<tr>
				<td>is ${statement.predicate} of</td>
				<td>${statement.subject}</td>
			</tr>
		</g:each>		
		</table>

    </body>
</html>
