package de.tudresden.inf.lat.tabulas.ext.main

import de.tudresden.inf.lat.tabulas.ext.parser.{CalendarParserExtension, CsvParserExtension}
import de.tudresden.inf.lat.tabulas.ext.renderer.{CsvExtension, HtmlExtension, SqlExtension, WikitextExtension}
import de.tudresden.inf.lat.tabulas.extension.{DefaultExtension, Extension, NormalizationExtension}
import de.tudresden.inf.lat.tabulas.main.ConsoleStarter

import scala.collection.mutable
import scala.collection.mutable.ArrayBuffer


/**
  * This is the main class.
  */
object Main {

  /**
    * Entry point for the console.
    *
    * @param args
    * console arguments
    */
  def main(args: Array[String]): Unit = {
    val extensions: mutable.Buffer[Extension] = new ArrayBuffer[Extension]()
    extensions += new DefaultExtension()
    extensions += new CsvParserExtension()
    extensions += new CalendarParserExtension()
    extensions += new WikitextExtension()
    extensions += new SqlExtension()
    extensions += new CsvExtension()
    extensions += new HtmlExtension()
    extensions += new NormalizationExtension()

    val instance: ConsoleStarter = new ConsoleStarter()
    instance.run(extensions, args)
  }

}

