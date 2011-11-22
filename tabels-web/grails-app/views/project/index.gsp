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
		<h2>Step 1: drag some spreadsheets to the list</h2>
		
		<img src="${resource(dir:'images', file:'spreadsheet.png')}" alt="Spreadsheet file icon"/>

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
		
		<g:form action="downloadSource" method="post">
		    <p>
		        <g:textField name="sourceUrl" value="${sourceUrl}"/>
		        <g:submitButton name="addUrl" value="Add URL to the sources" />
		    </p>
		</g:form>
		
		<g:link action="rdf"><img src="${resource(dir:'images',file:'download.png')}" alt="Download RDF"/></g:link>		
		<p class="rdfDownloadLink"><g:link action="rdf">Download RDF</g:link></p>
		
		<p class="sparqlLink">The SPARQL endpoint is <g:link action="sparqlForm">${resource(controller: 'project', action: 'sparqlForm', absolute: true)}</g:link></p>
		
		<h2>Transformation program</h2>
		
		<g:form action="saveProgram" method="post">
			<g:textArea name="program" value="${program}" rows="10" cols="80" />
			<p><g:submitButton name="save" value="Save program" /></p>
		</g:form>

    </body>
</html>
