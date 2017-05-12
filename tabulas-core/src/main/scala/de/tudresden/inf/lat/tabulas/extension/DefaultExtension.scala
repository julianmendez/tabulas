package de.tudresden.inf.lat.tabulas.extension

import java.io.{BufferedWriter, FileReader, FileWriter, IOException}
import java.util.Objects

import de.tudresden.inf.lat.tabulas.parser.SimpleFormatParser
import de.tudresden.inf.lat.tabulas.renderer.SimpleFormatRenderer
import de.tudresden.inf.lat.tabulas.table.TableMap

import scala.collection.mutable

/**
  * Default extension. It reads and writes using the default format.
  *
  */
class DefaultExtension extends Extension {

  val Name: String = "simple"
  val Help: String = "(input) (output) : parse and create output file with a simple text format"
  val RequiredArguments: Int = 2

  override def process(arguments: mutable.Buffer[String]): Boolean = {
    if (Objects.isNull(arguments) || arguments.size != RequiredArguments) {
      return false
    } else {
      try {

        val inputFileName: String = arguments(0)
        val outputFileName: String = arguments(1)
        val tableMap: TableMap = new SimpleFormatParser(new FileReader(
          inputFileName)).parse()
        val output: BufferedWriter = new BufferedWriter(new FileWriter(
          outputFileName))
        val renderer: SimpleFormatRenderer = new SimpleFormatRenderer(output)
        renderer.render(tableMap)
        return true

      } catch {
        case e: IOException => {
          throw new RuntimeException(e)
        }
      }
    }
  }

  override def getExtensionName(): String = {
    return Name
  }

  override def getHelp(): String = {
    return Help
  }

  override def getRequiredArguments(): Int = {
    return RequiredArguments
  }

}
