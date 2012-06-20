<html>
    <head>
        <title>Tabels projects</title>
        <meta name="layout" content="main" />
    </head>
    <body>
        <h2>Tabels projects</h2>
        <ul class="projectList">
            <g:each in="${projects}" var="project">
                <li>
                    <g:link action="index" id="${project}" class="projectListLink">${project}</g:link>
                    <g:link action="delete" id="${project}" class="deleteProjectList" alt="${message(code:'msg.delete.project.link')}"/>
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
