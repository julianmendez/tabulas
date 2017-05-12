package de.tudresden.inf.lat.tabulas.ext.renderer

import java.io.BufferedWriter
import java.io.FileReader
import java.io.FileWriter
import java.io.IOException
import scala.collection.mutable.Buffer
import de.tudresden.inf.lat.tabulas.extension.Extension
import de.tudresden.inf.lat.tabulas.parser.SimpleFormatParser
import de.tudresden.inf.lat.tabulas.table.TableMap
import java.util.Objects

/**
 * This models an extension that writes the output in Wikitext.
 *
 */
class WikitextExtension extends Extension {

  val Name: String = "wikitext"
  val Help: String = "(input) (output) : create output as Wiki text file"
  val RequiredArguments: Int = 2

  override def process(arguments: Buffer[String]): Boolean = {
    if (Objects.isNull(arguments) || arguments.size != RequiredArguments) {
      return false
    } else {
      try {

        val inputFileName: String = arguments(0)
        val outputFileName: String = arguments(1)
        val tableMap: TableMap = new SimpleFormatParser(new FileReader(
          inputFileName)).parse()
        val output: BufferedWriter = new BufferedWriter(new FileWriter(
          outputFileName))
        val renderer: WikitextRenderer = new WikitextRenderer(output)
        renderer.render(tableMap)
        return true

      } catch {
        case e: IOException => {
          throw new RuntimeException(e)
        }
      }
    }
  }

  override def getExtensionName(): String = {
    return Name
  }

  override def getHelp(): String = {
    return Help
  }

  override def getRequiredArguments(): Int = {
    return RequiredArguments
  }

}
