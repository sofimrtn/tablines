Tablines
========

This repository is a fork from a project developed by the `CTIC Foundation <http://www.fundacionctic.org/>` called Tabels. It is formed by 3 dependent modules: Tabels-core, Tabels-CLI and Tabels-Web.

Tabels-Core 
===========


The tool has been implemented in Scala language under the terms of `Apache License, version 2.0 <http://www.apache.org/licenses/LICENSE-2.0.html>`.  
Tablines helds the capability to map from supported tabular formats to RDF documents; the mapping between them should be specified by a ``transformation program`` by the means of a DSL developed with the only purpose of these mappings. 

Valid input formats:
  * Excel files: .xls, .xlsx
  * Open Document Format files: .odf
  * Coma Separated Value files: .csv
  * PX files: .px
  * HTML tables: .html
  * Shape files: .shp.zip 
  * RDF files: .rdf

Technological Enviroment:
  * Scala 2.9.1
  * sbt 0.11.3 or higher

Dependencies:
  * grizzled-slf4j 0.6.10
  * scalatest 1.6.1
  * junit 4.8.1
  * poi-ooxml 3.10-beta2
  * xmlbeans 2.6.0
  * odfdom-java 0.8.7
  * xercesImpl 2.9.1
  * opencsv 2.0
  * htmlparser 1.2.1
  * jena 2.6.4
  * arq 2.8.8
  * tdb 0.8.10
  * commons-cli 1.2
  * commons-lang 2.6
  * commons-io 2.1
  * commons-configuration 1.7
  * lucene-core 3.4.0
  * lucene-analyzers 3.4.0
  * commons-compress 1.0
  * jdom2 2.0.4
  * gt-shapefile 8.0-RC2
  * gt-epsg-hsql 8.0-RC2
  * gt-opengis 8.0-RC2
  * gt-xsd-kml 8.0-RC2
  * javadbf 0.4.0
  * log4j 1.2.16
  * undermaps-geotools 0.4-SNAPSHOT
  
Running Tabels Core
===================
Once tablines has been downloaded from the repository it can be used through the command line::

  sbt "run_main es.ctic.tabels.CLI [OPTIONS] [SPREADSHEET FILES]"

The options suported are: 
  * -t: path to the input Tabels program
  * -o: path to the output RDF file
  * -d: path to the ouput debug HTML file


=========================================================================================================

Tablines Web
==========

This is a demo for the interoperability capabilities of a group of technologies related to Linked Data, tabels web is a platform, grails powered, which integrates online tabels core, tapinos-ws, tapinos-js, undermaps and a bunch of linked data explotation tools.

Technological Enviroment:
  * Grails 2.0.4 

Dependencies:
  * uploadr 0.6.0
  * svn 1.0.2
  * jquery 1.7.1
  * fancybox 1.3.4
  * jquery-geturlparam 2.1
  * jquery-datatables 1.8.2
  * jquery-tipsy 1.0.0a
  * jquery-tooltip 1.3
  * jquery-ui 1.8.15
  * resources 1.1.6
  * mail 1.0
  * tabels-core_2.9.1 0.6-SNAPSHOT
  * undermaps-ws 0.5-SNAPSHOT
  * understats-ws 2.6-SNAPSHOT
  * commons-lang 2.6
  * commons-io 2.1
  * commons-httpclient 3.0
  * scala-library 2.9.2
  * su4j-endpoint 1.2
  * pubby 0.3.3.1
  * jena 2.6.4
  * arq 2.8.8

Running Tablines Web
==================

To run tabels web on a localhost execute the grails command on the project folder::

  grails run-app

Otherwise, it is posible to generate a .war file to be deployed at any application server with the grails command::

  grails war
