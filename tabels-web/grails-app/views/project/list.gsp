<html>
    <head>
        <title>Tabels projects</title>
<!--		<r:require modules="uploadr"/> -->
        <meta name="layout" content="main" />
    </head>
    <body>
        <h2>Tabels projects</h2>
        <ul>
            <g:each in="${projects}" var="project">
                <li>
                    <g:link action="index" id="${project}">${project}</g:link>
                    (<g:link action="delete" id="${project}"><g:message code="msg.delete.project.link"/></g:link>)
                </li>
            </g:each>
        </ul>
        
		<g:form action="create" method="post">
		    <p>
		        <g:message code="msg.new.project.name"/> <g:textField name="newProjectId" value=""/>
		        <g:submitButton name="createProject" value="${message(code:'msg.create.project.button')}" />
		    </p>
		</g:form>
		
    </body>
</html>
