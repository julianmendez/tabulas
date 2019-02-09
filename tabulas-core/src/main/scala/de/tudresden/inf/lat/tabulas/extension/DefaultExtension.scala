package de.tudresden.inf.lat.tabulas.extension

import java.io.{BufferedWriter, FileReader, FileWriter, IOException}
import java.util.Objects

import de.tudresden.inf.lat.tabulas.parser.SimpleFormatParser
import de.tudresden.inf.lat.tabulas.renderer.SimpleFormatRenderer
import de.tudresden.inf.lat.tabulas.table.TableMap

/** Default extension. It reads and writes using the default format.
  *
  */
class DefaultExtension extends Extension {

  val Name: String = "simple"
  val Help: String = "(input) (output) : parse and create output file with a simple text format"
  val RequiredArguments: Int = 2

  override def process(arguments: Seq[String]): Boolean = {
    val result: Boolean = if (Objects.isNull(arguments) || arguments.size != RequiredArguments) {
      false
    } else {
      try {

        val inputFileName= arguments(0)
        val outputFileName = arguments(1)
        val tableMap: TableMap = SimpleFormatParser(new FileReader(
          inputFileName)).parse()
        val output: BufferedWriter = new BufferedWriter(new FileWriter(
          outputFileName))
        val renderer: SimpleFormatRenderer = SimpleFormatRenderer(output)
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

object DefaultExtension {

  def apply(): DefaultExtension = new DefaultExtension

}
