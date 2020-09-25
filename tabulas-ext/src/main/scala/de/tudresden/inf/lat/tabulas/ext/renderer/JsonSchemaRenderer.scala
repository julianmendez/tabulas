
package de.tudresden.inf.lat.tabulas.ext.renderer

import java.io.Writer

import de.tudresden.inf.lat.tabulas.datatype._
import de.tudresden.inf.lat.tabulas.parser.ParserConstant
import de.tudresden.inf.lat.tabulas.renderer.{MetadataHelper, Renderer}
import de.tudresden.inf.lat.tabulas.table.{Table, TableMap}

/** Renderer that creates a JSON Schema file.
 */
case class JsonSchemaRenderer() extends Renderer {

  final val OpenBrace = "{"
  final val CloseBrace = "}"
  final val OpenSquareBracket = "["
  final val CloseSquareBracket = "]"
  final val ColonChar = ":"
  final val SpaceChar = " "
  final val CommaChar = ","
  final val HashChar = "#"

  final val HyphenChar = "-"
  final val TwoSpaces = SpaceChar + SpaceChar

  final val QuotationMark = "\""
  final val EscapedQuotationMark = "\\\""
  final val Backslash = "\\"
  final val EscapedBackslash = "\\"
  final val Backspace = "\b"
  final val EscapedBackspace = "\\b"
  final val FormFeed = "\f"
  final val EscapedFormFeed = "\\f"
  final val NewLine = "\n"
  final val EscapedNewLine = "\\n"
  final val CarriageReturn = "\r"
  final val EscapedCarriageReturn = "\\r"
  final val Tab = "\t"
  final val EscapedTab = "\\t"
  final val Slash = "/"

  final val JscSchemaKey = "$schema"
  final val JscSchemaValue = "http://json-schema.org/draft-06/schema#"
  final val JscSchemaNextValue = "http://json-schema.org/draft/2019-09/schema#"
  final val JscTitle = "title"
  final val JscType = "type"
  final val JscProperties = "properties"
  final val JscAdditionalProperties = "additionalProperties"
  final val JscFalse = "false"
  final val JscItems = "items"
  final val JscNull = "null"
  final val JscNumber = "number"
  final val JscInteger = "integer"
  final val JscString = "string"
  final val JscArray = "array"
  final val JscObject = "object"

  final val Translation: Map[String, String] = Seq(
    (EmptyType().getTypeName, JscNull),
    (StringType().getTypeName, JscString),
    (ParameterizedListType(StringType()).getTypeName, JscArray),
    (URIType().getTypeName, JscString),
    (ParameterizedListType(URIType()).getTypeName, JscArray),
    (IntegerType().getTypeName, JscInteger),
    (ParameterizedListType(IntegerType()).getTypeName, JscArray),
    (DecimalType().getTypeName, JscNumber),
    (ParameterizedListType(DecimalType()).getTypeName, JscArray)
  ).toMap

  final val ArrayItemTranslation: Map[String, String] = Seq(
    (ParameterizedListType(StringType()).getTypeName, JscString),
    (ParameterizedListType(URIType()).getTypeName, JscString),
    (ParameterizedListType(IntegerType()).getTypeName, JscInteger),
    (ParameterizedListType(DecimalType()).getTypeName, JscNumber)
  ).toMap

  override def render(output: Writer, tableMap: TableMap): Unit = {
    tableMap.getTableIds.foreach(tableId => {
      renderTable(output, tableId, tableMap.getTable(tableId).get)
    })
  }

  def renderTable(output: Writer, tableId: String, table: Table): Unit = {
    renderMetadata(output, tableId, table)
    output.flush()
  }

  def indent(output: Writer, indentation: Int): Unit = {
    (0 until indentation).foreach(_ => output.write(TwoSpaces))
  }

  def renderKey(output: Writer, indentation: Int, key: String): Unit = {
    indent(output, indentation)
    if (key.nonEmpty) {
      output.write(addQuotes(key))
      output.write(ColonChar + SpaceChar)
    }
  }

  def openBrace(output: Writer, indentation: Int, key: String): Unit = {
    renderKey(output, indentation, key)
    output.write(OpenBrace + NewLine)
  }

  def closeBrace(output: Writer, indentation: Int, withComma: Boolean): Unit = {
    indent(output, indentation)
    output.write(CloseBrace)
    if (withComma) {
      output.write(CommaChar)
    }
    output.write(NewLine)
  }

  def renderKeyValue(output: Writer, indentation: Int, key: String, value: String, withComma: Boolean): Unit = {
    renderKey(output, indentation, key)
    output.write(addQuotes(value))
    if (withComma) {
      output.write(CommaChar)
    }
    output.write(NewLine)
  }

  def renderMetadata(output: Writer, typeName: String, table: Table): Unit = {
    val record = MetadataHelper().getMetadataAsRecord(typeName, table)

    val indentation = 0
    openBrace(output, indentation, key = "")
    renderKeyValue(output, indentation + 1, JscSchemaKey, JscSchemaValue, withComma = true)
    renderKeyValue(output, indentation + 1, JscTitle, typeName, withComma = true)
    renderKeyValue(output, indentation + 1, JscType, JscArray, withComma = true)
    openBrace(output, indentation + 1, JscItems)
    renderKeyValue(output, indentation + 2, JscType, JscObject, withComma = true)
    renderKey(output, indentation + 2, JscAdditionalProperties)
    output.write(JscFalse + CommaChar + NewLine)

    openBrace(output, indentation + 2, JscProperties)
    val list = record.get(ParserConstant.TypeDefinitionToken).get.renderAsList()
    list.indices.foreach(index => {
      val pair = list(index)
      val parts = pair.split(ParserConstant.ColonFieldSign)
      val field = parts(0)
      val value = parts(1)
      val translation = Translation.getOrElse(value, JscObject)
      val arrayItemTranslation = ArrayItemTranslation.get(value)

      openBrace(output, indentation + 3, escapeString(field))
      renderKeyValue(output, indentation + 4, JscType, translation, withComma = arrayItemTranslation.isDefined)

      if (arrayItemTranslation.isDefined) {
        openBrace(output, indentation + 4, JscItems)
        renderKeyValue(output, indentation + 5, JscType, arrayItemTranslation.get, withComma = false)
        closeBrace(output, indentation + 4, withComma = false)
      }

      closeBrace(output, indentation + 3, withComma = (index < list.size - 1))
    })

    closeBrace(output, indentation + 2, withComma = false)
    closeBrace(output, indentation + 1, withComma = false)
    closeBrace(output, indentation, withComma = false)
    output.write(NewLine)
  }

  def addQuotes(str: String): String = {
    QuotationMark + escapeString(str) + QuotationMark
  }

  def escapeString(str: String): String = {
    val result = str.flatMap(ch => {
      "" + ch match {
        case QuotationMark => EscapedQuotationMark
        case Backslash => EscapedBackslash
        case Backspace => EscapedBackspace
        case FormFeed => EscapedFormFeed
        case NewLine => EscapedNewLine
        case CarriageReturn => EscapedCarriageReturn
        case Tab => EscapedTab
        case Slash => Slash // slash does not need to be escaped
        case _ => "" + ch
      }
    })
    result
  }

}

object JsonSchemaRenderer {}
