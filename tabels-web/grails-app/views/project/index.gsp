<html>
    <head>
        <title>Tabels project</title>
        <meta name="layout" content="main" />
	    <script type="text/javascript">
			$(document).ready(function() {
				CodeMirror.fromTextArea(program, {
					mode: "tabels",
					lineNumbers: true,
					matchBrackets: true
				});
			});
	    </script>
    </head>
    <body>
		<h2>Files</h2>

		<uploadr:add name="myFirstUploadr" path="${path}">
			<g:each in="${path.listFiles()}" var="file"> <!-- FIXME: use ${files} -->
				<uploadr:file name="${file.name}">
					<uploadr:fileSize>${file.size()}</uploadr:fileSize>
					<uploadr:fileModified>${file.lastModified()}</uploadr:fileModified>
					<uploadr:fileId>myId-${org.apache.commons.lang.RandomStringUtils.random(32, true, true)}</uploadr:fileId>
					<g:if test="${file.name.endsWith('.tabels')}">
						<uploadr:color>#ffffcc</uploadr:color>
					</g:if>
				</uploadr:file>
			</g:each>
		</uploadr:add>
		
		<p class="rdfDownloadLink"><g:link action="rdf">Download RDF</g:link></p>
		<p class="sparqlLink"><g:link action="sparqlForm">SPARQL query</g:link></p>
		
		<h2>Transformation program</h2>
		
		<g:form action="saveProgram">
			<g:textArea name="program" value="${program}" rows="10" cols="80" />
			<p><g:submitButton name="save" value="Save program" /></p>
		</g:form>

    </body>
</html>
