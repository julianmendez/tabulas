
package de.tudresden.inf.lat.tabulas.ext.renderer

import java.io._
import java.util.Objects

import de.tudresden.inf.lat.tabulas.extension.Extension
import de.tudresden.inf.lat.tabulas.parser.ParserConstant

import scala.util.Try

/** This models an extension that outputs the README.md file.
  *
  */
case class ReadmeExtension() extends Extension {

  final val Name: String = "readme"
  final val Help: String = " : output README.md"
  final val RequiredArguments: Int = 0
  final val ReadmeFileName = "/resources/README.md"
  final val ErrorMessageReadmeNotAvailable = "README.md not available."

  override def process(arguments: Seq[String]): Try[Boolean] = Try {
    val result = if (Objects.isNull(arguments) || arguments.size != RequiredArguments) {
      false
    } else {
      val content = readFileOrReturnDefault(ReadmeFileName)
      println(content)
      true
    }
    result
  }

  def readFileOrReturnDefault(inputFileName: String): String = {
    val inputStream = getClass.getResourceAsStream(inputFileName)
    val result = if (Option(inputStream).isEmpty) {
      ErrorMessageReadmeNotAvailable
    } else {
      val reader = new BufferedReader(new InputStreamReader(inputStream))
      val content = reader.lines.toArray.mkString(ParserConstant.NewLine)
      content
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
