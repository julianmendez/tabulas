package de.tudresden.inf.lat.tabulas.ext.main

import de.tudresden.inf.lat.tabulas.ext.parser.{CalendarParserExtension, CsvParserExtension, JsonParserExtension}
import de.tudresden.inf.lat.tabulas.ext.renderer._
import de.tudresden.inf.lat.tabulas.extension.{DefaultExtension, NormalizationExtension, OldFormatExtension}
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
      DefaultExtension(),
      OldFormatExtension(),
      JsonParserExtension(),
      CsvParserExtension(),
      CalendarParserExtension(),
      JsonExtension(),
      YamlExtension(),
      WikitextExtension(),
      SqlExtension(),
      CsvExtension(),
      HtmlExtension(),
      NormalizationExtension()
    )

    val instance: ConsoleStarter = new ConsoleStarter()
    instance.run(extensions, args)
  }

}

