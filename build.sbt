lazy val commonSettings = Seq(
  organization := "de.tu-dresden.inf.lat.tabulas",
  version := "0.3.0-SNAPSHOT",
  scalaVersion := "2.12.1"
)

lazy val core = project.in(file("tabulas-core"))
  .settings(
    commonSettings
  )

lazy val ext = project.in(file("tabulas-ext"))
  .dependsOn(core)
  .settings(
    commonSettings
  )

lazy val root = project.in(file("."))
  .dependsOn(core, ext)
  .settings(
    commonSettings,
    mainClass in (Compile,run) := Some("de.tudresden.inf.lat.tabulas.ext.main.Main")
  )


