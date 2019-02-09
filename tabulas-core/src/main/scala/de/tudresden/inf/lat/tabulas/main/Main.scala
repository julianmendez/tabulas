package de.tudresden.inf.lat.tabulas.main

import de.tudresden.inf.lat.tabulas.extension.{DefaultExtension, NormalizationExtension, OldFormatExtension}

/** This is the main class.
  */
object Main {

  /** Entry point for the console.
    *
    * @param args console arguments
    */
  def main(args: Array[String]): Unit = {
    val extensions = Seq(
      DefaultExtension(),
      OldFormatExtension(),
      NormalizationExtension()
    )

    val instance: ConsoleStarter = new ConsoleStarter()
    instance.run(extensions, args)
  }

}
