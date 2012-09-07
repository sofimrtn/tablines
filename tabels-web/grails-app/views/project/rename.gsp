<html>
    <head>
        <title>Tabels project</title>
        <meta name="layout" content="main" />
    </head>
    <body>
        <h2><g:message code="msg.project.rename.title" /></h2>
        
		<g:form action="rename" id="${params.id}" method="post">
		    <p>
		        <g:message code="msg.rename.project" args="${[params.id]}"/>
		        <g:textField name="newProjectId" value="${message(code:'msg.new.project.name')}"/>
                    <r:script>
                        $('#newProjectId').blur(function() {if (this.value == '') {this.value = '${message(code:"msg.new.project.name")}';}});
                        $('#newProjectId').focus(function() {if (this.value == '${message(code:"msg.new.project.name")}') {this.value = '';}});
                    </r:script>
		        <g:submitButton name="changeName" value="${message(code:'msg.rename.button')}" />
		    </p>
		</g:form>
		
    </body>
</html>
