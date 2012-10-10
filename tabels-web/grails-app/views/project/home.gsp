<html>
    <head>
        <title>Tabels project</title>
        <meta name="layout" content="main" />
    </head>
    <body>
        <h2>Tabels project</h2>
        <p>Tabels (Tabular Cells) is an R&D tool, developed by CTIC, to make meaning of 
            tabular data. Tabels goes beyond spreadsheets and csv files to include other 
            formats such as PC-Axis, Shapefiles, analysis-tools formats and so forth. 
            Data tables are everywhere in our life and organizations. Calendars, maps, 
            forms, reports and charts are only some examples of this tables omnipresence.</p>
	<p>The aim is to provide means to discover and to surface the data structures hidden 
	    in tables, and to enable users to combine data past the limits of files and formats. 
	    By transforming data tables to RDF datasets, the information integration achieves 
	    a new dimension. Raw data transcends into a world of linked resources brimming 
	    with enrichment and entity reconciliation opportunities. Tabels is not a mere 
	    transformation tool, but it facilitates end-user exploitation of data by supplying 
	    interactive mechanisms.</p> 

	<h3>Tabels language</h3>
	<p>Tabels comes with its own DSL (Domain Specific Language) to define the transformations 
	    from data tables to RDF. This language allows to declaratively express mappings 
	    between the two worlds: tabels and graphs. A Tabels program comprises three different parts:</p>
	<ol>
	  <li>The PREFIX section: where namespaces and prefixes are declared in a similar fashion as 
	      the SPARQL language.</li>
	  <li>The data tables interpretation section: where the program specifies how 
	      to go through the tables according to four complementary dimensions: 
	      (1) the file dimension; (2) the sheet dimension; (3) the row dimension; and 
	      (4) the column dimension. In addition, the language enables to declaratively 
	      express the process of data structures by means of three kind of statements:
		<ul>
		  <li>Dimension statements: the SET operator is used to fix the dimension into 
		      a single value (for instance, to cover a particular file). On the other, 
		      the FOR operator is used to iterate through all the values comprising a 
		      given dimension (for instance, all the rows).</li>
		  <li>Conditional statements: the WHEN operator is used to restrict the iteration 
		      through data by specifying boolean conditions that must be fulfilled. The WHEN 
		      statements are semantically similar to IF/THEN rules.</li>
		  <li>Binding statements: the LET operator is used to assign a value to a new variable. 
		      This value may be direct values from the data tables or the result of another 
		      data operation. The MATCH operator provides a mechanism to defube tuples of variables 
		      which values are taken from the data tables themselves.</li>
		</ul>
	  </li>
	  <li>The RDF template section: where the program specifies the RDF graph pattern which 
	      variables are to be instantiated with the bindings obtained during the data tables 
	      processing. Graph patterns can be separated and are expressed in N3 syntax (in a similar 
	      fashion as SPARQL language).<li>
	</ol>
	<p>The Tabels language also provides auxiliary machinery to filter and compare data values during 
	    the transformation process. There are a number of already defined functions to this end, such 
	    as the XPath operations.

	<h3>Data mediation and manipulation</h3>
	<p> </p>	

	<h3>Entity recognition and reconciliation</h3>
		
	<h3>APIs and data access</h3>
	<p>FIXME </p>
	
	<h3>Charts and statistics</h3>
	<p>Tabels benefits from the Understats (Understanding Statistics) technology, 
	    an easy-to-deploy HTML5-based component which facilitates end-users an
	    interactive exploration of statistical and multidimensional data.</p>
	<p>Understats is an data-sensitive front-end that generates
	    responsive charts from the inspection of the dimensions and measures comprising
	    an statistical indicator represented with the RDF Data Cube vocabulary. Tabels
	    can be used then to automatically provide visualizations of statistical datasets 
	    published by national and international agencies, such as Eurostat, FAO or IFM ones,
	    typically encoded in csv, spreadsheets or even PC-Axis (*.px) formats.</p>		
	<h3>Maps and geographic data</h3>
	<p>Tabels also integrates the Undermaps (Understanding Maps) component. In a similar
	    fashion as Understats works with multidimensional data described with the Data Cube vocabulary,
	    these HTML5 front-end widgets produces web-map visualizations of geographic
	    and cartographic information. Undermaps is NeoGeo vocabulary FIXME</p>
	</p>Shapefiles FIXME</p>
	
    </body>
</html>
