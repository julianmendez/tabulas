
package de.tudresden.inf.lat.tabulas.ext.parser

import java.io.{BufferedWriter, FileReader, FileWriter, IOException}
import java.util.Objects

import de.tudresden.inf.lat.tabulas.extension.Extension
import de.tudresden.inf.lat.tabulas.renderer.SimpleFormatRenderer

/** This models an extension that reads a YAML file and writes the default format.
  */
case class YamlParserExtension() extends Extension {

  final val Name: String = "parseyaml"
  final val Help: String = "(input) (output) : create a Tabula/Properties file by parsing a Tabula/YAML file"
  final val RequiredArguments: Int = 2

  override def process(arguments: Seq[String]): Boolean = {
    val result = if (Objects.isNull(arguments) || arguments.size != RequiredArguments) {
      false
    } else {
      try {

        val inputFileName = arguments(0)
        val outputFileName = arguments(1)
        val tableMap = new YamlParser(new FileReader(inputFileName)).parse()
        val output = new BufferedWriter(new FileWriter(outputFileName))
        val renderer = SimpleFormatRenderer(output)
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