package de.tudresden.inf.lat.tabulas.main

import de.tudresden.inf.lat.tabulas.extension.DefaultExtension

/** This is the main class.
  */
object Main {

  /** Entry point for the console.
    *
    * @param args console arguments
    */
  def main(args: Array[String]): Unit = {
    val extensions = Seq(
      DefaultExtension()
    )

    val instance: ConsoleStarter = ConsoleStarter()
    instance.run(extensions, args)
  }

}
