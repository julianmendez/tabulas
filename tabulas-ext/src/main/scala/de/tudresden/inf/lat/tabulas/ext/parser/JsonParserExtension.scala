package de.tudresden.inf.lat.tabulas.ext.parser

import java.io.{BufferedWriter, FileReader, FileWriter}
import java.util.Objects

import de.tudresden.inf.lat.tabulas.ext.renderer.JsonRenderer
import de.tudresden.inf.lat.tabulas.extension.Extension
import de.tudresden.inf.lat.tabulas.parser.ParserConstant

import scala.util.Try

/** This models an extension that reads a JSON file and writes the default format.
 */
case class JsonParserExtension() extends Extension {

  final val Name: String = "parsejson"
  final val Help: String = "(input) (output) : create a Tabula.Properties file by parsing a Tabula.JSON file"
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
      val tableMap = JsonParser().parse(new FileReader(inputFileName)).get
      if (tableMap.getTableIds.length != 1) {
        println(ParserConstant.WarningDeprecationOfMultipleTables)
      }
      val output = new BufferedWriter(new FileWriter(outputFileName))
      JsonRenderer().render(output, tableMap)
      true
    }
    result
  }

}

object JsonParserExtension {}
