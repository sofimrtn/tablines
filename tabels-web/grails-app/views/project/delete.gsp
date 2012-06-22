<html>
    <head>
        <title>Tabels project</title>
        <meta name="layout" content="main" />
    </head>
    <body>
        <h2><g:message code="msg.confirm.project.deletion.title" /></h2>
        
		<g:form action="delete" id="${params.id}" method="post">
		    <p>
		        <g:message code="msg.are.you.sure.delete.project" args="${[params.id]}"/>
		        <g:checkBox name="confirm" value="${false}" />
		        <g:submitButton name="confirmDelete" value="${message(code:'msg.confirm.delete.button')}" />
		    </p>
		</g:form>
		
    </body>
</html>
