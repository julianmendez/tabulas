package de.tudresden.inf.lat.tabulas.ext.main

import scala.collection.mutable.ArrayBuffer
import scala.collection.mutable.Buffer

import scala.Range

import de.tudresden.inf.lat.tabulas.main.ConsoleStarter
import de.tudresden.inf.lat.tabulas.ext.parser.CalendarParserExtension
import de.tudresden.inf.lat.tabulas.ext.parser.CsvParserExtension
import de.tudresden.inf.lat.tabulas.ext.renderer.CsvExtension
import de.tudresden.inf.lat.tabulas.ext.renderer.HtmlExtension
import de.tudresden.inf.lat.tabulas.ext.renderer.SqlExtension
import de.tudresden.inf.lat.tabulas.ext.renderer.WikitextExtension
import de.tudresden.inf.lat.tabulas.extension.DefaultExtension
import de.tudresden.inf.lat.tabulas.extension.Extension
import de.tudresden.inf.lat.tabulas.extension.ExtensionManager
import de.tudresden.inf.lat.tabulas.extension.NormalizationExtension


/**
 * This is the main class.
 */
object Main {

	/**
	 * Entry point for the console.
	 * 
	 * @param args
	 *            console arguments
	 */
  def main(args: Array[String]): Unit = {
    val extensions: Buffer[Extension] = new ArrayBuffer[Extension]()
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

