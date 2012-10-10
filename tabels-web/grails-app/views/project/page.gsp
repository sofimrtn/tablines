<html>
    <head>
        <title>About: ${currentResource}</title>
        <meta name="layout" content="main" />	   
    </head>
    <body>
        <ul id="crumbs">
			<li><g:link action="home">Home</g:link></li>
			<li><g:link action="list">Projects</g:link></li>
			<li><g:link action="index" id="${id}">Project ${id}</g:link></li>
			<li>About </li>
		</ul>
        <h2>About: <a href="${currentResource.getURI().replaceAll('#','%23')}">${currentResource}</a></h2>

		<table id="resource-table">
		<g:each in="${directStatements}" var="statement">
			<tr>
				<td><g:showRdfNode rdfNode="${statement.predicate}"/></td>
				<td><g:showRdfNode rdfNode="${statement.object}" /></td>
			</tr>
		</g:each>
		<g:each in="${inverseStatements}" var="statement">
			<tr>
				<td>is <g:showRdfNode rdfNode="${statement.predicate}"/> of</td>
				<td><g:showRdfNode rdfNode="${statement.subject}"/></td>
			</tr>
		</g:each>		
		</table>

    </body>
</html>
