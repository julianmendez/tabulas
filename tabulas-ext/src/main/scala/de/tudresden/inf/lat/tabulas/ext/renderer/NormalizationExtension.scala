
package de.tudresden.inf.lat.tabulas.ext.renderer

import java.io._
import java.util.Objects

import de.tudresden.inf.lat.tabulas.ext.normalizer.{JsonNormalizer, Normalizer, PropertiesNormalizer, YamlNormalizer}
import de.tudresden.inf.lat.tabulas.ext.parser.MultiParser
import de.tudresden.inf.lat.tabulas.extension.Extension
import de.tudresden.inf.lat.tabulas.table.TableMap

import scala.util.Try

/** Normalization extension. It reads and writes using the same file.
  *
  */
case class NormalizationExtension() extends Extension {

  final val Name: String = "normalize"
  final val Help: String = "(input) : normalize a Tabula file in its own format (e.g. Tabulas/JSON -> Tabulas/JSON, Tabula/YAML -> Tabula/YAML)"
  final val RequiredArguments: Int = 1

  override def process(arguments: Seq[String]): Try[Boolean] = Try {
    val result = if (Objects.isNull(arguments) || arguments.size != RequiredArguments) {
      false

    } else {
      val inputFileName = arguments(0)
      val outputFileName = inputFileName
      val content = MultiParser().readContent(new FileReader(inputFileName))

      val normalizers = Seq(PropertiesNormalizer(), JsonNormalizer(), YamlNormalizer())

      val res = normalizers.par
        .map(normalizer => NormalizerPair(normalizer, normalizer.parse(new StringReader(content))))
        .seq

      val bestNormalizerPair = res
        .find(pair => pair.content.isSuccess)
        .getOrElse(res.head)

      val tableMap = bestNormalizerPair.content.get
      val bestNormalizer = bestNormalizerPair.normalizer

      val output = new BufferedWriter(new FileWriter(outputFileName))
      bestNormalizer.render(output, tableMap)
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

case class NormalizerPair(normalizer: Normalizer, content: Try[TableMap])
