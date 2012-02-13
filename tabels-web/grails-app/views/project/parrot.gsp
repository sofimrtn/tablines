<html>
    <head>
        <title>Tabels project</title>
        <meta name="layout" content="main" />
    </head>
    <body>
        <h1>Generate dataset documentation</h1>
    	<p><g:link action="index">Back to the project</g:link></p>

        <form action="http://ontorule-project.eu/parrot/parrot" method="post">
            <input type="hidden" name="documentText" value="${documentText.encodeAsHTML()}" />
            <input type="hidden" name="mimetypeText" value="application/owl+xml" />
            <p>Pressing the following button will take you to the documentation generated
                by Parrot.</p>
            <input type="submit" value="Generate documentation with Parrot" />
        </form>
    </body>
</html>
