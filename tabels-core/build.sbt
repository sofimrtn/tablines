name := "Tabels core"

version := "0.6-SNAPSHOT"

organization := "es.ctic.tabels"

scalaVersion := "2.9.1"

scalacOptions += "-deprecation"

//libraryDependencies += "org.slf4j" % "slf4j-log4j12" % "1.5.8"

libraryDependencies += "org.clapper" %% "grizzled-slf4j" % "0.6.10"

libraryDependencies += "org.scalatest" %% "scalatest" % "1.6.1" % "it,test"

libraryDependencies += "junit" % "junit" % "4.8.1" % "it,test"

libraryDependencies += "net.sourceforge.jexcelapi" % "jxl" % "2.6.12"

libraryDependencies += "org.daisy.libs" % "jstyleparser" % "1.7.0"

libraryDependencies += "net.sf.opencsv" % "opencsv" % "2.0"

//libraryDependencies += "net.sourceforge.nekohtml" % "nekohtml" % "1.9.15"

libraryDependencies += "nu.validator.htmlparser" % "htmlparser" % "1.2.1"

libraryDependencies += "com.hp.hpl.jena" % "jena" % "2.6.4"

libraryDependencies += "com.hp.hpl.jena" % "arq" % "2.8.8"

libraryDependencies += "com.hp.hpl.jena" % "tdb" % "0.8.10"

libraryDependencies += "commons-cli" % "commons-cli" % "1.2"

libraryDependencies += "commons-lang" % "commons-lang" % "2.6"

libraryDependencies += "commons-io" % "commons-io" % "2.1"

libraryDependencies += "commons-configuration" % "commons-configuration" % "1.7"

libraryDependencies += "org.scalaz" %% "scalaz-core" % "6.0.3"

libraryDependencies += "org.apache.lucene" % "lucene-core" % "3.4.0"

libraryDependencies += "org.apache.lucene" % "lucene-analyzers" % "3.4.0"

libraryDependencies += "org.apache.commons" % "commons-compress" % "1.0"

libraryDependencies += "com.linuxense" % "javadbf" % "0.4.0"

libraryDependencies += "org.jdom" % "jdom2" % "2.0.4"

// Geotools dependencies

libraryDependencies += "org.geotools" % "gt-shapefile" % "8.0-RC2"

libraryDependencies += "org.geotools" % "gt-swing" % "8.0-RC2" // FIXME Im not sure we will need this

libraryDependencies += "org.geotools" % "gt-epsg-hsql" % "8.0-RC2"

libraryDependencies += "org.geotools" % "gt-opengis" % "8.0-RC2"

libraryDependencies += "org.geotools.xsd" % "gt-xsd-kml" % "8.0-RC2"

//Classification dependencies

libraryDependencies += "nz.ac.waikato.cms.weka" % "weka-stable" % "3.6.6"

// troubleshooting: log4j#log4j;1.2.16
libraryDependencies += "log4j" % "log4j" % "1.2.16"

// undermaps-geotools

libraryDependencies += "es.ctic.undermaps" % "undermaps-geotools" % "0.4-SNAPSHOT"

//managedStyle := ManagedStyle.Maven

publishTo := Some(Resolver.file("My local maven repo", file(Path.userHome + "/.m2/repository")))

publishTo <<= (version) { version: String =>
  val nexus = "http://devit.fundacionctic.org:8081/nexus/content/repositories/"
  if (version.trim.endsWith("SNAPSHOT")) Some("snapshots" at nexus + "private-snapshots/") 
  else                                   Some("releases"  at nexus + "private-releases/")
}

credentials += Credentials(Path.userHome / ".ivy2" / ".credentials")

libraryDependencies += "org.odftoolkit" % "odfdom-java" % "0.8.7"

libraryDependencies += "xerces" % "xercesImpl" % "2.9.1"

resolvers += "3rd party repo" at
"http://devit.fundacionctic.org:8081/nexus/content/repositories/thirdparty/"

resolvers += "CTIC releases" at
"http://devit.fundacionctic.org:8081/nexus/content/repositories/private-releases/"

// undermaps-geotools resolver
resolvers += "CTIC snapshots" at
"http://devit.fundacionctic.org:8081/nexus/content/repositories/private-snapshots/"

// Geotools resolver
resolvers += "Open Source Geospatial Foundation Repository" at
"http://download.osgeo.org/webdav/geotools/"

// local mvn repo
resolvers += "Local Maven Repository" at "file:///home/gillagher/.m2/repository"

