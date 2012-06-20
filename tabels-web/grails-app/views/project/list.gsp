<html>
    <head>
        <title>Tabels projects</title>
        <meta name="layout" content="main" />
    </head>
    <body>
        <h2>Tabels projects</h2>
        <div class="createProjectBox" >
        <h2><g:message code="msg.create.project.button"/></h2>
       		   <g:form action="create" method="post" class="projectListLink">
    		        <g:textField name="newProjectId" value="${message(code:'msg.new.project.name')}"/>
    		        <r:script>
	    		        $('#newProjectId').blur(function() {if (this.value == '') {this.value = '${message(code:"msg.new.project.name")}';}});
	    		        $('#newProjectId').focus(function() {if (this.value == '${message(code:"msg.new.project.name")}') {this.value = '';}});
    		        </r:script>
    		        <g:submitButton class="AddProjectButton" name="createProject" value="${message(code:'msg.create.project.button')}" />
    		    </g:form>
        </div>
        <ul class="projectList">
        
            <h2><g:message code="msg.project.list"/></h2>
            <g:each in="${projects}" var="project">
                <li>
                    <g:link action="index" id="${project}" class="projectListLink">${project}</g:link>
                    <g:link action="delete" id="${project}" class="deleteProjectList" alt="${message(code:'msg.delete.project.link')}"/>
                </li>
            </g:each>
        </ul>
        
    </body>
</html>
