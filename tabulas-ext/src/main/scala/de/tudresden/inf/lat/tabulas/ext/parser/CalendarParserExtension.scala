
package de.tudresden.inf.lat.tabulas.ext.parser

import java.io.{BufferedWriter, FileReader, FileWriter, IOException}
import java.util.Objects

import de.tudresden.inf.lat.tabulas.extension.Extension
import de.tudresden.inf.lat.tabulas.renderer.SimpleFormatRenderer
import de.tudresden.inf.lat.tabulas.table.TableMap

/** This models an extension that reads comma-separated values and writes them
  * with the default format.
  *
  */
class CalendarParserExtension extends Extension {

  val Name: String = "parsecalendar"

  val Help: String = "(input) (output) : create output file with a simple text format parsing a calendar file"

  val RequiredArguments: Int = 2

  override def process(arguments: Seq[String]): Boolean = {
    var result: Boolean = false
    if (Objects.isNull(arguments) || arguments.size != RequiredArguments) {
      result = false
    } else {
      try {

        val inputFileName: String = arguments(0)
        val outputFileName: String = arguments(1)
        val tableMap: TableMap = new CalendarParser(new FileReader(
          inputFileName)).parse()
        val output: BufferedWriter = new BufferedWriter(new FileWriter(
          outputFileName))
        val renderer: SimpleFormatRenderer = new SimpleFormatRenderer(output)
        renderer.render(tableMap)
        result = true

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

object CalendarParserExtension {

  def apply(): CalendarParserExtension = new CalendarParserExtension

}
