<html>
    <head>
        <title>Tabels project</title>
        <meta name="layout" content="main" />
        <r:require modules="fancybox" />
    </head>
    <body>
    	<div class="projectListBox homePage">
		    <div class="left">
	        	<h2>Tabels project</h2>
	        	<div>
		        	<p><strong>Tabels</strong> (<em>Tabular Cells</em>) is an R&amp;D tool, 
		        	developed by <a href="http://www.fundacionctic.org/en" title="CTIC">CTIC</a>, 
		        	to make meaning of 
		            tabular data. <strong>Tabels</strong> goes beyond spreadsheets and csv files to include other 
		            tabular formats such as PC-Axis, ESRI shapefile, analysis-tools formats and so forth. 
		            Data tables are everywhere in our life and organizations. Calendars, maps, 
		            forms, reports and charts are only some examples of this tables omnipresence.</p>
					<p>The aim is to provide means to discover and to surface the data structures hidden 
			   		in tables, and to enable users to combine data past the limits of files and formats. 
			    	By transforming data tables to RDF datasets, the information integration achieves 
			    	a new dimension. Raw data transcends into a world of linked resources brimming 
			    	with enrichment and entity reconciliation opportunities. <strong>Tabels</strong> is not a mere 
			    	transformation tool, but it facilitates end-user exploitation of data by supplying 
			    	front-end interactive mechanisms.</p> 
			    	<p>Give a <g:link action="list">try to our online demo service</g:link>
	    		    and <g:link action="contact">tell us</g:link> about your experience!</p>

			    </div>

				<h3 class="accordion inputbutton orange title"> <strong>Tabels</strong> language</h3>
				<div>
					<div class="screenshot">
			    		<a href="${resource(dir:'images',file:'code-screenshot.png')}"><img src="${resource(dir:'images',file:'code-screenshot.png')}" alt="Chart Screenshot" /></a>
			    	</div>
					<p><strong>Tabels</strong> comes with its own DSL (Domain Specific Language) to define the transformations 
			    	from data tables to RDF. This language allows to declaratively express mappings 
			    	between the two worlds: tables and graphs. A <strong>Tabels</strong> program comprises three different parts:</p>
					<ol>
			  			<li>The <code>PREFIX</code> section: where namespaces and prefixes are declared in a similar fashion as 
			      		the SPARQL language.</li>
			  			<li>The data tables interpretation section: where the program specifies how 
			      		to go through the tables according to four complementary dimensions: 
			      		(1) the file dimension; (2) the sheet dimension; (3) the row dimension; and 
			      		(4) the column dimension. In addition, the language enables to declaratively 
			      		express the process of data structures by means of three kind of statements:
						<ul>
				  			<li>Dimension statements: the <code>SET</code> operator is used to fix the dimension into 
				      		a single value (for instance, to cover a particular file). On the other hand, 
				      		the <code>FOR</code> operator is used to iterate through all the values comprising a 
				      		given dimension (for instance, all the rows).</li>
				  			<li>Conditional statements: the <code>WHEN</code> operator is used to restrict the iteration 
				      		through data by specifying boolean conditions that must be fulfilled. The <code>WHEN</code> 
				      		statements are semantically similar to <code>IF/THEN</code> rules.</li>
				  			<li>Binding statements: the <code>LET</code> operator is used to assign a value to a new variable. 
				      		This binding may be direct values from a data table or the result of another 
				      		data operation. The <code>MATCH</code> operator provides a mechanism to define tuples of variables 
				      		which values are obtained from a data table.</li>
						</ul>
			  			</li>
			  			<li>The RDF template section: where the program specifies the RDF graph pattern 
			  			    in which variables are instantiated with the bindings obtained from the data 
			  			    table processing. Graph patterns can be separated and are expressed in N3 
			  			    syntax.</li>
					</ol>
					<p>The <strong>Tabels</strong> language also provides auxiliary machinery to filter and compare data values during 
			    	the transformation process. There are a number of already defined functions to this end, such 
			    	as the XPath operations.</p>
		    	</div>
		    	
				<h3 class="accordion inputbutton orange title">APIs and data access</h3>
    			<div>
    				<div class="screenshot">
			    		<a href="${resource(dir:'images',file:'project-screenshot.png')}"><img src="${resource(dir:'images',file:'project-screenshot.png')}" alt="Chart Screenshot" /></a>
			    	</div>
    				<p><strong>Tabels</strong> has been designed as a data-experts tool providing some functionalities to
    				    transform, integrate and explore data tables. To this end, <strong>Tabels</strong> has been geared
    				    with a complete data-explotation toolkit: faceted views, interactive charts, etc. In addition,
    				    <strong>Tabels</strong> allows to download the produced RDF dataset in several syntaxes, namely RDF/XML,
    				    N3 and Turtle; and complementarily it provides a SPARQL data-access endpoint, 
    				    where queries can be performed by both domain experts and third-party tools.</p>
    				
    				<p>Besides the web front-end usage, <strong>Tabels</strong> is equipped with a set of REST services
    				    and a native Scala API to enable its integration into a more complex software
    				    architecture as a library.</p>
    			</div>

				<h3 class="accordion inputbutton orange title">RDF transformation, mediation and reconciliation</h3>
    			<div>
    				<p><strong>Tabels</strong> also supports some data operations over RDF graphs. The language
    				    has special commands (called directives) empowering domain experts
    				    to perform data cleaning, merging, preparation, etc. Following,
    				    <strong>Tabels</strong> directives are briefly described:</p>
    				<ul>
    				    <li><code>@LOAD</code> directive, to upload and merge RDF files with the current dataset.</li>
    				    <li><code>@JENARULE</code> directive, to define (recursive) data transformations as Jena rules,
    				        both backward and forward chaining.</li>
    				    <li><code>@SPARQL</code> directive, to perform an SPARQL Update query in order to update the
    				        current dataset. They can be also used as (non-recursive) production rules to 
    				        transform the RDF graph.</li>
    				    <li><code>@FETCH</code> directive, which retrieves external resources descriptions after the 
    				        transformation process. For instance, in case a DBpedia URI is used, <strong>Tabels</strong>
    				        performs a <code>GET</code> request to bring back its description and feed the current
    				        dataset.</li> 
    				</ul>
    				
    				<p>In addition, <strong>Tabels</strong> is supplied with some disambiguation functions that can be 
    				    used in the transformation process. These algorithms implement string-matching
    				    metrics such as the Levenshtein distance. There are a number of data-disambiguation 
    				    corpora to this end: DBpedia, Geonames, etc.</p>
    	    		<p>For instance: <g:link action="index" id="safetyRegions">Safety regions project</g:link> and <g:link action="index" id="imdb">IMDB</g:link></p>
    			</div>

				<h3 class="accordion inputbutton orange title">Charts and statistics</h3>
				<div>
					<div class="screenshot screenshotLeft">
			    		<a href="${resource(dir:'images',file:'chart-screenshot.png')}"><img src="${resource(dir:'images',file:'chart-screenshot.png')}" alt="Chart Screenshot" /></a>
			    	</div>
					<p><strong>Tabels</strong> benefits from the Understats (Understanding Statistics) technology, 
			    	an easy-to-deploy HTML5-based component which facilitates end-users an
			    	interactive exploration of statistical and multidimensional data.</p>
					<p>Understats is a data-sensitive front-end that generates
			    	responsive charts from the inspection of the dimensions and measures comprising
			    	an statistical indicator represented with the RDF Data Cube vocabulary. <strong>Tabels</strong>
			    	can be used then to automatically provide visualizations of multidimensional datasets 
			    	published by national and international agencies, such as 
			    	<a href="http://epp.eurostat.ec.europa.eu/" title="Eurostat">Eurostat</a>, 
			    	<a href="http://www.fao.org/" title="FAO">FAO</a> or 
			    	<a href="http://www.imf.org/" title="IMF">IMF</a> ones,
			    	typically encoded in csv, spreadsheets or even PC-Axis (*.px) formats.</p>
    	    		<p>For instance: <g:link action="index" id="MouseStatsQB">Mouse project</g:link> and <g:link action="index" id="eurostat">Eurostats Flora project</g:link></p>
			    			
		    	</div>
				
				<h3 class="accordion inputbutton orange title">Maps and geographic data</h3>
				<div>
					<div class="screenshot">
			    		<a href="${resource(dir:'images',file:'map-screenshot.png')}"><img src="${resource(dir:'images',file:'map-screenshot.png')}" alt="Chart Screenshot" /></a>
			    	</div>
    				<p>The most popular format to exchange geospatial data between geographic information
    				    systems (GIS) is the ESRI shapefile, a vector data format to represent points, polylines
    				    and polygons. <strong>Tabels</strong>, empowered by the GeoTools library, is able to transform the information
    				    encoded in a shapefile into a partial RDF representation. On the one hand, 
    				    features are actually captured as RDF resources. On the other, their spatial
    				    geometries are transformed to a KML standard representation. In addition, there
    				    is another relevant information in a shapefile to draw and interpret the map:
    				    the style (icons, colors, line format, etc.). These styles must be originally 
    				    expressed in the XML-based SLD standard (Styled Layer Descriptor) defined by the OGC
    				    consortium.</p>
    				<p>To visualize these geospatial data, <strong>Tabels</strong> also integrates the Undermaps (Understanding Maps) 
    				    component. In a similar fashion as Understats works with multidimensional data described 
    				    with the Data Cube vocabulary, these HTML5 front-end widgets produce web-map visualizations 
    				    of geographic and cartographic information.</p>
    	    		<p>Undermaps is sensitive to geospatial data expressed in the following way:</p>
    	    		<ul>
	    		        <li>Features are RDF resources and are linked to their geometries (a KML file)
	    		            through the NeoGeo Vocabulary property: 
	    		            <a href="http://geovocab.org/geometry#Geometry" title="neogeo:geometry"><code>neogeo:geometry</code></a>. 
	    		            In addition, Undermaps
	    		            also supports the <a href="http://www.w3.org/2003/01/geo/">W3C Basic Geo Vocabulary</a>
	    		            in case of points.</li>
	    		        <li>(Spatial) Types of features should be represented as SKOS concepts and these
	    		            relationship is done with the Dublin Core property: 
	    		            <a href="http://purl.org/dc/terms/subject" title="dct:subject"><code>dct:subject</code></a></li>
	    		        <li>Map styles, comprising the legend, are related to the SKOS hierarchy. An style 
	    		            can be a SVG-designed symbol or a Google Maps JSON. The relationship between 
	    		            an SKOS concept and its map-style is made for the moment via an ad-hoc property called:
	    		            <code>http://disaster-fp7.eu/ontology/core#prefStyle</code>.</li>    
    	    		</ul>
    	    		<p>For instance: <g:link action="index" id="safetyRegions">Safety regions project</g:link> and <g:link action="index" id="FloraOfAsturias">Flora of Asturias project</g:link></p>
    	    		
    			</div>
			</div>

			<div class="right">
				<p>
	    	   		<g:link action="list" class="demo">Try Tabels Demo</g:link>	
		    	</p>
		    </div>
		    <div class="clear"></div>
		</div>
    </body>
</html>
