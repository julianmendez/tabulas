package de.tudresden.inf.lat.tabulas.extension

import java.io.BufferedWriter
import java.io.FileReader
import java.io.FileWriter
import java.io.IOException
import java.util.List
import java.util.Objects

import de.tudresden.inf.lat.tabulas.parser.SimpleFormatParser
import de.tudresden.inf.lat.tabulas.renderer.SimpleFormatRenderer
import de.tudresden.inf.lat.tabulas.table.TableMap

/**
 * Normalization extension. It reads and writes using the same file.
 *
 */
class NormalizationExtension extends Extension {

  val Name: String = "normalize"
  val Help: String = "(input) : normalize a file with a simple text format"
  val RequiredArguments: Int = 1

  override def process(arguments: List[String]): Boolean = {
    if (Objects.isNull(arguments) || arguments.size() != RequiredArguments) {
      false
    } else {
      try {

        val inputFileName: String = arguments.get(0)
        val outputFileName: String = inputFileName
        val tableMap: TableMap = new SimpleFormatParser(new FileReader(
          inputFileName)).parse()
        val output: BufferedWriter = new BufferedWriter(new FileWriter(
          outputFileName))
        val renderer: SimpleFormatRenderer = new SimpleFormatRenderer(output)
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
