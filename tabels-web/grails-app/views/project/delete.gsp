<html>
    <head>
        <title>Tabels project - Delete Project</title>
        <meta name="layout" content="main" />
    </head>
    <body>
    	<ul id="crumbs" class="crumbsBlock">
        	<li><g:link action="home">Home</g:link></li>
        	<li><g:link action="list" id="${params.id}">Projects</g:link></li>
        	<li><g:link action="index" id="${params.id}">Project ${params.id}</g:link></li>
        	<li>Delete project</li>
    	</ul>
        <!--<h2><g:message code="msg.confirm.project.deletion.title" /></h2>-->
        
		<g:form action="delete" id="${params.id}" method="post">
		    <p>
		        <g:message code="msg.are.you.sure.delete.project" args="${[params.id]}"/>
		        <g:checkBox name="confirm" value="${false}" />
		        <g:submitButton name="confirmDelete" value="${message(code:'msg.confirm.delete.button')}" class="inputbutton red medium"/>
		    </p>
		</g:form>
		
    </body>
</html>
