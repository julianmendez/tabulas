
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
  final val JscSchemaNewValue = "http://json-schema.org/draft/2019-09/schema#"
  final val JscType = "type"
  final val JscProperties = "properties"
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
      val table: Table = tableMap.getTable(tableId).get
      renderMetadata(output, tableId, table)
    })
    output.flush()
  }

  def closeBrace(output: Writer, tab: Int, withComma: Boolean): Unit = {
    (0 until tab).foreach(_ => output.write(TwoSpaces))
    output.write(CloseBrace)
    if (withComma) {
      output.write(CommaChar)
    }
    output.write(NewLine)
  }

  def openBrace(output: Writer, tab: Int, key: String): Unit = {
    (0 until tab).foreach(_ => output.write(TwoSpaces))
    output.write(addQuotes(key))
    output.write(ColonChar + SpaceChar)
    output.write(OpenBrace)
    output.write(NewLine)
  }

  def renderKeyValue(output: Writer, tab: Int, key: String, value: String, withComma: Boolean): Unit = {
    (0 until tab).foreach(_ => output.write(TwoSpaces))
    output.write(addQuotes(key))
    output.write(ColonChar + SpaceChar)
    output.write(addQuotes(value))
    if (withComma) {
      output.write(CommaChar)
    }
    output.write(NewLine)
  }

  def renderMetadata(output: Writer, typeName: String, table: Table): Unit = {
    val record = MetadataHelper().getMetadataAsRecord(typeName, table)

    output.write(OpenBrace + NewLine)

    val tab = 1
    renderKeyValue(output, tab, JscSchemaKey, JscSchemaValue, withComma = true)
    renderKeyValue(output, tab, JscType, JscArray, withComma = true)
    openBrace(output, tab, JscItems)

    renderKeyValue(output, tab + 1, JscType, JscObject, withComma = true)
    openBrace(output, tab + 1, JscProperties)

    val list = record.get(ParserConstant.TypeDefinitionToken).get.renderAsList()
    list.indices.foreach(index => {
      val pair = list(index)
      val parts = pair.split(ParserConstant.ColonFieldSign)
      val field = parts(0)
      val value = parts(1)
      val translation = Translation.getOrElse(value, JscObject)
      val arrayItemTranslation = ArrayItemTranslation.get(value)

      openBrace(output, tab + 2, escapeString(field))
      renderKeyValue(output, tab + 3, JscType, translation, withComma = arrayItemTranslation.isDefined)

      if (arrayItemTranslation.isDefined) {
        openBrace(output, tab + 3, JscItems)
        renderKeyValue(output, tab + 4, JscType, arrayItemTranslation.get, withComma = false)
        closeBrace(output, tab + 3, withComma = false)
      }

      closeBrace(output, tab + 2, withComma = (index < list.size - 1))
    })

    closeBrace(output, tab + 1, withComma = false)

    closeBrace(output, tab, withComma = false)

    output.write(CloseBrace + NewLine + NewLine)
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
