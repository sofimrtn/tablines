<html>
<head>
	<title>Visualizador genérico</title>
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

	<h1>Visualizador genérico de datos</h1>
	
    <table width="100%">
            <tr valign="top" class="exhibit">
                <td ex:role="viewPanel">
                    <div ex:role="view"></div>
                </td>
                <td width="25%">
                    <div ex:role="facet" ex:facetClass="TextSearch"></div>
                    <div ex:role="facet" ex:expression=".type" ex:facetLabel="Type"></div>
                    <div ex:role="facet" ex:expression=".dimension" ex:facetLabel="Dimension"></div>
                    <div ex:role="facet" ex:expression=".dataset" ex:facetLabel="Dataset"></div>
                </td>
            </tr>
    </table>
        
</body>
</html>