package de.tudresden.inf.lat.tabulas.ext.renderer

import java.io.{BufferedWriter, FileReader, FileWriter, IOException}
import java.util.Objects

import de.tudresden.inf.lat.tabulas.extension.Extension
import de.tudresden.inf.lat.tabulas.parser.SimpleFormatParser
import de.tudresden.inf.lat.tabulas.table.TableMap

import scala.collection.mutable

/** This models an extension that writes the output in comma-separated values.
  *
  */
class CsvExtension extends Extension {

  val Name: String = "csv"
  val Help: String = "(input) (output) : create output as a comma-separated values (CSV) file"
  val RequiredArguments: Int = 2

  override def process(arguments: mutable.Buffer[String]): Boolean = {
    var result: Boolean = false
    if (Objects.isNull(arguments) || arguments.size != RequiredArguments) {
      result = false
    } else {
      try {

        val inputFileName: String = arguments(0)
        val outputFileName: String = arguments(1)
        val tableMap: TableMap = new SimpleFormatParser(new FileReader(
          inputFileName)).parse()
        val output: BufferedWriter = new BufferedWriter(new FileWriter(
          outputFileName))
        val renderer: CsvRenderer = new CsvRenderer(output)
        renderer.render(tableMap)
        result = true

      } catch {
        case e: IOException => throw new RuntimeException(e)
      }
    }
    result
  }

  override def getExtensionName: String = { Name }

  override def getHelp: String = { Help }

  override def getRequiredArguments: Int = { RequiredArguments }

}
