
package de.tudresden.inf.lat.tabulas.ext.renderer

import java.io.Writer
import java.util.Objects

import de.tudresden.inf.lat.tabulas.datatype._
import de.tudresden.inf.lat.tabulas.parser.ParserConstant
import de.tudresden.inf.lat.tabulas.renderer.{MetadataHelper, Renderer}
import de.tudresden.inf.lat.tabulas.table.{Table, TableMap}

/** Renderer that creates a JSON file.
 */
case class JsonRenderer(withMetadata: Boolean) extends Renderer {

  final val OpenBrace = "{"
  final val CloseBrace = "}"
  final val OpenSquareBracket = "["
  final val CloseSquareBracket = "]"
  final val ColonChar = ":"
  final val SpaceChar = " "
  final val CommaChar = ","
  final val HashChar = "#"

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

  def indent(output: Writer, indentation: Int): Unit = {
    (0 until indentation).foreach(_ => output.write(SpaceChar))
  }

  def writeAsIntegerIfNotEmpty(output: Writer, indentation: Int, prefix: String, value: PrimitiveTypeValue): Boolean = {
    val result = if (Objects.nonNull(value) && !value.toString.trim().isEmpty) {
      indent(output, indentation)
      output.write(prefix)
      output.write(escapeString(value.toString))
      true
    } else {
      false
    }
    result
  }

  def writeAsStringIfNotEmpty(output: Writer, indentation: Int, prefix: String, value: PrimitiveTypeValue): Boolean = {
    val result = if (Objects.nonNull(value) && !value.toString.trim().isEmpty) {
      indent(output, indentation)
      output.write(prefix)
      output.write(addQuotes(value.toString))
      true
    } else {
      false
    }
    result
  }

  def writeParameterizedListIfNotEmpty(output: Writer, indentation: Int, prefix: String, list: ParameterizedListValue): Boolean = {
    val result = if (Objects.nonNull(list)) {
      indent(output, indentation)
      output.write(prefix)
      output.write(OpenSquareBracket)
      val newList = list.getList
      if (newList.nonEmpty) {
        output.write(NewLine)
        newList.indices.foreach(index => {
          val value = newList(index)
          if (value.getType.equals(URIType())) {
            val link: URIValue = URIType().castInstance(value)
            writeLinkIfNotEmpty(output, indentation, SpaceChar, link)

          } else if (value.getType.equals(IntegerType())) {
            val intVal: IntegerValue = IntegerType().castInstance(value)
            writeAsIntegerIfNotEmpty(output, indentation, SpaceChar, intVal)

          } else {
            val strVal: StringValue = StringType().castInstance(value)
            writeAsStringIfNotEmpty(output, indentation, SpaceChar, strVal)

          }
          val maybeComma = if (index < newList.length - 1) CommaChar else ""
          output.write(maybeComma + NewLine)
        })

        indent(output, indentation)
      }
      output.write(CloseSquareBracket)
      true
    } else {
      false
    }
    result
  }

  def writeLinkIfNotEmpty(output: Writer, indentation: Int, prefix: String, link: URIValue): Boolean = {
    val result = if (Objects.nonNull(link) && !link.isEmpty) {
      val fragment = if (link.getLabel.isEmpty) "" else HashChar + link.getLabel
      indent(output, indentation)
      output.write(prefix)
      output.write(addQuotes(link.getUriNoLabel.toString + fragment))
      true
    } else {
      false
    }
    result
  }

  def render(output: Writer, indentation: Int, record: Record, fields: Seq[String]): Unit = {
    val newList = fields.filter(field => record.get(field).isDefined)
    newList.indices.foreach(index => {
      val field = newList(index)
      val optValue: Option[PrimitiveTypeValue] = record.get(field)
      val value: PrimitiveTypeValue = optValue.get

      val prefix = addQuotes(field) + ColonChar + SpaceChar

      value match {
        case list: ParameterizedListValue =>
          writeParameterizedListIfNotEmpty(output, indentation + 1, prefix, list)

        case link: URIValue =>
          writeLinkIfNotEmpty(output, indentation + 1, prefix, link)

        case number: IntegerValue =>
          writeAsIntegerIfNotEmpty(output, indentation + 1, prefix, number)

        case _ =>
          writeAsStringIfNotEmpty(output, indentation + 1, prefix, value)
      }
      val maybeComma = if (index < newList.length - 1) CommaChar else ""
      output.write(maybeComma + NewLine)
    })
  }

  def renderMetadataIfNecessary(output: Writer, indentation: Int, typeName: String, table: Table): Unit = {
    if (withMetadata) {
      indent(output, indentation)
      output.write(OpenBrace + NewLine)

      indent(output, indentation + 1)
      output.write(addQuotes(ParserConstant.TypeSelectionToken))
      output.write(ColonChar + SpaceChar + OpenBrace + NewLine)

      val record = MetadataHelper().getMetadataAsRecord(typeName, table)
      render(output, indentation + 1, record, JsonRenderer.MetadataTokens)

      indent(output, indentation + 1)
      output.write(CloseBrace + NewLine)

      val maybeComma = if (table.getRecords.nonEmpty) CommaChar else ""
      indent(output, indentation)
      output.write(CloseBrace + maybeComma + NewLine)
    }
  }

  def renderAllRecords(output: Writer, indentation: Int, table: CompositeTypeValue): Unit = {
    val list: Seq[Record] = table.getRecords
    list.indices.foreach(index => {

      indent(output, indentation)
      output.write(OpenBrace + NewLine)

      val record = list(index)
      render(output, indentation, record, table.getType.getFields)
      val maybeComma = if (index < list.length - 1) CommaChar + NewLine else ""

      indent(output, indentation)
      output.write(CloseBrace + maybeComma)
    })
  }

  override def render(output: Writer, tableMap: TableMap): Unit = {
    output.write(OpenSquareBracket + NewLine)
    val list = tableMap.getTableIds
    list.indices.foreach(index => {
      val tableId = list(index)

      renderTable(output, tableId, tableMap.getTable(tableId).get)

      val maybeComma = if (index < list.length - 1) CommaChar else ""
      output.write(maybeComma + NewLine)
    })
    output.write(CloseSquareBracket + NewLine)
    output.flush()
  }

  def renderTable(output: Writer, tableId: String, table: Table): Unit = {
    val indentation = 0
    renderMetadataIfNecessary(output, indentation + 1, tableId, table)
    renderAllRecords(output, indentation + 1, table)
  }
}

object JsonRenderer {

  final val MetadataTokens = Seq(
    ParserConstant.TypeNameToken,
    ParserConstant.TypeDefinitionToken,
    ParserConstant.PrefixMapToken,
    ParserConstant.SortingOrderDeclarationToken
  )

  def apply(): JsonRenderer = JsonRenderer(true)

}
