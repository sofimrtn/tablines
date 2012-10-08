<html>
    <head>
        <title><g:message code="title.tabels.projects"/></title>
        <meta name="layout" content="main" />
    </head>
    <body>
        <h2><g:message code="title.tabels.projects"/></h2>
        
    	<g:if test="${!projects}">
    		<g:if test="${!q}">
    	    	<div class="messagebox"><p><g:message code="msg.why.nothing.in.project.list.msg"/></p></div>
    	   	</g:if>
    	    <g:else>
    	    	<div class="messagebox"><p><g:message code="msg.why.nothing.in.project.search.msg" args="[q]"/></p></div>
    	    </g:else>
    	</g:if>
        
        <div class=projectListBox>
			<div id="projectListDiv">
				<g:form action="list" method="post">
					<g:textField name="q" id="searchbox" class="searchbox" value="${q}"/><g:submitButton name="filter" class="button" value="" />
					<r:script>
						if(!$('#searchbox').val()) $('#searchbox').val('${message(code:"msg.filter.project.name")}').css('color', '#cdc9c9');;
						console.log($('#searchbox').value);
		    		    $('#searchbox').blur(function() {if (!this.value || this.value == '') {this.value = '${message(code:"msg.filter.project.name")}'; $(this).css('color', '#cdc9c9');}});
		    		    console.log($('#searchbox').value);
		    		    $('#searchbox').focus(function() {if (this.value == '${message(code:"msg.filter.project.name")}') {this.value = ''; $(this).css('color', '#000');}});
		    		    console.log($('#searchbox').value);
	    		    </r:script>
				</g:form>
	        	<ul class="projectList">
	            <g:each in="${projects}" var="project">
	                <li>
	                    <g:link action="index" id="${project}" class="projectListLink">${project}</g:link>
	                </li>
	            </g:each>
	        	</ul>
			</div>	
			<div class="createProjectBox" >
	        <h2><g:message code="msg.create.project.button"/></h2>
	       		   <g:form action="create" method="post" class="createProject">
	    		        <g:textField name="newProjectId" value="${message(code:'msg.new.project.name')}"/>
	    		        <r:script>
		    		        $('#newProjectId').blur(function() {if (this.value == '') {this.value = '${message(code:"msg.new.project.name")}'; $(this).css('color', '#cdc9c9');}});
		    		        $('#newProjectId').focus(function() {if (this.value == '${message(code:"msg.new.project.name")}') {this.value = ''; $(this).css('color', '#000');}});
	    		        </r:script>
	    		        <g:submitButton class="AddProjectButton" name="createProject" value="${message(code:'msg.create.project.button')}" />
	    		    </g:form>
	        </div>
<br class="clear"/>

        </div>

        <h2><g:message code="title.access.your.data"/></h2>
		<p class="sparqlLink"><g:message code="msg.endpoint.sparql.link"/> <g:link mapping="globalSparql"><g:createLink mapping="globalSparql" absolute="true"/></g:link></p>
		
	</body>
</html>
