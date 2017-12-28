

lazy val commonSettings = Seq(
  organization := "de.tu-dresden.inf.lat.tabulas",
  version := "0.3.0-SNAPSHOT",
  scalaVersion := "2.12.3",
  libraryDependencies += "org.scalatest" %% "scalatest" % "3.0.3" % "test",
  resolvers += Resolver.mavenLocal,
  publishTo := Some(Resolver.mavenLocal),
  publishMavenStyle := true
)

lazy val core = project
  .in(file("tabulas-core"))
  .settings(
    commonSettings,
    name := "tabulas-core"
  )

lazy val ext = project
  .in(file("tabulas-ext"))
  .aggregate(core)
  .dependsOn(core)
  .settings(
    commonSettings,
    name := "tabulas-ext"
  )

lazy val dist = project
  .in(file("tabulas-distribution"))
  .aggregate(core, ext)
  .dependsOn(core, ext)
  .settings(
    commonSettings,
    name := "tabulas-distribution",
    mainClass in assembly := Some("de.tudresden.inf.lat.tabulas.ext.main.Main"),
    assemblyJarName in assembly := "tabulas-" + version.value + ".jar"
  )

lazy val root = project
  .in(file("."))
  .aggregate(dist)
  .dependsOn(dist)
  .settings(
    commonSettings,
    name := "tabulas-parent",
    mainClass in (Compile,run) := Some("de.tudresden.inf.lat.tabulas.ext.main.Main")
  )


