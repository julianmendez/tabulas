
package de.tudresden.inf.lat.tabulas.ext.renderer

import java.io._
import java.util.Objects

import de.tudresden.inf.lat.tabulas.datatype.ParseException
import de.tudresden.inf.lat.tabulas.ext.normalizer.{JsonNormalizer, Normalizer, PropertiesNormalizer, YamlNormalizer}
import de.tudresden.inf.lat.tabulas.ext.parser.MultiParser
import de.tudresden.inf.lat.tabulas.extension.Extension
import de.tudresden.inf.lat.tabulas.parser.ParserConstant
import de.tudresden.inf.lat.tabulas.table.TableMap

import scala.util.{Failure, Success, Try}

/** Normalization extension. It reads and writes using the same file.
 *
 */
case class NormalizationExtension() extends Extension {

  final val Name: String = "normalize"
  final val Help: String = "(input) : normalize a Tabula file in its own format (e.g. Tabula.JSON -> Tabula.JSON, Tabula.YAML -> Tabula.YAML)"
  final val RequiredArguments: Int = 1

  final val HyphenChar = "-"
  final val ColonChar = ":"
  final val Space = ParserConstant.Space
  final val NewLine = ParserConstant.NewLine

  override def process(arguments: Seq[String]): Try[Boolean] = Try {
    val result = if (Objects.isNull(arguments) || arguments.size != RequiredArguments) {
      false

    } else {
      val inputFileName = arguments(0)
      val outputFileName = inputFileName
      val content = MultiParser().readContent(new FileReader(inputFileName))

      val normalizers = Seq(PropertiesNormalizer(), JsonNormalizer(), YamlNormalizer())

      val res = normalizers
        .map(normalizer => NormalizerPair(normalizer, normalizer.parse(new StringReader(content))))

      val maybeBestNormalizerPair = res
        .find(pair => pair.content.isSuccess)

      val acceptedNormalizerPair = maybeBestNormalizerPair match {
        case Some(theFirstNormalizerPair) => theFirstNormalizerPair
        case None =>
          val newMessage = res.map(pair => pair.content match {
            case Failure(exception) =>
              formatMessage(pair.normalizer, exception.getMessage)

            case Success(_) =>
              throw new IllegalStateException("Illegal state in normalization extension.")

          }).mkString(ParserConstant.NewLine)
          throw ParseException(newMessage)
      }

      val tableMap = acceptedNormalizerPair.content.get
      if (tableMap.getTableIds.length != 1) {
        println(ParserConstant.WarningDeprecationOfMultipleTables)
      }
      val normalizer = acceptedNormalizerPair.normalizer

      val output = new BufferedWriter(new FileWriter(outputFileName))
      normalizer.render(output, tableMap)
      true
    }
    result
  }

  def formatMessage(normalizer: Normalizer, message: String): String = {
    NewLine + HyphenChar + Space + normalizer.getClass.getSimpleName +
      Space + ColonChar + Space + message
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

object NormalizationExtension {}


case class NormalizerPair(normalizer: Normalizer, content: Try[TableMap])

object NormalizerPair {}
