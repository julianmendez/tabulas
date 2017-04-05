

lazy val commonSettings = Seq(
  organization := "de.tu-dresden.inf.lat.tabulas",
  version := "0.3.0-SNAPSHOT",
  scalaVersion := "2.12.1",
  libraryDependencies += "junit" % "junit" % "4.12" % "test",
  libraryDependencies += "com.novocode" % "junit-interface" % "0.11" % "test"
)

lazy val core = project
  .in(file("tabulas-core"))
  .settings(commonSettings)

lazy val ext = project
  .in(file("tabulas-ext"))
  .aggregate(core)
  .dependsOn(core)
  .settings(commonSettings)

lazy val root = project
  .in(file("."))
  .aggregate(core, ext)
  .dependsOn(core, ext)
  .settings(
    commonSettings,
    mainClass in (Compile,run) := Some("de.tudresden.inf.lat.tabulas.ext.main.Main")
  )


