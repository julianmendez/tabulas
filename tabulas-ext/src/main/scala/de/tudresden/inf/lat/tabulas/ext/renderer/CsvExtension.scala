
package de.tudresden.inf.lat.tabulas.ext.renderer

import java.io.{BufferedWriter, FileReader, FileWriter}
import java.util.Objects

import de.tudresden.inf.lat.tabulas.ext.parser.MultiParser
import de.tudresden.inf.lat.tabulas.extension.Extension

import scala.util.Try

/** This models an extension that writes the output in comma-separated values.
  *
  */
case class CsvExtension() extends Extension {

  final val Name: String = "csv"
  final val Help: String = "(input) (output) : create a comma-separated values (CSV) file"
  final val RequiredArguments: Int = 2

  override def process(arguments: Seq[String]): Try[Boolean] = Try {
    val result = if (Objects.isNull(arguments) || arguments.size != RequiredArguments) {
      false
    } else {
      val inputFileName = arguments(0)
      val outputFileName = arguments(1)
      val tableMap = MultiParser(new FileReader(inputFileName)).parse().get
      val output = new BufferedWriter(new FileWriter(outputFileName))
      val renderer = CsvRenderer(output)
      renderer.render(tableMap)
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
