<html>
<head>
	<title><g:message code="msg.map.title"/></title>
    <script type="text/javascript"
          src="http://maps.googleapis.com/maps/api/js?key=AIzaSyBrr-fgXpidTazB9PUI0U6YjzaHh5BSgMA&amp;sensor=false"></script>
    <script type="text/javascript">
		$(document).ready(function() {
        var myOptions = {
          center: new google.maps.LatLng(0, 0),
          zoom: 2,
          mapTypeId: google.maps.MapTypeId.ROADMAP
        };
        var map = new google.maps.Map(document.getElementById("map_canvas"), myOptions);
        <g:each in="${geopoints}">
            var point${it.id} = new google.maps.LatLng(${it.lat}, ${it.lon})
            var marker${it.id} = new google.maps.Marker({
                position: point${it.id},
                map: map,
                title: "${it.label}",
                animation: google.maps.Animation.DROP
            })
        </g:each>
		});
    </script>
    <meta name="layout" content="main" />
</head>
<body onload="initialize()">       

	<h2><g:message code="msg.map.title"/></h2>
	
	<p><g:link action="index">Back to the project</g:link></p>
	
    <div id="map_canvas"></div>

</body>
</html>