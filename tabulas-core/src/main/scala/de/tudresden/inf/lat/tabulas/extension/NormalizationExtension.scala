package de.tudresden.inf.lat.tabulas.extension

import java.io.{BufferedWriter, FileReader, FileWriter, IOException}
import java.util.Objects

import de.tudresden.inf.lat.tabulas.parser.SimpleFormatParser
import de.tudresden.inf.lat.tabulas.renderer.SimpleFormatRenderer
import de.tudresden.inf.lat.tabulas.table.TableMap

/** Normalization extension. It reads and writes using the same file.
  *
  */
class NormalizationExtension extends Extension {

  final val Name: String = "normalize"
  final val Help: String = "(input) : normalize a Tabula/Properties file (this is the default format)"
  final val RequiredArguments: Int = 1

  override def process(arguments: Seq[String]): Boolean = {
    val result: Boolean = if (Objects.isNull(arguments) || arguments.size != RequiredArguments) {
      false

    } else {
      try {

        val inputFileName = arguments(0)
        val outputFileName = inputFileName
        val tableMap: TableMap = SimpleFormatParser(new FileReader(
          inputFileName)).parse()
        val output: BufferedWriter = new BufferedWriter(new FileWriter(
          outputFileName))
        val renderer: SimpleFormatRenderer = SimpleFormatRenderer(output)
        renderer.render(tableMap)
        true

      } catch {
        case e: IOException => throw new ExtensionException(e.toString, e)
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

object NormalizationExtension {

  def apply(): NormalizationExtension = new NormalizationExtension

}
