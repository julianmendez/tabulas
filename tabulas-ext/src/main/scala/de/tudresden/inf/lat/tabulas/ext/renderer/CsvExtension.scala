package de.tudresden.inf.lat.tabulas.ext.renderer

import java.io.BufferedWriter
import java.io.FileReader
import java.io.FileWriter
import java.io.IOException
import java.util.List

import de.tudresden.inf.lat.tabulas.extension.Extension
import de.tudresden.inf.lat.tabulas.table.TableMap
import de.tudresden.inf.lat.tabulas.parser.SimpleFormatParser

/**
 * This models an extension that writes the output in comma separated values.
 *
 */
class CsvExtension extends Extension {

  val Name: String = "csv"
  val Help: String = "(input) (output) : create output as a comma separated values (CSV) file"
  val RequiredArguments: Int = 2

  override def process(arguments: List[String]): Boolean = {
    if (arguments == null || arguments.size() != RequiredArguments) {
      false
    } else {
      try {

        val inputFileName: String = arguments.get(0)
        val outputFileName: String = arguments.get(1)
        val tableMap: TableMap = new SimpleFormatParser(new FileReader(
          inputFileName)).parse()
        val output: BufferedWriter = new BufferedWriter(new FileWriter(
          outputFileName))
        val renderer: CsvRenderer = new CsvRenderer(output)
        renderer.render(tableMap)
        true

      } catch {
        case e: IOException => {
          throw new RuntimeException(e)
        }
      }
    }
  }

  override def getExtensionName() = {
    Name
  }

  override def getHelp(): String = {
    Help
  }

  override def getRequiredArguments(): Int = {
    RequiredArguments
  }

}
