package de.tudresden.inf.lat.tabulas.extension

import java.io.{BufferedWriter, FileReader, FileWriter, IOException}
import java.util.Objects

import de.tudresden.inf.lat.tabulas.parser.{ParserConstant, SimpleFormatParser}
import de.tudresden.inf.lat.tabulas.renderer.SimpleFormatRenderer
import de.tudresden.inf.lat.tabulas.table.TableMap

/** Default extension. It reads and writes using the default format.
  *
  */
case class OldFormatExtension() extends Extension {

  final val Name: String = "oldformat"
  final val Help: String = "(input) (output) : create an old Tabula/Properties file, i.e. using the equals sign instead of colon"
  final val RequiredArguments: Int = 2

  override def process(arguments: Seq[String]): Boolean = {
    val result: Boolean = if (Objects.isNull(arguments) || arguments.size != RequiredArguments) {
      false
    } else {
      try {

        val inputFileName = arguments(0)
        val outputFileName = arguments(1)
        val tableMap: TableMap = SimpleFormatParser(new FileReader(
          inputFileName)).parse()
        val output: BufferedWriter = new BufferedWriter(new FileWriter(
          outputFileName))
        val renderer: SimpleFormatRenderer = SimpleFormatRenderer(output, ParserConstant.EqualsFieldSign)
        renderer.render(tableMap)
        true

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
