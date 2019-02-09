package de.tudresden.inf.lat.tabulas.ext.parser

import java.io.{BufferedWriter, FileReader, FileWriter, IOException}
import java.util.Objects

import de.tudresden.inf.lat.tabulas.extension.Extension
import de.tudresden.inf.lat.tabulas.renderer.SimpleFormatRenderer
import de.tudresden.inf.lat.tabulas.table.TableMap

/** This models an extension that reads comma-separated values and writes them
  * with the default format.
  *
  */
class CsvParserExtension extends Extension {

  val Name: String = "parsecsv"
  val Help: String = "(input) (output) : create output file with a simple text format parsing a comma-separated values (CSV) file"
  val RequiredArguments: Int = 2

  override def process(arguments: Seq[String]): Boolean = {
    val result = if (Objects.isNull(arguments) || arguments.size != RequiredArguments) {
      false
    } else {
      try {

        val inputFileName = arguments(0)
        val outputFileName = arguments(1)
        val tableMap: TableMap = new CsvParser(new FileReader(
          inputFileName)).parse()
        val output = new BufferedWriter(new FileWriter(
          outputFileName))
        val renderer = SimpleFormatRenderer(output)
        renderer.render(tableMap)
        true

      } catch {
        case e: IOException => throw new RuntimeException(e)
      }
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

object CsvParserExtension {

  def apply(): CsvParserExtension = new CsvParserExtension

}
