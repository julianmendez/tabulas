
package de.tudresden.inf.lat.tabulas.ext.renderer

import java.io.{OutputStreamWriter, Writer}
import java.text.SimpleDateFormat
import java.util.Objects

import de.tudresden.inf.lat.tabulas.datatype._
import de.tudresden.inf.lat.tabulas.parser.ParserConstant
import de.tudresden.inf.lat.tabulas.renderer.{MetadataHelper, Renderer, UncheckedWriter, UncheckedWriterImpl}
import de.tudresden.inf.lat.tabulas.table.{Table, TableMap}

import scala.util.Try

/** Renderer that creates a YAML file.
  */
class YamlRenderer(output: Writer) extends Renderer {

  final val ColonChar = ":"
  final val SpaceChar = " "
  final val HashChar = "#"
  final val HyphenChar = "-"
  final val HyphenSpace = HyphenChar + SpaceChar
  final val TwoSpaces = SpaceChar + SpaceChar
  final val FourSpaces = TwoSpaces + TwoSpaces

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

  final val ColonSpace = ": "
  final val SpaceHash = " #"

  final val SpecialCharSeq = Seq(
    ":", "{", "}", "[", "]", ",", "&", "*", "#", "?", "|", "-", "<", ">", "=", "!", "%", "@", "`"
  )

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

  def requiresQuotes(text: String): Boolean = {
    val isABoolean = Try {
      text.toBoolean
    }.isSuccess
    val isANumber = Try {
      BigDecimal(text)
    }.isSuccess
    val isADate = Try {
      new SimpleDateFormat("yyyy-MM-dd").format(text)
    }.isSuccess
    val trimmedText = text.trim
    val startsWithSpecialChar = SpecialCharSeq.exists(specialChar => trimmedText.startsWith(specialChar))
    val result = isABoolean || isANumber || isADate ||
      startsWithSpecialChar || text.contains(ColonSpace) || text.contains(SpaceHash)
    result
  }

  def addQuotesIfNeeded(text: String): String = {
    val result = if (requiresQuotes(text)) {
      QuotationMark + text + QuotationMark
    } else {
      text
    }
    result
  }

  def writeAsStringIfNotEmpty(output: UncheckedWriter, prefix: String, value: PrimitiveTypeValue): Boolean = {
    val text = addQuotesIfNeeded(escapeString(value.toString))
    val result = if (Objects.nonNull(value) && !value.toString.trim().isEmpty) {
      output.write(prefix)
      output.write(text)
      true
    } else {
      false
    }
    result
  }

  def writeParameterizedListIfNotEmpty(output: UncheckedWriter, prefix: String, list: ParameterizedListValue, tabulation: String): Boolean = {
    val result = if (Objects.nonNull(list)) {
      output.write(tabulation)
      output.write(prefix)
      output.write(NewLine)
      val newList = list.getList
      newList.indices.foreach(index => {
        val value = newList(index)
        if (value.getType.equals(URIType())) {
          val link: URIValue = URIType().castInstance(value)
          writeLinkIfNotEmpty(output, tabulation + TwoSpaces + HyphenSpace, link)
        } else if (value.getType.equals(IntegerType())) {
          val intVal: IntegerValue = IntegerType().castInstance(value)
          writeAsIntegerIfNotEmpty(output, tabulation + TwoSpaces + HyphenSpace, intVal)
        } else {
          val strVal: StringValue = StringType().castInstance(value)
          writeAsStringIfNotEmpty(output, tabulation + TwoSpaces + HyphenSpace, strVal)
        }
        val maybeNewLine = if (index < newList.length - 1) NewLine else ""
        output.write(maybeNewLine)
      })
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
      output.write(escapeString(link.getUriNoLabel.toASCIIString + fragment))
      true
    } else {
      false
    }
    result
  }

  def render(output: UncheckedWriter, record: Record, fields: Seq[String], tabulation: String): Unit = {
    val newList = fields.filter(field => record.get(field).isDefined)
    output.write(tabulation)
    newList.indices.foreach(index => {
      val field = newList(index)
      val optValue: Option[PrimitiveTypeValue] = record.get(field)
      val value: PrimitiveTypeValue = optValue.get
      val spaces = if (index > 0) TwoSpaces else ""
      val prefix = spaces + escapeString(field) + SpaceChar + ColonChar
      val tabPrefixSp = tabulation + prefix + SpaceChar
      value match {
        case list: ParameterizedListValue =>
          writeParameterizedListIfNotEmpty(output, prefix, list, tabulation)
        case link: URIValue =>
          writeLinkIfNotEmpty(output, tabPrefixSp, link)
        case number: IntegerValue =>
          writeAsIntegerIfNotEmpty(output, tabPrefixSp, number)
        case _ =>
          writeAsStringIfNotEmpty(output, tabPrefixSp, value)
      }
      output.write(NewLine)
    })
  }

  def renderMetadata(output: UncheckedWriter, typeName: String, table: Table): Unit = {
    val record = MetadataHelper().getMetadataAsRecord(typeName, table)
    output.write(HyphenSpace + ParserConstant.TypeSelectionToken + SpaceChar + ColonChar + NewLine)
    render(output, record, YamlRenderer.MetadataTokens, TwoSpaces)
    output.write(NewLine + NewLine)
  }

  def renderAllRecords(output: UncheckedWriter, table: CompositeTypeValue): Unit = {
    val list: Seq[Record] = table.getRecords
    list.indices.foreach(index => {
      val record = list(index)
      output.write(HyphenChar + SpaceChar)
      render(output, record, table.getType.getFields, "")
      output.write(NewLine + NewLine)
    })
  }

  def render(output: UncheckedWriter, tableMap: TableMap): Unit = {
    output.write(NewLine + NewLine)
    tableMap.getTableIds.foreach(tableId => {
      val table: Table = tableMap.getTable(tableId).get
      renderMetadata(output, tableId, table)
      renderAllRecords(output, table)
    })
    output.write(NewLine + NewLine)
    output.flush()
  }

  override def render(tableMap: TableMap): Unit = {
    render(new UncheckedWriterImpl(this.output), tableMap)
  }

}

object YamlRenderer {

  final val MetadataTokens = Seq(
    ParserConstant.TypeNameToken,
    ParserConstant.TypeDefinitionToken,
    ParserConstant.PrefixMapToken,
    ParserConstant.SortingOrderDeclarationToken
  )

  def apply(): YamlRenderer = new YamlRenderer(new OutputStreamWriter(System.out))

  def apply(output: Writer): YamlRenderer = new YamlRenderer(output)

}
