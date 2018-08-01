package de.tudresden.inf.lat.tabulas.main

import de.tudresden.inf.lat.tabulas.extension.{DefaultExtension, Extension, NormalizationExtension}

import scala.collection.mutable

/** This is the main class.
  */
object Main {

  /** Entry point for the console.
    *
    * @param args
    * console arguments
    */
  def main(args: Array[String]): Unit = {
    val extensions = new mutable.ArrayBuffer[Extension]()
    extensions += new DefaultExtension()
    extensions += new NormalizationExtension()

    val instance: ConsoleStarter = new ConsoleStarter()
    instance.run(extensions, args)
  }

}
