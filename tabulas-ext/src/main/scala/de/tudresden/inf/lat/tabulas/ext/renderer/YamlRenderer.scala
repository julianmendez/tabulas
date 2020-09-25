
package de.tudresden.inf.lat.tabulas.ext.renderer

import java.io.Writer
import java.text.SimpleDateFormat
import java.util.Objects

import de.tudresden.inf.lat.tabulas.datatype._
import de.tudresden.inf.lat.tabulas.parser.ParserConstant
import de.tudresden.inf.lat.tabulas.renderer.{MetadataHelper, Renderer}
import de.tudresden.inf.lat.tabulas.table.{Table, TableMap}

import scala.util.Try

/** Renderer that creates a YAML file.
 */
case class YamlRenderer(withMetadata: Boolean) extends Renderer {

  final val ColonChar = ":"
  final val SpaceChar = " "
  final val HashChar = "#"
  final val HyphenChar = "-"
  final val DotChar = "."
  final val HyphenSpace = HyphenChar + SpaceChar
  final val TwoSpaces = SpaceChar + SpaceChar
  final val FourSpaces = TwoSpaces + TwoSpaces
  final val BeginningOfDocument = HyphenChar + HyphenChar + HyphenChar
  final val EndOfDocument = DotChar + DotChar + DotChar

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
  final val Apostrophe = "'"

  final val SpecialCharSeq = Seq(
    ":", "{", "}", "[", "]", ",", "&", "*", "#", "?", "|", "-", "<", ">", "=", "!", "%", "@", "`"
  )

  final val BeautifyingNewLine = NewLine
  // this is just to make the output more readable

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

  def writeAsIntegerIfNotEmpty(output: Writer, prefix: String, value: PrimitiveTypeValue): Boolean = {
    val result = if (Objects.nonNull(value) && !value.toString.trim().isEmpty) {
      output.write(prefix)
      output.write(escapeString(value.toString))
      true
    } else {
      false
    }
    result
  }

  def startsWithSpecialChar(text: String): Boolean = {
    SpecialCharSeq.exists(specialChar => text.trim.startsWith(specialChar))
  }

  def mayUseApostrophes(text: String): Boolean = {
    isBoolean(text) ||
      isNumber(text) ||
      isDate(text)
  }

  def isBoolean(text: String): Boolean = {
    Try {
      text.toBoolean
    }.isSuccess
  }

  def isNumber(text: String): Boolean = {
    Try {
      BigDecimal(text)
    }.isSuccess
  }

  def isDate(text: String): Boolean = {
    Try {
      new SimpleDateFormat("yyyy-MM-dd").format(text)
    }.isSuccess
  }

  def requiresQuotesOrApostrophes(text: String): Boolean = {
    isBoolean(text) ||
      isNumber(text) ||
      isDate(text) ||
      startsWithSpecialChar(text) ||
      text.contains(ColonSpace) ||
      text.contains(SpaceHash)
  }

  def addQuotesIfNeeded(text: String): String = {
    val result = if (requiresQuotesOrApostrophes(text)) {
      if (mayUseApostrophes(text)) {
        Apostrophe + text + Apostrophe
      } else {
        QuotationMark + text + QuotationMark
      }
    } else {
      text
    }
    result
  }

  def writeAsStringIfNotEmpty(output: Writer, prefix: String, value: PrimitiveTypeValue): Boolean = {
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

  def writeParameterizedListIfNotEmpty(output: Writer, prefix: String, list: ParameterizedListValue, tabulation: String): Boolean = {
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

  def writeLinkIfNotEmpty(output: Writer, prefix: String, link: URIValue): Boolean = {
    val result = if (Objects.nonNull(link) && !link.isEmpty) {
      val fragment = if (link.getLabel.isEmpty) "" else HashChar + link.getLabel
      output.write(prefix)
      output.write(escapeString(link.getUriNoLabel.toString + fragment))
      true
    } else {
      false
    }
    result
  }

  def render(output: Writer, record: Record, fields: Seq[String], tabulation: String): Unit = {
    val newList = fields.filter(field => record.get(field).isDefined)
    output.write(tabulation)
    newList.indices.foreach(index => {
      val field = newList(index)
      val optValue: Option[PrimitiveTypeValue] = record.get(field)
      val value: PrimitiveTypeValue = optValue.get
      val spaces = if (index > 0) TwoSpaces else ""
      val prefix = spaces + escapeString(field) + ColonChar
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

  def renderMetadataIfNecessary(output: Writer, typeName: String, table: Table): Unit = {
    if (withMetadata) {
      val record = MetadataHelper().getMetadataAsRecord(typeName, table)
      output.write(HyphenSpace + ParserConstant.TypeSelectionToken + ColonChar + NewLine)
      render(output, record, YamlRenderer.MetadataTokens, TwoSpaces)

      output.write(BeautifyingNewLine)
    }
  }

  def renderAllRecords(output: Writer, table: CompositeTypeValue): Unit = {
    val list: Seq[Record] = table.getRecords
    list.indices.foreach(index => {
      val record = list(index)
      output.write(HyphenChar + SpaceChar)
      render(output, record, table.getType.getFields, "")

      output.write(BeautifyingNewLine)
    })
  }

  def renderTable(output: Writer, tableId: String, table: Table): Unit = {
    output.write(BeginningOfDocument)
    output.write(NewLine)
    renderMetadataIfNecessary(output, tableId, table)
    renderAllRecords(output, table)
    output.flush()
  }

  override def render(output: Writer, tableMap: TableMap): Unit = {
    tableMap.getTableIds.foreach(tableId => {
      renderTable(output, tableId, tableMap.getTable(tableId).get)
    })
  }

}

object YamlRenderer {

  final val MetadataTokens = Seq(
    ParserConstant.TypeNameToken,
    ParserConstant.TypeDefinitionToken,
    ParserConstant.PrefixMapToken,
    ParserConstant.SortingOrderDeclarationToken
  )

  def apply(): YamlRenderer = YamlRenderer(true)

}
