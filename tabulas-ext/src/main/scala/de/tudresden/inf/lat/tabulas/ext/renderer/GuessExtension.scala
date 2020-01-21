
package de.tudresden.inf.lat.tabulas.ext.renderer

import java.io.{BufferedWriter, FileReader, FileWriter}
import java.util.Objects

import de.tudresden.inf.lat.tabulas.ext.parser.{JsonParser, MultiParser, YamlParser}
import de.tudresden.inf.lat.tabulas.extension.Extension
import de.tudresden.inf.lat.tabulas.parser.SimpleFormatParser

import scala.util.Try

/** This models an extension that writes the output in Tabula/YAML by reading a file with possibly undeclared fields.
  *
  */
case class GuessExtension() extends Extension {

  final val Name: String = "guess"
  final val Help: String = "(input) (output) : create a Tabula/YAML file by reading a file with possibly undeclared fields"
  final val RequiredArguments: Int = 2

  override def process(arguments: Seq[String]): Try[Boolean] = Try {
    val result = if (Objects.isNull(arguments) || arguments.size != RequiredArguments) {
      false
    } else {
      val inputFileName = arguments(0)
      val outputFileName = arguments(1)
      val tableMap = MultiParser(
        Seq(YamlParser(permissive = true), JsonParser(permissive = true), SimpleFormatParser(permissive = true))
      ).parse(new FileReader(inputFileName)).get
      val output = new BufferedWriter(new FileWriter(outputFileName))
      YamlRenderer().render(output, tableMap)
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

object GuessExtension {}