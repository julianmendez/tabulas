
package de.tudresden.inf.lat.tabulas.ext.renderer

import java.io.{BufferedWriter, FileReader, FileWriter, IOException}
import java.util.Objects

import de.tudresden.inf.lat.tabulas.extension.Extension
import de.tudresden.inf.lat.tabulas.parser.SimpleFormatParser

/** This models an extension that writes the output in SQL.
  *
  */
case class SqlExtension() extends Extension {

  final val Name: String = "sql"
  final val Help: String = "(input) (output) : create an SQL dump file"
  final val RequiredArguments: Int = 2

  override def process(arguments: Seq[String]): Boolean = {
    val result = if (Objects.isNull(arguments) || arguments.size != RequiredArguments) {
      false
    } else {
      val res = try {

        val inputFileName = arguments(0)
        val outputFileName = arguments(1)
        val tableMap = new SimpleFormatParser(new FileReader(inputFileName)).parse()
        val output = new BufferedWriter(new FileWriter(outputFileName))
        val renderer = new SqlRenderer(output)
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
