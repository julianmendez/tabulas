package de.tudresden.inf.lat.tabulas.ext.main

import de.tudresden.inf.lat.tabulas.ext.parser.{CalendarParserExtension, CsvParserExtension}
import de.tudresden.inf.lat.tabulas.ext.renderer.{CsvExtension, HtmlExtension, SqlExtension, WikitextExtension}
import de.tudresden.inf.lat.tabulas.extension.{DefaultExtension, NormalizationExtension}
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
      CsvParserExtension(),
      CalendarParserExtension(),
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

