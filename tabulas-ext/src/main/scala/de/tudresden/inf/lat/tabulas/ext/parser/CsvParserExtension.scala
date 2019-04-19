package de.tudresden.inf.lat.tabulas.ext.parser

import java.io.{BufferedWriter, FileReader, FileWriter}
import java.util.Objects

import de.tudresden.inf.lat.tabulas.extension.Extension
import de.tudresden.inf.lat.tabulas.renderer.SimpleFormatRenderer
import de.tudresden.inf.lat.tabulas.table.TableMap

import scala.util.Try

/** This models an extension that reads comma-separated values and writes them
  * with the default format.
  *
  */
case class CsvParserExtension() extends Extension {

  final val Name: String = "parsecsv"
  final val Help: String = "(input) (output) : create a Tabula/Properties file by parsing a comma-separated values (CSV) file"
  final val RequiredArguments: Int = 2

  override def process(arguments: Seq[String]): Try[Boolean] = Try {
    val result = if (Objects.isNull(arguments) || arguments.size != RequiredArguments) {
      false
    } else {
      val inputFileName = arguments(0)
      val outputFileName = arguments(1)
      val tableMap = CsvParser(new FileReader(inputFileName)).parse().get
      val output = new BufferedWriter(new FileWriter(outputFileName))
      val renderer = SimpleFormatRenderer(output)
      renderer.render(tableMap)
      true
    }
    result
  }

  override def getExtensionName: String = {
    Name
  }

  override def getHelp: String = {
    Help
  }

  override def getRequiredArguments: Int = {
    RequiredArguments
  }

}
