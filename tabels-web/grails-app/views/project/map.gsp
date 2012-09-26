<!DOCTYPE HTML PUBLIC "-//W3C//DTD HTML 4.01//EN" "http://www.w3.org/TR/html4/strict.dtd">
<html>
<head>
    <!--<meta http-equiv="content-type" content="text/html; charset=ISO-8859-1">-->
    <title>Tabels Map</title>
    <r:require modules="dynatree, maplab"/>

    <script type="text/javascript" src="http://maps.google.com/maps/api/js?sensor=true"></script>
    <meta name="layout" content="main" />	   

</head>

<body>
    <div id="container">
        <div id="leftContainer">
            <div id="layerMap">
                <div class="introMap">  
                    <span>Maps Layers</span>
                </div>
                <div id="tree" name="selNodes">
                </div>
            </div>
            
            <!-- <div id="layerLegend">
                <div class="introLegend">   
                    <a href="javascript:void(0);" onclick="javascript:mapLabLayers($('#legend'))">Legend</a>
                </div>
                <div id="legend"><ul><li>No hay nada seleccionado</li></ul></div>
            </div>
            -->
            
            <div id="visualize">
                <a href="javascript:void(0);" onclick="javascript:mapLabReload($('#tree'), $('#mapGoogle'))">Visualize</a>
            </div>
        </div>
        <div id="rightContainer">
            <div id="mapGoogle">
            </div>
        </div>
    </div>
    <r:script>
        //Tree must be defined before map
        $("#tree").mapLabTree({
            treeWs: "<g:resource dir='ws' file='tree'/>",
            mapRef: "mapGoogle",
            legendRef: "legend",
            endpoint: "${endpoint}",
            namedgraph: "${namedgraph}"
        });
        
        $("#mapGoogle").mapLabMap({
            mapAreaWs: "<g:resource dir='ws' file='mapArea'/>",
            treeRef: "tree",
            endpoint: "${endpoint}",
            namedgraph: "${namedgraph}"
        });
        window.onload = mapLabLayers($('#tree'));
    </r:script>
</body>
</html>