
package de.tudresden.inf.lat.tabulas.ext.parser

import java.io.{BufferedReader, ByteArrayInputStream, IOException, InputStreamReader, Reader}

import com.eclipsesource.json.{Json, JsonValue}
import de.tudresden.inf.lat.tabulas.parser.{Parser, ParserConstant, SimpleFormatParser}
import de.tudresden.inf.lat.tabulas.renderer.SimpleFormatRenderer
import de.tudresden.inf.lat.tabulas.table.TableMap

import scala.collection.JavaConverters._

/** Parser for JSON format.
  */
class JsonParser(input: Reader) extends Parser {

  def asString(value: JsonValue): String = {
    val result = if (value.isString) {
      value.asString
    } else {
      value.toString
    }
    result
  }

  def renderEntry(key: String, value: JsonValue): String = {
    val prefix = key + ParserConstant.Space + ParserConstant.EqualsFieldSign
    val middle = if (value.isNull) {
      ""

    } else if (value.isArray) {
      val array = value.asArray()
      val arrayStr = (0 until array.size)
        .map(index => {
          val entry = array.get(index)
          ParserConstant.Space + ParserConstant.LineContinuationSymbol + ParserConstant.NewLine +
            ParserConstant.Space + asString(entry)
        })
        .mkString("")
      arrayStr

    } else {
      ParserConstant.Space + asString(value)

    }
    val result = prefix + middle + ParserConstant.NewLine
    result
  }

  def transformDocument(reader: Reader): String = {
    val value = Json.parse(reader)
    val mainArray = value.asArray()
    val result = SimpleFormatRenderer.Header + ParserConstant.NewLine +
      (0 until mainArray.size)
        .map(index => {
          val record = mainArray.get(index).asObject()
          val maybeMetadata = Option(record.get(ParserConstant.TypeSelectionToken))
          val elements = if (maybeMetadata.isDefined) maybeMetadata.get.asObject() else record
          val typeName = if (maybeMetadata.isDefined) {
            elements.get(ParserConstant.TypeNameToken).asString()
          } else {
            ""
          }
          val recordStr = elements.names().asScala
            .map(key => renderEntry(key, elements.get(key)))
            .mkString("")
          val newRecord = if (maybeMetadata.isDefined) {
            ParserConstant.TypeSelectionToken + ParserConstant.Space + ParserConstant.EqualsFieldSign + ParserConstant.Space + typeName
          } else {
            ParserConstant.NewRecordToken + ParserConstant.Space + ParserConstant.EqualsFieldSign + ParserConstant.Space
          }
          ParserConstant.NewLine + ParserConstant.NewLine + newRecord + ParserConstant.NewLine + recordStr
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
