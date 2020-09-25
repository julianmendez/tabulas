
package de.tudresden.inf.lat.tabulas.ext.renderer

import java.io.{BufferedWriter, FileReader, FileWriter}
import java.util.Objects

import de.tudresden.inf.lat.tabulas.ext.parser.{JsonParser, MultiParser, YamlParser}
import de.tudresden.inf.lat.tabulas.extension.Extension
import de.tudresden.inf.lat.tabulas.parser.{ParserConstant, SimpleFormatParser}
import de.tudresden.inf.lat.tabulas.renderer.SimpleFormatRenderer

import scala.util.Try

/** Default extension. It reads and writes using the default format.
 *
 */
case class OldFormatExtension() extends Extension {

  final val Name: String = "oldformat"
  final val Help: String = "(input) (output) : create an old Tabula.Properties file, i.e. using the equals sign instead of colon"
  final val RequiredArguments: Int = 2

  override val getExtensionName: String = Name

  override val getHelp: String = Help

  override val getRequiredArguments: Int = RequiredArguments

  override def process(arguments: Seq[String]): Try[Boolean] = Try {
    val result: Boolean = if (Objects.isNull(arguments) || arguments.size != RequiredArguments) {
      false
    } else {
      val inputFileName = arguments(0)
      val outputFileName = arguments(1)
      val tableMap = MultiParser(
        Seq(SimpleFormatParser(), JsonParser(), YamlParser())
      ).parse(new FileReader(inputFileName)).get
      val output = new BufferedWriter(new FileWriter(outputFileName))
      val renderer = SimpleFormatRenderer(ParserConstant.Space + ParserConstant.EqualsFieldSign)
      renderer.render(output, tableMap)
      true
    }
    result
  }

}

object OldFormatExtension {}
