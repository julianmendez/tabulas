package de.tudresden.inf.lat.tabulas.ext.main

import de.tudresden.inf.lat.tabulas.ext.parser.CsvParserExtension
import de.tudresden.inf.lat.tabulas.ext.renderer._
import de.tudresden.inf.lat.tabulas.main.ConsoleStarter


/** This is the main class.
  */
object Main {

  /** Entry point for the console.
    *
    * @param args console arguments
    */
  def main(args: Array[String]): Unit = {
    val extensions = Seq(
      YamlExtension(),
      JsonExtension(),
      PropertiesExtension(),
      OldFormatExtension(),
      WikitextExtension(),
      SqlExtension(),
      CsvExtension(),
      HtmlExtension(),
      CsvParserExtension(),
      NormalizationExtension(),
      ReadmeExtension(),
      ExampleExtension()
    )

    val instance: ConsoleStarter = ConsoleStarter()
    instance.run(extensions, args)
  }

}

