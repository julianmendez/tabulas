
package de.tudresden.inf.lat.tabulas.ext.renderer

import java.io.{OutputStreamWriter, Writer}
import java.util.Objects

import de.tudresden.inf.lat.tabulas.datatype.{CompositeTypeValue, ParameterizedListValue, PrimitiveTypeValue, Record, StringType, StringValue, URIType, URIValue}
import de.tudresden.inf.lat.tabulas.renderer.{MetadataHelper, Renderer, UncheckedWriter, UncheckedWriterImpl}
import de.tudresden.inf.lat.tabulas.table.{Table, TableMap}

/** Renderer that creates a JSON file.
  */
class JsonRenderer(output: Writer) extends Renderer {

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

  def writeAsStringIfNotEmpty(output: UncheckedWriter, prefix: String, value: PrimitiveTypeValue): Boolean = {
    val result = if (Objects.nonNull(value) && !value.toString.trim().isEmpty) {
      output.write(prefix)
      output.write(addQuotes(value.toString))
      true
    } else {
      false
    }
    result
  }

  def writeParameterizedListIfNotEmpty(output: UncheckedWriter, prefix: String, list: ParameterizedListValue): Boolean = {
    val result = if (Objects.nonNull(list)) {
      output.write(prefix)
      output.write(OpenSquareBracket + NewLine)
      val newList = list.getList
      newList.indices.foreach(index => {
        val value = newList(index)
        if (value.getType.equals(URIType())) {
          val link: URIValue = URIType().castInstance(value)
          writeLinkIfNotEmpty(output, " ", link)
        } else {
          val strVal: StringValue = StringType().castInstance(value)
          writeAsStringIfNotEmpty(output, " ", strVal)
        }
        val maybeComma = if (index < newList.length - 1) CommaChar else ""
        output.write(maybeComma + NewLine)
      })
      output.write(CloseSquareBracket)
      true
    } else {
      false
    }
    result
  }

  def writeLinkIfNotEmpty(output: UncheckedWriter, prefix: String, link: URIValue): Boolean = {
    val result = if (Objects.nonNull(link) && !link.isEmpty) {
      output.write(prefix)
      output.write(addQuotes(link.getUriNoLabel.toASCIIString + HashChar + link.getLabel))
      true
    } else {
      false
    }
    result
  }

  def render(output: UncheckedWriter, record: Record, fields: Seq[String]): Unit = {
    val newList = fields.filter(field => record.get(field).isDefined)
    newList.indices.foreach(index => {
      val field = newList(index)
      val optValue: Option[PrimitiveTypeValue] = record.get(field)
      val value: PrimitiveTypeValue = optValue.get
      val prefix = addQuotes(field) + SpaceChar + ColonChar + SpaceChar
      value match {
        case list: ParameterizedListValue =>
          writeParameterizedListIfNotEmpty(output, prefix, list)
        case link: URIValue =>
          writeLinkIfNotEmpty(output, prefix, link)
        case _ =>
          writeAsStringIfNotEmpty(output, prefix, value)
      }
      val maybeComma = if (index < newList.length - 1) CommaChar else ""
      output.write(maybeComma + NewLine)
    })
  }

  def renderMetadata(output: UncheckedWriter, typeName: String, table: Table): Unit = {
    output.write(NewLine + OpenBrace + NewLine)
    val record = MetadataHelper().getMetadataAsRecord(typeName, table)
    render(output, record, MetadataHelper.MetadataTokens)
    val maybeComma = if (table.getRecords.nonEmpty) CommaChar else ""
    output.write(CloseBrace + maybeComma + NewLine + NewLine)
  }

  def renderAllRecords(output: UncheckedWriter, table: CompositeTypeValue, hasMoreTables: Boolean): Unit = {
    val list: Seq[Record] = table.getRecords
    list.indices.foreach(index => {
      output.write(NewLine + OpenBrace + NewLine)
      val record = list(index)
      render(output, record, table.getType.getFields)
      val maybeComma = if (index < list.length - 1 || hasMoreTables) CommaChar else ""
      output.write(CloseBrace + maybeComma + NewLine + NewLine)
    })
  }


  def render(output: UncheckedWriter, tableMap: TableMap): Unit = {
    output.write(OpenSquareBracket + NewLine + NewLine)
    val list = tableMap.getTableIds
    list.indices.foreach(index => {
      val tableId = list(index)
      val table: Table = tableMap.getTable(tableId).get
      renderMetadata(output, tableId, table)
      val hasMoreTables = index < list.length - 1
      renderAllRecords(output, table, hasMoreTables)
    })
    output.write(NewLine + CloseSquareBracket + NewLine + NewLine + NewLine)
    output.flush()
  }

  override def render(tableMap: TableMap): Unit = {
    render(new UncheckedWriterImpl(this.output), tableMap)
  }

}

object JsonRenderer {

  def apply(): JsonRenderer = new JsonRenderer(new OutputStreamWriter(System.out))

  def apply(output: Writer): JsonRenderer = new JsonRenderer(output)

}
