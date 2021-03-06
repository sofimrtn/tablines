<html>
    <head>
        <title>Tabels Project - ${params.id}</title>
		<r:require modules="uploadr,codemirror"/>
        <meta name="layout" content="main" />	   
	    <r:script>
			$(document).ready(function() {
				var editor = CodeMirror.fromTextArea(program, {
					<g:if test="${readonly}">readOnly: true,</g:if>
					mode: "tabels",
					lineNumbers: true,
					matchBrackets: true/*,
					extraKeys: {"Ctrl-Space": "autocomplete"}*/
				});
			});
	    </r:script>
    </head>
    <body>
        <ul id="crumbs">
			<li><g:link action="home">Home</g:link></li>
			<li><g:link action="list" id="${params.id}">Projects</g:link></li>
			<li>Project ${params.id}</li>
		</ul>
        <div class="projectInfo">
        	<ul>
        		<g:if test="${!readonly}">
		    		<li>
				        <g:form action="rename" id="${params.id}" method="post" style="display:inline">
				        <strong><g:message code="msg.project.name"/></strong>
				        <g:textField id="newProjectId" name="newProjectId" value="${params.id}"/>
		                    <r:script>
		                    	$(document).ready(function() {
			                        $('#newProjectId').blur(function() {if (this.value == '') {this.value = '${message(code:"msg.new.project.name")}';}});
			                        $('#newProjectId').focus(function() {if (this.value == '${message(code:"msg.new.project.name")}') {this.value = '';}});
			                        $('#newProjectId').change(function() {
		  								$("#changeName").removeAttr("disabled");
									});
								});
		                    </r:script>
				        <g:submitButton id="changeName" name="changeName" value="${message(code:'msg.rename.project.link')}" class="inputbutton gray medium" disabled="disabled"/>
				    	</g:form>
		    		</li>
                	<li><g:link action="delete" id="${params.id}" class="inputbutton red medium"><g:message code="msg.delete.project.link"/></g:link></li>
                </g:if>
                <g:else>
                	<li><strong><g:message code="msg.project.name"/></strong> ${params.id}</li>
            	</g:else>
            </ul>

        </div>
        <div class="stepBox" id="step1">
		<h2><g:message code="msg.step1.drag"/></h2>
		
		<img id="spreadsheet-icon" src="${resource(dir:'images', file:'spreadsheet.png')}" alt="Spreadsheet file icon"/><br/>
		<img id="drag-icon" src="${resource(dir:'images', file:'down-arrow.png')}" alt="An arrow indicating where to drag your spreadsheet files"/>

		<uploadr:add name="uploadr${params.id.replaceAll(/-/, "")}" path="${path}" maxSize="${maxFileSize}" allowedExtensions="${allowedExtensions}" viewable="false" deletable="${!readonly}">
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
		<g:if test="${!readonly}">
		<g:form action="downloadSource" id="${params.id}" method="post">
		    <p>
		        <g:textField name="sourceUrl" value="${sourceUrl}" size="65" class="sourceUrlText"/>
		        <g:submitButton name="addUrl" value="${message(code:'msg.add.url.to.sources.button')}" class="inputbutton white medium"/>
		    </p>
		</g:form>
		</g:if>
		</div>
		
		<div class="stepBox" id="step2">
		<h2><g:message code="msg.step2.access"/></h2>
		
		<!--<g:link action="rdf" id="${params.id}"><img id="downloadRDFIcon" src="${resource(dir:'images',file:'download.png')}" alt="Download RDF"/></g:link> -->
		<div id="datasetInfo">
		  <div id="datasetInfoWaiting"><img src="${resource(dir:'images',file:'loader.gif')}"> Waiting...</div>
		  <div id="datasetInfoData">
		      <g:message code="msg.triples"/>: <span id="triplesCount"></span>
		  </div>
		  <div id="datasetInfoError">
		      <g:message code="msg.count.triples.error"/>
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
		 
		<p class="chartsLink"><g:link mapping="globalTapinos" params="[endpoint:endpoint]" id="${params.id}"><g:message code="msg.charts.link"/></g:link></p>   
		    
		<p class="mapLink"><g:link mapping="newMap" id="${params.id}" params="[endpoint:endpoint]"><g:message code="msg.map.link"/></g:link></p>
		
		</div>
		<div class="showHide">
			<h2><g:message code="msg.transformation.program"/><span class="inputbutton white small">Show/Hide</span></h2>
		</div>
		<div id="programDiv">
		<g:if test="${!readonly}">
			<g:form action="autogenerateProgram" id="${params.id}" method="post" class="autogenerateForm">
			    <g:select name="strategy" from="${['Simple','MAPS']}" value="Simple" class="inputbutton white medium"/>
			    <g:submitButton name="autogenerate" value="${message(code: 'msg.autogenerate.program.button')}" class="inputbutton white medium"/>
			</g:form>
		</g:if>
		<g:form action="saveProgram" id="${params.id}" method="post">
			<g:if test="${!readonly}"><p><g:submitButton name="save" value="${message(code: 'msg.save.program.button')}" class="inputbutton white medium"/></p></g:if>
			<g:textArea name="program" value="${program}" cols="80" />
			<g:if test="${!readonly}"><p><g:submitButton name="save" value="${message(code: 'msg.save.program.button')}" class="inputbutton white medium"/></p></g:if>
		</g:form>

		</div>

    </body>
</html>
