
package de.tudresden.inf.lat.tabulas.ext.renderer

import java.io._
import java.util.Objects

import de.tudresden.inf.lat.tabulas.ext.parser.YamlParser
import de.tudresden.inf.lat.tabulas.extension.Extension

/** This models an extension that writes an example of Tabula/YAML.
  *
  */
case class ExampleExtension() extends Extension {

  final val Name: String = "example"
  final val Help: String = "(output) : create a Tabula/YAML example file"
  final val RequiredArguments: Int = 1
  final val ExampleFileName = "/resources/example.yaml"

  override def process(arguments: Seq[String]): Boolean = {
    val result = if (Objects.isNull(arguments) || arguments.size != RequiredArguments) {
      false
    } else {
      val res = try {

        val inputFileName = ExampleFileName
        val inputStream = getClass.getResourceAsStream(inputFileName)
        val outputFileName = arguments(0)
        val tableMap = new YamlParser(new InputStreamReader(inputStream)).parse()
        val output = new BufferedWriter(new FileWriter(outputFileName))
        val renderer = new YamlRenderer(output)
        renderer.render(tableMap)
        true

      } catch {
        case e: IOException => throw new RuntimeException(e)
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
