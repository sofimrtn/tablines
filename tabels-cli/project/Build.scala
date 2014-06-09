import sbt._
import Keys._

// Integration Tests (run with it:test), more information at
// https://github.com/harrah/xsbt/wiki/Testing

object B extends Build
{
  lazy val root =
    Project("root", file("."))
      .configs( IntegrationTest )
      .settings( Defaults.itSettings : _*)
//      .settings( libraryDependencies += specs )

//  lazy val junit = "org.scala-tools.testing" %% "specs" % "1.6.8" % "it,test"
}