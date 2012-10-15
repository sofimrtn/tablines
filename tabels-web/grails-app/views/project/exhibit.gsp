<html>
<head>
	<title><g:message code="msg.faceted.view.title"/></title>
    <script src="http://api.simile-widgets.org/exhibit/2.2.0/exhibit-api.js"
                type="text/javascript"></script>
    <link href="exhibitData" type="application/json" rel="exhibit/data" />
    <meta name="layout" content="main" />
    <style>
        tr:hover {
            background: white;
            color: black;
        }
    </style>
</head>
<body>       

    <ul id="crumbs" class="crumbsBlock">
        <li><g:link action="home">Home</g:link></li>
        <li><g:link action="list" id="${params.id}">Projects</g:link></li>
        <li><g:link action="index" id="${params.id}">Project ${params.id}</g:link></li>
        <li><g:message code="msg.faceted.view.title"/></li>
    </ul>
    
	<!--<h2><g:message code="msg.faceted.view.title"/></h2>
	<p class="backLink"><g:link action="index" id="${params.id}"><g:message code="msg.back.to.project.link"/></g:link></p>-->
	
    <table width="100%">
            <tr valign="top" class="exhibit">
                <td ex:role="viewPanel">
                    <div ex:role="view"></div>
                </td>
                <td width="25%">
                    <div ex:role="facet" ex:facetClass="TextSearch"></div>
                    <div ex:role="facet" ex:expression=".type" ex:facetLabel="Type"></div>
                    <g:each in="${facets}">
                        <div ex:role="facet" ex:expression=".${it}" ex:facetLabel="${it}"></div>
                    </g:each>
                </td>
            </tr>
    </table>
        
</body>
</html>