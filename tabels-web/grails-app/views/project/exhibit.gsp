<html>
<head>
	<title>Faceted view</title>
    <script src="../js/exhibit/exhibit-api.js"
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

	<h1>Faceted view</h1>
	
	<p><g:link action="index">Back to the project</g:link></p>
	
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