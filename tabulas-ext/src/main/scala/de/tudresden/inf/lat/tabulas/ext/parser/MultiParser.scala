
package de.tudresden.inf.lat.tabulas.ext.parser

import java.io._

import de.tudresden.inf.lat.tabulas.parser.{Parser, ParserConstant, SimpleFormatParser}
import de.tudresden.inf.lat.tabulas.table.TableMap

import scala.util.Try

/** Parser for JSON, YAML, and Properties format.
  */
case class MultiParser(input: Reader) extends Parser {

  override def parse(): Try[TableMap] = Try {
    val reader = new BufferedReader(input)
    val readerContent = reader.lines.toArray.mkString(ParserConstant.NewLine)
    val parsers = Seq(
      SimpleFormatParser(new StringReader(readerContent)),
      JsonParser(new StringReader(readerContent)),
      YamlParser(new StringReader(readerContent))
    )
    val res = parsers.par.map(parser => parser.parse()).seq
    val result = if (res.exists(content => content.isSuccess)) {
      res.find(content => content.isSuccess).get.get
    } else {
      res(0).get
    }
    result
  }

}

