
package de.tudresden.inf.lat.tabulas.ext.renderer

import java.io._
import java.util.Objects

import de.tudresden.inf.lat.tabulas.ext.parser.MultiParser
import de.tudresden.inf.lat.tabulas.extension.Extension
import de.tudresden.inf.lat.tabulas.parser.ParserConstant

import scala.util.Try

/** This models an extension that writes an example of Tabula.YAML.
 *
 */
case class ExampleExtension() extends Extension {

  final val Name: String = "example"
  final val Help: String = "(output) : create a Tabula.YAML example file"
  final val RequiredArguments: Int = 1
  final val ExampleFileName = "/resources/example.tab.yaml"

  override val getExtensionName: String = Name

  override val getHelp: String = Help

  override val getRequiredArguments: Int = RequiredArguments

  override def process(arguments: Seq[String]): Try[Boolean] = Try {
    val result = if (Objects.isNull(arguments) || arguments.size != RequiredArguments) {
      false
    } else {
      val inputFileName = ExampleFileName
      val inputStream = getClass.getResourceAsStream(inputFileName)
      val outputFileName = arguments(0)
      val tableMap = MultiParser().parse(new InputStreamReader(inputStream)).get
      if (tableMap.getTableIds.length != 1) {
        println(ParserConstant.WarningDeprecationOfMultipleTables)
      }
      val output = new BufferedWriter(new FileWriter(outputFileName))
      YamlRenderer().render(output, tableMap)
      true
    }
    result
  }

}

object ExampleExtension {}
