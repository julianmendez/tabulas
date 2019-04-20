
package de.tudresden.inf.lat.tabulas.ext.normalizer

import java.io.{Reader, Writer}

import de.tudresden.inf.lat.tabulas.ext.parser.{JsonParser, YamlParser}
import de.tudresden.inf.lat.tabulas.ext.renderer.{JsonRenderer, YamlRenderer}
import de.tudresden.inf.lat.tabulas.parser.{Parser, SimpleFormatParser}
import de.tudresden.inf.lat.tabulas.renderer.{Renderer, SimpleFormatRenderer}
import de.tudresden.inf.lat.tabulas.table.TableMap

import scala.util.Try

trait Normalizer extends Parser with Renderer

case class YamlNormalizer() extends Normalizer {

  override def parse(input: Reader): Try[TableMap] = YamlParser().parse(input)

  override def render(output: Writer, tableMap: TableMap): Unit = YamlRenderer().render(output, tableMap)

}

case class JsonNormalizer() extends Normalizer {

  override def parse(input: Reader): Try[TableMap] = JsonParser().parse(input)

  override def render(output: Writer, tableMap: TableMap): Unit = JsonRenderer().render(output, tableMap)

}

case class PropertiesNormalizer() extends Normalizer {

  override def parse(input: Reader): Try[TableMap] = SimpleFormatParser().parse(input)

  override def render(output: Writer, tableMap: TableMap): Unit = SimpleFormatRenderer().render(output, tableMap)

}
