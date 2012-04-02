<html>
    <head>
        <title>Tabels project</title>
		<r:require modules="uploadr"/>
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
        <div class="stepBox" id="step1">
		<h2><g:message code="msg.step1.drag"/></h2>
		
		<img id="spreadsheet-icon" src="${resource(dir:'images', file:'spreadsheet.png')}" alt="Spreadsheet file icon"/><br/>
		<img id="drag-icon" src="${resource(dir:'images', file:'down-arrow.png')}" alt="An arrow indicating where to drag your spreadsheet files"/>

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
		        <g:submitButton name="addUrl" value="${message(code:'msg.add.url.to.sources.button')}" />
		    </p>
		</g:form>
		
		</div>
		
		<div class="stepBox" id="step2">
		<h2><g:message code="msg.step2.access"/></h2>
		
		<g:link action="rdf"><img id="downloadRDFIcon" src="${resource(dir:'images',file:'download.png')}" alt="Download RDF"/></g:link>
		<p class="rdfDownloadLink"><g:link action="rdf"><g:message code="msg.download.rdf.title"/></g:link></p>
		
		<p class="sparqlLink"><g:message code="msg.endpoint.sparql.link"/> <g:link action="sparql"><g:createLink controller="project" action="sparql" absolute="true"/></g:link></p>
		
		<p class="pubbyLink"><span class="stars">★★★★★</span> <a href="${resource(dir:'pubby')}">Browse the linked data</a></p>
		
		<p class="chartsLink"><g:link action="tapinos"><g:message code="msg.charts.link"/></g:link></p>
		    
		<p class="facetedLink"><g:link action="exhibit"><g:message code="msg.faceted.link"/></g:link></p>
		    
		<p class="mapLink"><g:link action="map"><g:message code="msg.map.link"/></g:link></p>
		
		<p class="parrotLink"><g:link action="parrot"><g:message code="msg.parrot.link"/></g:link></p>
		    
		</div>
		
		<div id="programDiv">
		<h2><g:message code="msg.transformation.program"/></h2>
		
		<g:form action="saveProgram" method="post">
		<p><g:submitButton name="save" value="${message(code: 'msg.save.program.button')}" /></p>
			<g:textArea name="program" value="${program}" rows="10" cols="80" />
		</g:form>
		<g:form action="autogenerateProgram" method="post">
		    <g:select name="strategy" from="${['Simple','SCOVO']}" />
		    <g:submitButton name="autogenerate" value="${message(code: 'msg.autogenerate.program.button')}" /></p>
		</g:form>
		</div>

    </body>
</html>
