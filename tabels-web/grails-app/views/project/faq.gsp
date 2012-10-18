<html>
    <head>
        <title>Tabels project - FAQ</title>
        <meta name="layout" content="main" />
    </head>
    <body>
    	<div class="projectListBox homePage">
    		<h2>Frequently Asked Questions</h2>
    		
    		<ul>
    			<li><a href="#license">What is the Tabels license?</a></li>
    			<li><a href="#openSource">Is Tabels available as open source?</a></li>
    			<li><a href="#clean">If I create a project, will it stay online forever?</a></li>
    			<li><a href="#privateData">If I upload some private data, will it be publicly available?</a></li>
    			<li><a href="#saveProjects">How can I save my projects?</a></li>
    			<li><a href="#modifyProject">Why can't I modify the project?</a></li>
    			<li><a href="#chartView">Chart view does not work, any idea why is this happening?</a></li>
    			<li><a href="#mapView">Map view does not show any features.</a></li>
    			<li><a href="#1000results">Why only 1000 results are displayed under the faceted view?</a></li>
    			<li><a href="#contact">How can I contact you?</a></li>
    			<li><a href="#projectDoesNotExist">I clicked on a URI resource and got a "project does not exist" message.</a></li>
    			<li><a href="#firstSteps">I have already uploaded a file, what's next?</a></li>
    			<li><a href="#supportedFormats">Which input formats are supported by Tabels?</a></li>
    			<li><a href="#rdf">Can I upload my own RDF too?</a></li>
    		</ul>
    		
    		<h3 id="license">What is the Tabels license?</h3>            
            <p>Tabels is available as a free public online service.</p>

            <h3 id="openSource">Is Tabels available as open source?</h3>            
            <p>Not at this moment.</p>

            <h3 id="clean">If I create a project, will it stay online forever?</h3>            
            <p>No, we clean the projects every night. 
            Make sure you keep a local copy of the input and output data 
            and your transformation program.</p>
           
            <h3 id="privateData">If I upload some private data, will it be publicly available?</h3>            
            <p>All the projects in Tabels are public, including their input data. 
            You are the sole responsible for your data. 
            Take a look at the  <g:link action="disclaimer">disclaimer</g:link>.            
            </p>

            <h3 id="saveProjects">How can I save my projects?</h3>            
            <p>All the input files can be downloaded using the download button 
            (<img src="${resource(dir:'images',file:'uploader-download.png')}" alt="download file button">). 
            To download the transformation program you can copy the content using your favorite editor.</p>

            <h3 id="modifyProject">Why can't I modify the project?</h3>            
            <p>Tabels includes a number of sample 
            projects which can not be modified. 
            Every sample project is identified with the folder with lock icon
            (<img width="50px" src="${resource(dir:'images',file:'tabels-folder-readonly.png')}" alt="folder with lock icon">).
            If you create a new project, it will always be editable by you and any other user.
            </p>
            
            <h3 id="chartView">Chart view does not work, any idea why is this happening?</h3>            
            <p>Depending on the nature of the input files, Tabels produces different flavors of RDF. 
            Chart view only consumes DataCube-compliant data. 
            Take a look at the <g:link action="index" id="eurostats">eurostats</g:link> and 
            <g:link action="index" id="MouseStatsQB">MouseStatsQB</g:link> projects
            to see the chart view in action.
            </p>      
            
			<h3 id="mapView">Map view does not show any features.</h3>            
            <p>The map view can display any resource that includes geospatial information. 
            If you upload a shapefile and select the "MAPS" transformation program autogeneration, 
            this will produce map-visualizable RDF.
            </p>             
            
            <h3 id="1000results">Why only 1000 results are displayed under the faceted view?</h3>            
            <p>This limitation prevents the faceted view from presenting 
            the user a long list of results. Use the faceted view 
            to filter rows according to selected values of different attributes. 
            If you are interested in the "long list", the SPARQL endpoint 
            might be what you are looking for. And remember, this is an early prototype.
            </p>                   
            
            <h3 id="contact">How can I contact you?</h3>            
            <p>Please visit the <g:link action="contact">contact page</g:link> 
            for more details on contacting us. You can also input your feedback
            using the 
            </p>         
            
            <h3 id="projectDoesNotExist">I clicked on a URI resource and got a "project does not exist" message.</h3>            
            <p>Each transformation program is specific to a project. 
            If you are reusing a transformation program for a different project, 
            make sure you update the namespaces of the project. In the PREFIX <code>project:</code> 
            and <code>my:</code>, rename the last segment of the namespace with the name of your project.           
            </p>                             

            <h3 id="firstSteps">I have already uploaded a file, what's next?</h3>            
            <p>You can try an autogeneration of the transformation program 
            and select the most appropriate for your kind of data. 
            This program can be modified later. And if you feel brave enough 
            to start by yourself writing Tabels code or modify the autogenerated program, 
            do not forget to save it ;)             
            </p>                             

            <h3 id="supportedFormats">Which input formats are supported by Tabels?</h3>            
            <p>Tabels currently supports the following formats: 
            <ul>
            	<li>Spreadsheets: Microsoft Office (*.xls, *.xlsx) and Open Office (*.odf)</li>
            	<li>Comma separated values: *.csv</li>
            	<li>HTML tables: *.html</li>
            	<li>PC-Axis: *.px</li>
            	<li>Zipped ESRI shapefiles: *.shp.zip</li>
            	<li>RDF data in RDF/XML, Turtle and N3 formats: *.owl, *.rdf, *.ttl, *.n3, *.nt</li>
            </ul>            	             
            </p> 
            
            <!-- 
            <h3>Is it possible to modify the generated RDF?</h3>            
            <p>Tabels empowers users to freely modify the RDF output from the transformation
            by means of directives. See <g:link action="home" id="mediation">RDF mediation and reconciliation</g:link>
            for further information.                
            </p> 
             -->
             
            <h3 id="rdf">Can I upload my own RDF too?</h3>            
            <p>Definitively. You can upload your own RDF file. 
            Furthermore, you can merge the RDF from another project using the @LOAD directive.
            </p>                             
            
    	</div>
    </body>
</html>
