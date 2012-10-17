<html>
    <head>
        <title>Tabels project - FAQ</title>
        <meta name="layout" content="main" />
    </head>
    <body>
    	<div class="projectListBox homePage">
    		<h2>Frequently Asked Questions</h2>
    		<h3>What is the end-user license for Tabels?</h3>            
            <p>Tabels is available as a free public online service.</p>

            <h3>Is Tabels available as open source?</h3>            
            <p>Not at this moment.</p>

            <h3>If I create a project, will it stay online forever?</h3>            
            <p>No, we clean the projects everynight. 
            Make sure you keep a local copy of the input files and the transformation program.</p>
           
            <h3>If I upload some sensitive data, will it be publicly available?</h3>            
            <p>All the projects in Tabels are public, including their input data. 
            Make sure you do not upload sensitive information to Tabels.
            </p>

            <h3>How can I save my projects?</h3>            
            <p>All the input files can be downloaded using the download button 
            (<img src="${resource(dir:'images',file:'uploader-download.png')}" alt="download file button">). 
            To download the transformation program you can copy the content using your favorite editor. </p>

            <h3>Why can't I modify the project?</h3>            
            <p>Tabels includes a number of example projects which can not be modified. Every example project
            is identified with the folder with lock icon 
            (<img width="50px" src="${resource(dir:'images',file:'tabels-folder-readonly.png')}" alt="folder with lock icon">).
            If you create a new project, it will always be editable.
            </p>
            
            <h3>Chart view does not work, any idea why is this happening?</h3>            
            <p>Depending on the nature of the input files, Tabels produces
            different flavors of RDF. Chart view only consumes RDF that uses the RDF Data Cube
            vocabulary. Take a look at <g:link action="list">eurostats example</g:link>
            to see the chart view in action.
            </p>      
            
			<h3>Map view does not show any features.</h3>            
            <p>The map view can display any resource that includes some geospatial information attached to it.
            If you upload a shapefile and select the "MAPS" transformation program autogeneration, This will
            often produce RDF that can be viewed under the map view.
            </p>             
            
            <h3>Why only 1000 results are displayed under the faceted view?</h3>            
            <p>This limitation prevents the faceted view from presenting the user a long list of results. Use the 
            faceted view to filter rows according to selected values of different attributes. If
            you are interested in the "long list", the SPARQL endpoint might be what you are looking for.
            </p>                   
            
    	</div>
    </body>
</html>
