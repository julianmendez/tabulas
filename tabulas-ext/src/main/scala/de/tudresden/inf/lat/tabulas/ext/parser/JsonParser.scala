
package de.tudresden.inf.lat.tabulas.ext.parser

import java.io.{BufferedReader, ByteArrayInputStream, IOException, InputStreamReader, Reader}
import java.util.Objects

import com.eclipsesource.json.{Json, JsonObject, JsonValue}
import de.tudresden.inf.lat.tabulas.parser.{Parser, ParserConstant, SimpleFormatParser}
import de.tudresden.inf.lat.tabulas.renderer.SimpleFormatRenderer
import de.tudresden.inf.lat.tabulas.table.TableMap

import scala.collection.JavaConverters._

/** Parser for JSON format.
  */
class JsonParser(input: Reader) extends Parser {

  def renderEntry(key: String, value: JsonValue): String = {
    val prefix = key + ParserConstant.Space + ParserConstant.EqualsSign
    val result = if (value.isArray) {
      val array = value.asArray()
      val arrayStr = (0 until array.size)
        .map(index => {
          val entry = array.get(index)
          ParserConstant.Space + ParserConstant.LineContinuationSymbol + ParserConstant.NewLine +
            ParserConstant.Space + entry.asString
        })
        .mkString("")
      prefix + arrayStr + ParserConstant.NewLine

    } else {
      prefix + ParserConstant.Space + value.asString + ParserConstant.NewLine

    }
    result
  }

  def isMetadata(elements: JsonObject): Boolean = {
    Objects.nonNull(elements.get(ParserConstant.TypeDefinitionToken))
  }

  def transformDocument(reader: Reader): String = {
    val value = Json.parse(reader)
    val mainArray = value.asArray()
    val result = SimpleFormatRenderer.Prefix + ParserConstant.NewLine +
      (0 until mainArray.size)
        .map(index => {
          val record = mainArray.get(index)
          val elements = record.asObject()
          val recordStr = elements.names().asScala
            .map(key => renderEntry(key, elements.get(key)))
            .mkString("")
          val newRecord = if (isMetadata(elements)) {
            ""
          } else {
            ParserConstant.NewLine + ParserConstant.NewLine + ParserConstant.NewRecordToken +
              ParserConstant.Space + ParserConstant.EqualsSign + ParserConstant.Space
          }
          newRecord + ParserConstant.NewLine + recordStr
        }).mkString("") + ParserConstant.NewLine + ParserConstant.NewLine
    result
  }

  override def parse(): TableMap = {
    val result: TableMap = try {
      val buffer = transformDocument(this.input)
      val parser = new SimpleFormatParser(new BufferedReader(
        new InputStreamReader(new ByteArrayInputStream(buffer.getBytes()))))
      parser.parse()

    } catch {
      case e: IOException => throw new RuntimeException(e)
    }
    result
  }

}

object JsonParser {

  def apply(): JsonParser = new JsonParser(new InputStreamReader(System.in))

  def apply(input: Reader): JsonParser = new JsonParser(input)

}
