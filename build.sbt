import sbt.Keys.scalacOptions

lazy val scala2_13 = "2.13.10"

lazy val scala3_2 = "3.2.2"

lazy val commonSettings =
  Seq(
    organization := "de.tu-dresden.inf.lat.tabulas",
    normalizedName := "tabulas",
    version := "1.2.0-SNAPSHOT",
    name := "tabulas",
    description := "System to manage human-readable tables using files",
    homepage := Some(url("https://github.com/julianmendez/tabulas")),
    startYear := Some(2015),
    licenses := Seq("Apache License Version 2.0" -> url("https://www.apache.org/licenses/LICENSE-2.0.txt")),
    developers := List(
      Developer("julianmendez", "Julian Mendez", "julian.mendez@gmail.com", new URL("https://julianmendez.github.io") )
    ),
    /**
     * Scala
     * [[https://www.scala-lang.org]]
     * [[https://github.com/scala/scala]]
     * [[https://repo1.maven.org/maven2/org/scala-lang/scalap/]]
     * [[https://repo1.maven.org/maven2/org/scala-lang/scala3-compiler_3/]]
     */
    crossScalaVersions := Seq(scala2_13, scala3_2),
    scalaVersion := scala3_2,
    /**
     * ScalaTest
     * [[http://www.scalatest.org]]
     * [[https://github.com/scalatest/scalatest]]
     * [[https://repo1.maven.org/maven2/org/scalatest/]]
     */
    libraryDependencies += "org.scalatest" %% "scalatest" % "3.2.15" % "test",
    resolvers += Resolver.mavenLocal,
    publishTo := Some(Resolver.mavenLocal),
    publishMavenStyle := true,
    scalacOptions ++= Seq("-deprecation", "-feature")
  )

lazy val tabulas_core =
  project
    .withId("tabulas-core")
    .in(file("tabulas-core"))
    .settings(
      commonSettings
    )

lazy val tabulas_ext =
  project
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
      libraryDependencies += "org.snakeyaml" % "snakeyaml-engine" % "2.3"
    )

lazy val tabulas_distribution =
  project
    .withId("tabulas-distribution")
    .in(file("tabulas-distribution"))
    .aggregate(tabulas_core, tabulas_ext)
    .dependsOn(tabulas_core, tabulas_ext)
    .settings(
      commonSettings,
      assembly / mainClass := Some("de.tudresden.inf.lat.tabulas.ext.main.Main"),
      assembly / assemblyJarName := "tabulas-" + version.value + ".jar"
    )

lazy val root =
  project
    .withId("tabulas-parent")
    .in(file("."))
    .aggregate(tabulas_distribution)
    .dependsOn(tabulas_distribution)
    .settings(
      commonSettings,
      assembly / mainClass := Some("de.tudresden.inf.lat.tabulas.ext.main.Main")
    )

