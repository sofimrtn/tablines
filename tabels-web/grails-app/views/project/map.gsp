<!DOCTYPE HTML PUBLIC "-//W3C//DTD HTML 4.01//EN" "http://www.w3.org/TR/html4/strict.dtd">
<html>
<head>
    <!--<meta http-equiv="content-type" content="text/html; charset=ISO-8859-1">-->
    <title>My Tree Example</title>

    <script src="http://wwwendt.de/tech/dynatree/jquery/jquery.js" type="text/javascript"></script>
    <script src="http://wwwendt.de/tech/dynatree/jquery/jquery-ui.custom.js" type="text/javascript"></script>
    <script src="http://wwwendt.de/tech/dynatree/jquery/jquery.cookie.js" type="text/javascript"></script>

    <link href="http://wwwendt.de/tech/dynatree/src/skin/ui.dynatree.css" rel="stylesheet" type="text/css" id="skinSheet">
    <script src="http://wwwendt.de/tech/dynatree/src/jquery.dynatree.js" type="text/javascript"></script>

    <script type="text/javascript" src="http://maps.google.com/maps/api/js?sensor=true"></script>
    
    <link href="${resource(dir:'css',file:'maplab.css')}" rel="stylesheet" type="text/css">
    <script src="${resource(dir:'js',file:'mapLabmap.js')}" type="text/javascript"></script>
    <script src="${resource(dir:'js',file:'mapLabTree.js')}" type="text/javascript"></script>
</head>

<body>
    <div id="container">
        <div id="leftContainer">
            <div id="layerMap">
                <div class="introMap">  
                    <a href="javascript:void(0);" onclick="javascript:mapLabLayers($('#tree'))">Maps Layers</a>
                </div>
                <div id="tree" name="selNodes">
                </div>
            </div>
            
            <div>Selected keys: <span id="echoSelection">-</span></div>
            
            <div id="layerLegend">
                <div class="introLegend">   
                    <a href="javascript:void(0);" onclick="javascript:mapLabLayers($('#legend'))">Legend</a>
                </div>
                <div id="legend"><ul><li>No hay nada seleccionado</li></ul></div>
            </div>
            
            <div id="visualize">
                <a href="javascript:void(0);" onclick="javascript:mapLabReload($('#tree'), $('#mapGoogle'))">Visualize</a>
            </div>
        </div>
        <div id="rightContainer">
            <div id="mapGoogle">
            </div>
        </div>
    </div>
    <script>
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
        
    </script>
</body>
</html>