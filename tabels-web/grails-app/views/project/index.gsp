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
		
		<p><g:link action="rdf">Download RDF</g:link></p>
		<p><g:link action="sparqlForm">SPARQL query</g:link></p>
		
		<g:form action="saveProgram">
			<g:textArea name="program" value="${program}" rows="15" cols="80" />
			<p><g:submitButton name="save" value="Save program" /></p>
		</g:form>

    </body>
</html>
