
package de.tudresden.inf.lat.tabulas.ext.parser

import java.io.BufferedWriter
import java.io.FileReader
import java.io.FileWriter
import java.io.IOException
import java.util.List
import java.util.Objects

import de.tudresden.inf.lat.tabulas.extension.Extension
import de.tudresden.inf.lat.tabulas.renderer.SimpleFormatRenderer
import de.tudresden.inf.lat.tabulas.table.TableMap

/**
 * This models an extension that reads comma-separated values and writes them
 * with the default format.
 *
 */
class CalendarParserExtension extends Extension {

  def Name: String = "parsecalendar"
  def Help: String = "(input) (output) : create output file with a simple text format parsing a calendar file"
  def RequiredArguments: Int = 2

  override def process(arguments: List[String]): Boolean = {
    if (Objects.isNull(arguments) || arguments.size() != RequiredArguments) {
      false
    } else {
      try {

        val inputFileName: String = arguments.get(0)
        val outputFileName: String = arguments.get(1)
        val tableMap: TableMap = new CalendarParser(new FileReader(
          inputFileName)).parse()
        val output: BufferedWriter = new BufferedWriter(new FileWriter(
          outputFileName))
        val renderer: SimpleFormatRenderer = new SimpleFormatRenderer(output)
        renderer.render(tableMap)
        true

      } catch {
        case e: IOException => {
          throw new RuntimeException(e)
        }
      }
    }
  }

  override def getExtensionName() = {
    Name
  }

  override def getHelp(): String = {
    Help
  }

  override def getRequiredArguments(): Int = {
    RequiredArguments
  }

}
