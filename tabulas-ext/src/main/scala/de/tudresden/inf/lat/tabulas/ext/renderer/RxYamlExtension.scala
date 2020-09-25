
package de.tudresden.inf.lat.tabulas.ext.renderer

import java.io.{BufferedWriter, FileReader, FileWriter}
import java.util.Objects

import de.tudresden.inf.lat.tabulas.ext.parser.{JsonParser, MultiParser, YamlParser}
import de.tudresden.inf.lat.tabulas.extension.Extension
import de.tudresden.inf.lat.tabulas.parser.SimpleFormatParser

import scala.util.Try

/** This extension exports the metadata as an Rx YAML schema.
 *
 */
case class RxYamlExtension() extends Extension {

  final val Name: String = "rxyaml"
  final val Help: String = "(input) (output) : given a Tabula file with exactly one table, " +
    "this extension exports the metadata of that table only as an Rx YAML schema. " +
    "See also Deprecation of Multiple Tables."

  final val RequiredArguments: Int = 2

  override def process(arguments: Seq[String]): Try[Boolean] = Try {
    val result = if (Objects.isNull(arguments) || arguments.size != RequiredArguments) {
      false
    } else {
      val inputFileName = arguments(0)
      val outputFileName = arguments(1)
      val tableMap = MultiParser(
        Seq(YamlParser(), JsonParser(), SimpleFormatParser())
      ).parse(new FileReader(inputFileName)).get
      val res = if (tableMap.getTableIds.length == 1) {
        val output = new BufferedWriter(new FileWriter(outputFileName))
        RxYamlRenderer().render(output, tableMap)
        true
      } else {
        false
      }
      res
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

object RxYamlExtension {}
