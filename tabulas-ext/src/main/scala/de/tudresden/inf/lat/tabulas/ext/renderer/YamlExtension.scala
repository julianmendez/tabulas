package de.tudresden.inf.lat.tabulas.ext.renderer

import java.io.{BufferedWriter, FileReader, FileWriter, IOException}
import java.util.Objects

import de.tudresden.inf.lat.tabulas.extension.Extension
import de.tudresden.inf.lat.tabulas.parser.SimpleFormatParser
import de.tudresden.inf.lat.tabulas.table.TableMap

/** This models an extension that writes the output in Wikitext.
  *
  */
class YamlExtension extends Extension {

  val Name: String = "yaml"
  val Help: String = "(input) (output) : create output as YAML file"
  val RequiredArguments: Int = 2

  override def process(arguments: Seq[String]): Boolean = {
    val result = if (Objects.isNull(arguments) || arguments.size != RequiredArguments) {
      false
    } else {
      val res = try {

        val inputFileName = arguments(0)
        val outputFileName = arguments(1)
        val tableMap: TableMap = new SimpleFormatParser(new FileReader(
          inputFileName)).parse()
        val output: BufferedWriter = new BufferedWriter(new FileWriter(
          outputFileName))
        val renderer: YamlRenderer = new YamlRenderer(output)
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

object YamlExtension {

  def apply(): YamlExtension = new YamlExtension

}