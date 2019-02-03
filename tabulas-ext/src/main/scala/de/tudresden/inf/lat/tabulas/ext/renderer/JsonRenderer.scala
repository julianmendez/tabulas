
package de.tudresden.inf.lat.tabulas.ext.renderer

import java.io.{OutputStreamWriter, Writer}
import java.util.Objects

import de.tudresden.inf.lat.tabulas.datatype._
import de.tudresden.inf.lat.tabulas.parser.ParserConstant
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

  def writeAsIntegerIfNotEmpty(output: UncheckedWriter, prefix: String, value: PrimitiveTypeValue): Boolean = {
    val result = if (Objects.nonNull(value) && !value.toString.trim().isEmpty) {
      output.write(prefix)
      output.write(escapeString(value.toString))
      true
    } else {
      false
    }
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
          writeLinkIfNotEmpty(output, SpaceChar + SpaceChar, link)
        } else if (value.getType.equals(IntegerType())) {
          val intVal: IntegerValue = IntegerType().castInstance(value)
          writeAsIntegerIfNotEmpty(output, SpaceChar + SpaceChar, intVal)
        } else {
          val strVal: StringValue = StringType().castInstance(value)
          writeAsStringIfNotEmpty(output, SpaceChar + SpaceChar, strVal)
        }
        val maybeComma = if (index < newList.length - 1) CommaChar else ""
        output.write(maybeComma + NewLine)
      })
      output.write(SpaceChar + CloseSquareBracket)
      true
    } else {
      false
    }
    result
  }

  def writeLinkIfNotEmpty(output: UncheckedWriter, prefix: String, link: URIValue): Boolean = {
    val result = if (Objects.nonNull(link) && !link.isEmpty) {
      val fragment = if (link.getLabel.isEmpty) "" else HashChar + link.getLabel
      output.write(prefix)
      output.write(addQuotes(link.getUriNoLabel.toASCIIString + fragment))
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
      val prefix = SpaceChar + addQuotes(field) + SpaceChar + ColonChar + SpaceChar
      value match {
        case list: ParameterizedListValue =>
          writeParameterizedListIfNotEmpty(output, prefix, list)
        case link: URIValue =>
          writeLinkIfNotEmpty(output, prefix, link)
        case number: IntegerValue =>
          writeAsIntegerIfNotEmpty(output, prefix, number)
        case _ =>
          writeAsStringIfNotEmpty(output, prefix, value)
      }
      val maybeComma = if (index < newList.length - 1) CommaChar else ""
      output.write(maybeComma + NewLine)
    })
  }

  def renderMetadata(output: UncheckedWriter, typeName: String, table: Table): Unit = {
    output.write(NewLine + OpenBrace + NewLine)
    output.write(addQuotes(ParserConstant.TypeSelectionToken))
    output.write(SpaceChar + ColonChar)
    writeAsStringIfNotEmpty(output, ParserConstant.TypeSelectionToken, StringValue())
    val record = MetadataHelper().getMetadataAsRecord(typeName, table)
    output.write(NewLine + OpenBrace + NewLine)
    render(output, record, JsonRenderer.MetadataTokens)
    output.write(CloseBrace + NewLine)
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

  final val MetadataTokens = Seq(
    ParserConstant.TypeNameToken,
    ParserConstant.TypeDefinitionToken,
    ParserConstant.PrefixMapToken,
    ParserConstant.SortingOrderDeclarationToken
  )

  def apply(): JsonRenderer = new JsonRenderer(new OutputStreamWriter(System.out))

  def apply(output: Writer): JsonRenderer = new JsonRenderer(output)

}
