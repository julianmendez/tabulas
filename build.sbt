import sbt.Keys.scalacOptions


lazy val commonSettings = Seq(
  organization := "de.tu-dresden.inf.lat.tabulas",
  version := "1.1.0",

  /**
    * Scala
    * [[https://www.scala-lang.org]]
    * [[https://github.com/scala/scala]]
    * [[https://repo1.maven.org/maven2/org/scala-lang/scalap/]]
    */
  scalaVersion := "2.13.2",

  /**
    * ScalaTest
    * [[http://www.scalatest.org]]
    * [[https://github.com/scalatest/scalatest]]
    * [[https://repo1.maven.org/maven2/org/scalatest/scalatest_2.13/]]
    */
  libraryDependencies += "org.scalatest" %% "scalatest" % "3.1.1" % "test",

  resolvers += Resolver.mavenLocal,
  publishTo := Some(Resolver.mavenLocal),
  publishMavenStyle := true,
  scalacOptions ++= Seq("-deprecation", "-feature")
)

lazy val tabulas_core = project
  .withId("tabulas-core")
  .in(file("tabulas-core"))
  .settings(
    commonSettings
  )

lazy val tabulas_ext = project
  .withId("tabulas-ext")
  .in(file("tabulas-ext"))
  .aggregate(tabulas_core)
  .dependsOn(tabulas_core)
  .settings(
    commonSettings,

    /**
      * JSON parser
      * [[https://github.com/ralfstx/minimal-json]]
      * [[https://repo1.maven.org/maven2/com/eclipsesource/minimal-json/minimal-json/]]
      */
    libraryDependencies += "com.eclipsesource.minimal-json" % "minimal-json" % "0.9.5",

    /**
      * YAML 1.2 parser
      * [[https://bitbucket.org/asomov/snakeyaml-engine]]
      * [[https://repo1.maven.org/maven2/org/snakeyaml/snakeyaml-engine/]]
      */
    libraryDependencies += "org.snakeyaml" % "snakeyaml-engine" % "1.0"
  )

lazy val tabulas_distribution = project
  .withId("tabulas-distribution")
  .in(file("tabulas-distribution"))
  .aggregate(tabulas_core, tabulas_ext)
  .dependsOn(tabulas_core, tabulas_ext)
  .settings(
    commonSettings,
    mainClass in assembly := Some("de.tudresden.inf.lat.tabulas.ext.main.Main"),
    assemblyJarName in assembly := "tabulas-" + version.value + ".jar"
  )

lazy val root = project
  .withId("tabulas-parent")
  .in(file("."))
  .aggregate(tabulas_distribution)
  .dependsOn(tabulas_distribution)
  .settings(
    commonSettings,
    mainClass in(Compile, run) := Some("de.tudresden.inf.lat.tabulas.ext.main.Main")
  )


