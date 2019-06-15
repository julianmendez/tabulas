

lazy val commonSettings = Seq(
  organization := "de.tu-dresden.inf.lat.tabulas",
  version := "1.0.0",
  scalaVersion := "2.12.8",
  libraryDependencies += "org.scalatest" %% "scalatest" % "3.0.5" % "test",
  resolvers += Resolver.mavenLocal,
  publishTo := Some(Resolver.mavenLocal),
  publishMavenStyle := true
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
    libraryDependencies += "com.eclipsesource.minimal-json" % "minimal-json" % "0.9.5",
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


