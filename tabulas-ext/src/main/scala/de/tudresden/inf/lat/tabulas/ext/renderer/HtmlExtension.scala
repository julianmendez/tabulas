
package de.tudresden.inf.lat.tabulas.ext.renderer

import java.io.{BufferedWriter, FileReader, FileWriter}
import java.util.Objects

import de.tudresden.inf.lat.tabulas.ext.parser.MultiParser
import de.tudresden.inf.lat.tabulas.extension.Extension
import de.tudresden.inf.lat.tabulas.parser.ParserConstant

import scala.util.Try

/** This models an extension that writes the output in HTML.
 *
 */
case class HtmlExtension() extends Extension {

  final val Name: String = "html"
  final val Help: String = "(input) (output) : create an HTML file"
  final val RequiredArguments: Int = 2

  override val getExtensionName: String = Name

  override val getHelp: String = Help

  override val getRequiredArguments: Int = RequiredArguments

  override def process(arguments: Seq[String]): Try[Boolean] = Try {
    val result = if (Objects.isNull(arguments) || arguments.size != RequiredArguments) {
      false
    } else {
      val inputFileName = arguments(0)
      val outputFileName = arguments(1)
      val tableMap = MultiParser().parse(new FileReader(inputFileName)).get
      if (tableMap.getTableIds.length != 1) {
        println(ParserConstant.WarningDeprecationOfMultipleTables)
      }
      val output = new BufferedWriter(new FileWriter(outputFileName))
      HtmlRenderer().render(output, tableMap)
      true
    }
    result
  }

}

object HtmlExtension {}
