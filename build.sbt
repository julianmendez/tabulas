

lazy val commonSettings = Seq(
  organization := "de.tu-dresden.inf.lat.tabulas",
  version := "0.3.0-SNAPSHOT",
  scalaVersion := "2.12.1",
  libraryDependencies += "org.scalatest" %% "scalatest" % "3.0.3" % "test",
  libraryDependencies += "junit" % "junit" % "4.12" % "test",
  libraryDependencies += "com.novocode" % "junit-interface" % "0.11" % "test",
  resolvers += Resolver.mavenLocal,
  publishTo := Some(Resolver.mavenLocal)
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


