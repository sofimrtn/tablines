<html>
    <head>
        <title>Tabels project</title>
        <meta name="layout" content="main" />
    </head>
    <body>
    	<p class="backLink"><g:link action="index" id="${params.id}"><g:message code="msg.back.to.project.link"/></g:link></p>

        <h2><g:message code="msg.rename.project.link" /></h2>
        
		<g:form action="rename" id="${params.id}" method="post">
		    <p>
		        <g:message code="msg.current.project.name" /> ${params.id}
		    </p>
		    <p>
		        <g:message code="msg.new.project.name" />
		        <g:textField name="newProjectId" value="${params.id}" />
		        <g:submitButton name="renameButton" value="${message(code:'msg.rename.project.link')}" />
		    </p>
		</g:form>
		
    </body>
</html>
