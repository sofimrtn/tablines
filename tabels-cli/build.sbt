import AssemblyKeys._ // put this at the top of the file

assemblySettings

test in assembly := {}

// uncomment to exclude scala libraries
// assembleArtifact in packageScala := false

excludedJars in assembly <<= (fullClasspath in assembly) map { cp =>
  cp filter {x =>  Set(
  "servlet-api-2.5-20081211.jar",
  "ant-1.6.5.jar",
  "jsp-api-2.0.jar", 
  "stax-api-1.0.1.jar",
  "asm-3.2.jar", 
  "javax.servlet-2.5.0.v201103041518.jar", 
  "commons-beanutils-1.7.0.jar",
  "commons-beanutils-core-1.8.0.jar", 
  "minlog-1.2.jar",
  "xml-apis-1.3.04.jar",
  "xmlParserAPIs-2.0.2.jar",
  "commons-beanutils-1.8.3.jar").contains(x.data.getName)}
}

mergeStrategy in assembly <<= (mergeStrategy in assembly) { (old) =>
  {
    // case PathList("javax", "servlet", xs @ _*) => MergeStrategy.first
    case "application.conf" => MergeStrategy.concat
    case ".gitignore"     => MergeStrategy.discard
    case "log4j.properties"     => MergeStrategy.first
    case "about.html" => MergeStrategy.discard
    case "overview.html" => MergeStrategy.discard
    case "logback.xml" =>MergeStrategy.first
    case "META-INF/ECLIPSEF.RSA" => MergeStrategy.first
    case "META-INF/registryFile.jai" => MergeStrategy.first
    case "about.properties" => MergeStrategy.first
    case "plugin.properties" => MergeStrategy.first
    case "plugin.xml" => MergeStrategy.first
    case "about.mappings" => MergeStrategy.first
    case x => old(x)
  }
}

name := "tabels-cli"

version := "0.6-SNAPSHOT"

organization := "es.ctic.tabels"

scalaVersion := "2.9.1"

scalacOptions += "-deprecation"

// tabels core
libraryDependencies += "es.ctic.tabels" %% "tabels-core" % "0.6-SNAPSHOT"

//Logger tools -> BSD
libraryDependencies += "org.clapper" %% "grizzled-slf4j" % "0.6.10"

//Command line interface -> Apache License Version 2.0
libraryDependencies += "commons-cli" % "commons-cli" % "1.2"

// troubleshooting: log4j#log4j;1.2.16   -> The Apache Software License, Version 2.0
libraryDependencies += "log4j" % "log4j" % "1.2.16"

artifact in (Compile, assembly) ~= { art =>
  art.copy(`classifier` = Some("assembly"))
}

addArtifact(artifact in (Compile, assembly), assembly)

publishTo := Some(Resolver.file("My local maven repo", file(Path.userHome + "/.m2/repository")))

publishTo <<= (version) { version: String =>
  val nexus = "http://devit.fundacionctic.org:8081/nexus/content/repositories/"
  if (version.trim.endsWith("SNAPSHOT")) Some("snapshots" at nexus + "snapshots/")
  else                                   Some("releases"  at nexus + "releases/")
}

credentials += Credentials(Path.userHome / ".ivy2" / ".credentials")

resolvers += "ctic-nexus public" at
"http://repository.fundacionctic.org/content/groups/public/"



