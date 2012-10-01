<html>
    <head>
        <title>Tabels project</title>
		<r:require modules="uploadr,codemirror"/>
        <meta name="layout" content="main" />	   
	    <r:script>
			$(document).ready(function() {
				var editor = CodeMirror.fromTextArea(program, {
					mode: "tabels",
					lineNumbers: true,
					matchBrackets: true/*,
					extraKeys: {"Ctrl-Space": "autocomplete"}*/
				});
			});
			 /* CodeMirror.commands.autocomplete = function(cm) {
		        CodeMirror.simpleHint(cm, CodeMirror.javascriptHint);
		      }*/
	    </r:script>
    </head>
    <body>
        
        <div class="projectInfo">
        	<ul>
            	<li><g:message code="msg.project.name"/> ${params.id}</li>
                <li class="renameLink"><g:link action="rename" id="${params.id}"><g:message code="msg.rename.project.link"/></g:link></li>
                <li class="deleteLink"><g:link action="delete" id="${params.id}"><g:message code="msg.delete.project.link"/></g:link></li>
            </ul>

        </div>
     
        <div class="stepBox" id="step1">
		<h2><g:message code="msg.step1.drag"/></h2>
		
		<img id="spreadsheet-icon" src="${resource(dir:'images', file:'spreadsheet.png')}" alt="Spreadsheet file icon"/><br/>
		<img id="drag-icon" src="${resource(dir:'images', file:'down-arrow.png')}" alt="An arrow indicating where to drag your spreadsheet files"/>

		<uploadr:add name="uploadr${params.id.replaceAll(/-/, "")}" path="${path}">
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
		
		<g:form action="downloadSource" id="${params.id}" method="post">
		    <p>
		        <g:textField name="sourceUrl" value="${sourceUrl}" size="65"/>
		        <g:submitButton name="addUrl" value="${message(code:'msg.add.url.to.sources.button')}" />
		    </p>
		</g:form>
		
		</div>
		
		<div class="stepBox" id="step2">
		<h2><g:message code="msg.step2.access"/></h2>
		
		<!--<g:link action="rdf" id="${params.id}"><img id="downloadRDFIcon" src="${resource(dir:'images',file:'download.png')}" alt="Download RDF"/></g:link> -->
		<div id="datasetInfo">
		  <div id="datasetInfoWaiting">Espere...</div>
		  <div id="datasetInfoData">
		      Triples: <span id="triplesCount"></span>
		  </div>
		  <div id="datasetInfoError">
		      Ooops! There was a problem running the transformation
	      </div>
		</div>
		<r:script>
		    $(document).ready(function() {
		        $("#datasetInfoData").hide();
		        $("#datasetInfoError").hide();
		        $.ajax({
		            url: "${createLink(controller: 'project', action:'datasetInfo', id: params.id)}"		            
		        }).done(function(data) {
		            $("#triplesCount").text(data.info.triplesCount);
		            $("#datasetInfoData").show();
		        }).fail(function() {
		            $("#datasetInfoError").show();
		        }).always(function() {
		            $("#datasetInfoWaiting").hide();		            
		        });
		    });
		</r:script>
		
		<p class="rdfDownloadLink"><g:message code="msg.download.rdf.title"/>
			<g:link action="data" id="${params.id}">[RDF/XML]</g:link>
			<g:link action="data" id="${params.id}" params="[format: 'ttl']">[Turtle]</g:link>
			<g:link action="data" id="${params.id}" params="[format: 'text']">[N-Triples]</g:link>
		</p>
		
		<p class="sparqlLink"><g:link action="sparql" id="${params.id}" mapping="projectSpecific"><g:message code="msg.endpoint.sparql.link"/></g:link></p>
		
		<p class="facetedLink"><g:link action="exhibit" id="${params.id}"><g:message code="msg.faceted.link"/></g:link></p>
		 
		<p class="chartsLink"><g:link mapping="globalTapinos" params="[endpoint:endpoint]"><g:message code="msg.charts.link"/></g:link></p>   
		    
		<p class="mapLink"><g:link mapping="newMap" id="${params.id}" params="[endpoint:endpoint]"><g:message code="msg.map.link"/></g:link></p>
		
		<p class="parrotLink"><g:link action="parrot" id="${params.id}"><g:message code="msg.parrot.link"/></g:link></p>
		
		<p class="traceLink"><g:link action="trace" id="${params.id}"><g:message code="msg.trace.link"/></g:link></p>
		    
		</div>
		
		<div id="programDiv">
		<h2><g:message code="msg.transformation.program"/></h2>
		
		<g:form action="saveProgram" id="${params.id}" method="post">
		<p><g:submitButton name="save" value="${message(code: 'msg.save.program.button')}" /></p>
			<g:textArea name="program" value="${program}" rows="10" cols="80" />
		</g:form>
		<g:form action="autogenerateProgram" id="${params.id}" method="post">
		    <g:select name="strategy" from="${['Simple','SCOVO','MAPS']}" value="MAPS"/>
		    <g:submitButton name="autogenerate" value="${message(code: 'msg.autogenerate.program.button')}" /></p>
		</g:form>
		</div>

    </body>
</html>
