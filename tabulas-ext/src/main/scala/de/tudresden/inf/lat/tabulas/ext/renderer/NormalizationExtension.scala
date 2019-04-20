
package de.tudresden.inf.lat.tabulas.ext.renderer

import java.io.{BufferedWriter, FileReader, FileWriter}
import java.util.Objects

import de.tudresden.inf.lat.tabulas.ext.parser.{JsonParser, MultiParser, YamlParser}
import de.tudresden.inf.lat.tabulas.extension.Extension
import de.tudresden.inf.lat.tabulas.parser.SimpleFormatParser
import de.tudresden.inf.lat.tabulas.renderer.SimpleFormatRenderer

import scala.util.Try

/** Normalization extension. It reads and writes using the same file.
  *
  */
case class NormalizationExtension() extends Extension {

  final val Name: String = "normalize"
  final val Help: String = "(input) : normalize a Tabula/Properties file (this is the default format)"
  final val RequiredArguments: Int = 1

  override def process(arguments: Seq[String]): Try[Boolean] = Try {
    val result = if (Objects.isNull(arguments) || arguments.size != RequiredArguments) {
      false

    } else {
      val inputFileName = arguments(0)
      val outputFileName = inputFileName
      val tableMap = MultiParser(
        Seq(SimpleFormatParser(), JsonParser(), YamlParser())
      ).parse(new FileReader(inputFileName)).get
      val output = new BufferedWriter(new FileWriter(outputFileName))
      val renderer = SimpleFormatRenderer()
      renderer.render(output, tableMap)
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
