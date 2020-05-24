
package de.tudresden.inf.lat.tabulas.ext.renderer

import java.io.{BufferedWriter, FileReader, FileWriter}
import java.util.Objects

import de.tudresden.inf.lat.tabulas.ext.parser.{JsonParser, MultiParser, YamlParser}
import de.tudresden.inf.lat.tabulas.extension.Extension
import de.tudresden.inf.lat.tabulas.parser.{ParserConstant, SimpleFormatParser}

import scala.util.Try

/** This models an extension that writes the output in Tabula.JSON.
  *
  */
case class JsonExtension() extends Extension {

  final val Name: String = "json"
  final val OptionNoMeta: String = "--nometa"
  final val Help: String = "[" + OptionNoMeta + "] (input) (output) : create a Tabula.Properties file," +
    " if the option " + OptionNoMeta + " is set, the metadata is not included"
  final val RequiredArguments: Int = 2

  override def process(arguments: Seq[String]): Try[Boolean] = Try {
    val result = if (Objects.isNull(arguments)
      || arguments.size < RequiredArguments
      || (arguments.size == RequiredArguments + 1 && !(arguments(0) == OptionNoMeta))
      || (arguments.size > RequiredArguments + 1)) {
      false
    } else {
      val withMetadata = !(arguments.size == RequiredArguments + 1 && arguments(0) == OptionNoMeta)
      val startIndex = arguments.size - RequiredArguments
      val inputFileName = arguments(startIndex)
      val outputFileName = arguments(startIndex + 1)
      val tableMap = MultiParser(
        Seq(JsonParser(), YamlParser(), SimpleFormatParser())
      ).parse(new FileReader(inputFileName)).get
      if (tableMap .getTableIds.length != 1) {
        println(ParserConstant.WarningDeprecationOfMultipleTables)
      }
      val output = new BufferedWriter(new FileWriter(outputFileName))
      JsonRenderer(withMetadata).render(output, tableMap)
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

object JsonExtension {}
