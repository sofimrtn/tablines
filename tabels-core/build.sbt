name := "Tabels core"

version := "0.1"

organization := "es.ctic.tabels"

scalaVersion := "2.9.1"

libraryDependencies += "org.slf4j" % "slf4j-log4j12" % "1.6.1"

libraryDependencies += "org.clapper" %% "grizzled-slf4j" % "0.6.6"

libraryDependencies += "org.scalatest" %% "scalatest" % "1.6.1" % "test"

libraryDependencies += "junit" % "junit" % "4.8.1" % "test"

libraryDependencies += "net.sourceforge.jexcelapi" % "jxl" % "2.6.12"

libraryDependencies += "net.sf.opencsv" % "opencsv" % "2.0"

libraryDependencies += "com.hp.hpl.jena" % "jena" % "2.6.4"

libraryDependencies += "commons-cli" % "commons-cli" % "1.1"

//managedStyle := ManagedStyle.Maven

publishTo := Some(Resolver.file("My local maven repo", file(Path.userHome + "/.m2/repository")))