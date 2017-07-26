package de.tudresden.inf.lat.tabulas.ext.parser

import java.io.{BufferedWriter, FileReader, FileWriter, IOException}
import java.util.Objects

import de.tudresden.inf.lat.tabulas.extension.Extension
import de.tudresden.inf.lat.tabulas.renderer.SimpleFormatRenderer
import de.tudresden.inf.lat.tabulas.table.TableMap

import scala.collection.mutable.Buffer

/**
  * This models an extension that reads comma-separated values and writes them
  * with the default format.
  *
  */
class CsvParserExtension extends Extension {

  val Name: String = "parsecsv"
  val Help: String = "(input) (output) : create output file with a simple text format parsing a comma-separated values (CSV) file"
  val RequiredArguments: Int = 2

  override def process(arguments: Buffer[String]): Boolean = {
    var result: Boolean = false
    if (Objects.isNull(arguments) || arguments.size != RequiredArguments) {
      result = false
    } else {
      try {

        val inputFileName: String = arguments(0)
        val outputFileName: String = arguments(1)
        val tableMap: TableMap = new CsvParser(new FileReader(
          inputFileName)).parse()
        val output: BufferedWriter = new BufferedWriter(new FileWriter(
          outputFileName))
        val renderer: SimpleFormatRenderer = new SimpleFormatRenderer(output)
        renderer.render(tableMap)
        result = true

      } catch {
        case e: IOException => {
          throw new RuntimeException(e)
        }
      }
    }
    return result
  }

  override def getExtensionName: String = {
    return Name
  }

  override def getHelp: String = {
    return Help
  }

  override def getRequiredArguments: Int = {
    return RequiredArguments
  }

}
