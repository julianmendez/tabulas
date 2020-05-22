
package de.tudresden.inf.lat.tabulas.ext.renderer

import java.io.Writer

import de.tudresden.inf.lat.tabulas.datatype._
import de.tudresden.inf.lat.tabulas.parser.ParserConstant
import de.tudresden.inf.lat.tabulas.renderer.{MetadataHelper, Renderer}
import de.tudresden.inf.lat.tabulas.table.{Table, TableMap}

/** Renderer that creates an Rx YAML schema file.
  */
case class RxYamlRenderer() extends Renderer {

  final val ColonChar = ":"
  final val SpaceChar = " "
  final val HyphenChar = "-"
  final val TwoSpaces = SpaceChar + SpaceChar
  final val BeginningOfDocument = HyphenChar + HyphenChar + HyphenChar

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

  final val RxType = "type"
  final val RxOptional = "optional"
  final val RxNil = "//nil"
  final val RxNum = "//num"
  final val RxInt = "//int"
  final val RxStr = "//str"
  final val RxArr = "//arr"
  final val RxRec = "//rec"
  final val RxAny = "//any"

  final val Translation: Map[String, String] = Seq(
    (EmptyType().getTypeName, RxNil),
    (StringType().getTypeName, RxStr),
    (ParameterizedListType(StringType()).getTypeName, RxArr),
    (URIType().getTypeName, RxStr),
    (ParameterizedListType(URIType()).getTypeName, RxArr),
    (IntegerType().getTypeName, RxInt),
    (ParameterizedListType(IntegerType()).getTypeName, RxArr),
    (DecimalType().getTypeName, RxNum),
    (ParameterizedListType(DecimalType()).getTypeName, RxArr)
  ).toMap

  override def render(output: Writer, tableMap: TableMap): Unit = {
    tableMap.getTableIds.foreach(tableId => {
      val table: Table = tableMap.getTable(tableId).get
      output.write(BeginningOfDocument)
      output.write(NewLine)
      renderMetadata(output, tableId, table)
    })
    output.write(NewLine + NewLine)
    output.flush()
  }

  def renderMetadata(output: Writer, typeName: String, table: Table): Unit = {
    val record = MetadataHelper().getMetadataAsRecord(typeName, table)
    output.write(RxType + ColonChar + SpaceChar + addQuotes(RxRec) + NewLine)
    output.write(RxOptional + ColonChar + NewLine)

    val list = record.get(ParserConstant.TypeDefinitionToken).get.renderAsList()
    list.foreach(pair => {
      val parts = pair.split(ParserConstant.ColonFieldSign)
      val field = parts(0)
      val value = parts(1)
      val translation = Translation.getOrElse(value, RxAny)
      output.write(TwoSpaces + escapeString(field) + ColonChar + SpaceChar + addQuotes(translation) + NewLine)
    })

    output.write(NewLine)
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

  def addQuotes(text: String): String = {
    QuotationMark + escapeString(text) + QuotationMark
  }

}

object RxYamlRenderer {}
