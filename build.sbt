

lazy val commonSettings = Seq(
  organization := "de.tu-dresden.inf.lat.tabulas",
  version := "0.3.0-SNAPSHOT",
  scalaVersion := "2.12.1",
  libraryDependencies += "org.scalatest" %% "scalatest" % "3.0.3" % "test",
  libraryDependencies += "junit" % "junit" % "4.12" % "test",
  libraryDependencies += "com.novocode" % "junit-interface" % "0.11" % "test"
)

lazy val core = project
  .in(file("tabulas-core"))
  .settings(
    name := "tabulas-core",
    commonSettings
  )

lazy val ext = project
  .in(file("tabulas-ext"))
  .aggregate(core)
  .dependsOn(core)
  .settings(
    name := "tabulas-ext",
    commonSettings
  )

lazy val dist = project
  .in(file("tabulas-distribution"))
  .aggregate(core, ext)
  .dependsOn(core, ext)
  .settings(
    name := "tabulas-distribution",
    mainClass in assembly := Some("de.tudresden.inf.lat.tabulas.ext.main.Main"),
    assemblyJarName in assembly := "tabulas-" + version.value + ".jar",
    commonSettings
  )

lazy val root = project
  .in(file("."))
  .aggregate(dist)
  .dependsOn(dist)
  .settings(
    name := "tabulas-parent",
    commonSettings,
    mainClass in (Compile,run) := Some("de.tudresden.inf.lat.tabulas.ext.main.Main")
  )


