name := "Tabels core"

version := "0.6-SNAPSHOT"

organization := "es.ctic.tabels"

scalaVersion := "2.9.1"

scalacOptions += "-deprecation"

//Logger tools -> BSD
libraryDependencies += "org.clapper" %% "grizzled-slf4j" % "0.6.10"

// Scala test tools -> Apache License Version 2.0
libraryDependencies += "org.scalatest" %% "scalatest" % "1.6.1" % "it,test"

//Junit testing tools -> Common Public License Version 1.0
libraryDependencies += "junit" % "junit" % "4.8.1" % "it,test"

//Data input readers
//Excel reader ->  Apache License Version 2.0
//Solved problem with xmlbeans 2.3.0 - dependency conflict when using tabels core in tabels web project
//MORE INFO: https://issues.apache.org/jira/browse/XMLBEANS-484
//MORE INFO: http://stackoverflow.com/questions/6097279/java-lang-linkageerror-loader-constraint-violation-in-grails-project
//MORE INFO: http://mail-archives.apache.org/mod_mbox/xmlbeans-dev/201207.mbox/%3C853963532.44477.1342130314758.JavaMail.jiratomcat@issues-vm%3E
libraryDependencies += "org.apache.poi" % "poi-ooxml" % "3.10-beta2" exclude("org.apache.xmlbeans", "xmlbeans")

//To import a newer version of the one excluded from apache.poi which has solved the problem
libraryDependencies += "org.apache.xmlbeans" % "xmlbeans" % "2.6.0"

//ODF reader -> Apache License Version 2.0
libraryDependencies += "org.odftoolkit" % "odfdom-java" % "0.8.7"

//XML -> Apache License Version 2.0
libraryDependencies += "xerces" % "xercesImpl" % "2.9.1"  // Apache Software License

//CSV reader -> Apache License Version 2.0
libraryDependencies += "net.sf.opencsv" % "opencsv" % "2.0"

//html reader -> The MIT License
libraryDependencies += "nu.validator.htmlparser" % "htmlparser" % "1.2.1"

//Jena -> BSD license
libraryDependencies += "com.hp.hpl.jena" % "jena" % "2.6.4"

libraryDependencies += "com.hp.hpl.jena" % "arq" % "2.8.8"

libraryDependencies += "com.hp.hpl.jena" % "tdb" % "0.8.10"

//Command line interface -> Apache License Version 2.0
libraryDependencies += "commons-cli" % "commons-cli" % "1.2"

//Java utility classes -> Apache License Version 2.0
libraryDependencies += "commons-lang" % "commons-lang" % "2.6"

libraryDependencies += "commons-io" % "commons-io" % "2.1"

libraryDependencies += "commons-configuration" % "commons-configuration" % "1.7"

//Lucene -> Apache License, Version 2.0
libraryDependencies += "org.apache.lucene" % "lucene-core" % "3.4.0"

libraryDependencies += "org.apache.lucene" % "lucene-analyzers" % "3.4.0"

libraryDependencies += "org.apache.commons" % "commons-compress" % "1.0"


// Dom library -> apache license
libraryDependencies += "org.jdom" % "jdom2" % "2.0.4"

//Geotools dependencies -> GNU Lesser General Public License
libraryDependencies += "org.geotools" % "gt-shapefile" % "8.0-RC2"

libraryDependencies += "org.geotools" % "gt-epsg-hsql" % "8.0-RC2"

libraryDependencies += "org.geotools" % "gt-opengis" % "8.0-RC2"

libraryDependencies += "org.geotools.xsd" % "gt-xsd-kml" % "8.0-RC2"

//DBF reader -> GNU Lesser General Public License
libraryDependencies += "com.linuxense" % "javadbf" % "0.4.0"

// troubleshooting: log4j#log4j;1.2.16   -> The Apache Software License, Version 2.0
libraryDependencies += "log4j" % "log4j" % "1.2.16"

// undermaps-geotools -> The Apache Software License, Version 2.0
libraryDependencies += "es.ctic.undermaps" % "undermaps-geotools" % "0.6"

publishTo := Some(Resolver.file("My local maven repo", file(Path.userHome + "/.m2/repository")))

publishTo <<= (version) { version: String =>
  val nexus = "http://devit.fundacionctic.org:8081/nexus/content/repositories/"
  if (version.trim.endsWith("SNAPSHOT")) Some("snapshots" at nexus + "snapshots/")
  else                                   Some("releases"  at nexus + "releases/")
}

credentials += Credentials(Path.userHome / ".ivy2" / ".credentials")

resolvers += "ctic-nexus public" at
"http://repository.fundacionctic.org/content/groups/public/"

// Geotools resolver
resolvers += "Open Source Geospatial Foundation Repository" at
"http://download.osgeo.org/webdav/geotools/"


