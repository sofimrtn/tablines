<html>
    <head>
        <title>Tabels project</title>
        <meta name="layout" content="main" />
    </head>
    <body>
		<h1>Tabels project</h1>

		<uploadr:add name="myFirstUploadr" path="${path}">
			<% path.listFiles().each { file -> %>
				<uploadr:file name="${file.name}">
					<uploadr:fileSize>${file.size()}</uploadr:fileSize>
					<uploadr:fileModified>${file.lastModified()}</uploadr:fileModified>
					<uploadr:fileId>myId-${org.apache.commons.lang.RandomStringUtils.random(32, true, true)}</uploadr:fileId>
					<g:if test="${file.name.endsWith('.tabels')}">
						<uploadr:color>#c78cda</uploadr:color>
					</g:if>
				</uploadr:file>
			<% } %>
		</uploadr:add>
		
		<g:link action="rdf">Download RDF</g:link>
		<g:link action="form">SPARQL query</g:link>

    </body>
</html>
