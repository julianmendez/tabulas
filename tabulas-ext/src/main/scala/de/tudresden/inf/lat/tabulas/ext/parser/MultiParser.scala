
package de.tudresden.inf.lat.tabulas.ext.parser

import java.io._

import de.tudresden.inf.lat.tabulas.parser.{Parser, ParserConstant, SimpleFormatParser}
import de.tudresden.inf.lat.tabulas.table.TableMap

import scala.util.Try

/** Parser for JSON, YAML, and Properties format.
  */
case class MultiParser(parsers: Seq[Parser]) extends Parser {

  override def parse(input: Reader): Try[TableMap] = Try {
    val content = readContent(input)
    val res = parsers.par.map(parser => parser.parse(new StringReader(content))).seq
    val result = if (res.exists(content => content.isSuccess)) {
      res.find(content => content.isSuccess).get.get
    } else {
      res.head.get
    }
    result
  }

  @throws[IOException]
  def readContent(input: Reader): String = {
    val reader = new BufferedReader(input)
    val result = reader.lines.toArray.mkString(ParserConstant.NewLine)
    result
  }
}

object MultiParser {

  def apply(): MultiParser = MultiParser(Seq(YamlParser(), JsonParser(), SimpleFormatParser()))

}

