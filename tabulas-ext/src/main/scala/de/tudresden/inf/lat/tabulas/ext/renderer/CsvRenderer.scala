
package de.tudresden.inf.lat.tabulas.ext.renderer

import java.io.{OutputStreamWriter, Writer}
import java.util.Objects

import de.tudresden.inf.lat.tabulas.datatype.{ParameterizedListValue, PrimitiveTypeValue, Record, URIValue}
import de.tudresden.inf.lat.tabulas.parser.ParserConstant
import de.tudresden.inf.lat.tabulas.renderer.{Renderer, UncheckedWriter, UncheckedWriterImpl}
import de.tudresden.inf.lat.tabulas.table.{Table, TableMap}

/** Renderer of tables in comma-separated values format.
  */
class CsvRenderer extends Renderer {

  val Quotes: String = "\""
  val QuotesReplacement: String = "''"
  val Null: String = ""
  val Comma: String = ","

  private var _output: Writer = new OutputStreamWriter(System.out)

  def this(output: Writer) = {
    this()
    this._output = output
  }

  def sanitize(str: String): String = { str.replace(Quotes, QuotesReplacement) }

  def writeAsStringIfNotEmpty(output: UncheckedWriter, field: String, value: PrimitiveTypeValue): Boolean = {
    var result: Boolean = false
    if (Objects.nonNull(field) && !field.trim().isEmpty && Objects.nonNull(value)
      && !value.toString.trim().isEmpty) {
      output.write(Quotes)
      output.write(sanitize(value.toString))
      output.write(Quotes)
      result = true
    } else {
      output.write(Null)
      result = false
    }
    result
  }

  def writeParameterizedListIfNotEmpty(output: UncheckedWriter, field: String, list: ParameterizedListValue): Boolean = {
    var result: Boolean = false
    if (Objects.nonNull(list) && !list.isEmpty) {
      output.write(Quotes)
      list.foreach(value => {
        output.write(sanitize(value.toString))
        output.write(ParserConstant.Space)
      })
      output.write(Quotes)
      result = true
    } else {
      output.write(Null)
      result = false
    }
    result
  }

  def writeLinkIfNotEmpty(output: UncheckedWriter, field: String, link: URIValue): Boolean = {
    var result: Boolean = false
    if (Objects.nonNull(link) && !link.isEmpty) {
      output.write(Quotes)
      output.write(sanitize(link.toString))
      output.write(Quotes)
      result = true
    } else {
      output.write(Null)
      result = false
    }
    result
  }

  def render(output: UncheckedWriter, record: Record, fields: Seq[String]): Unit = {
    var first: Boolean = true
    for (field: String <- fields) {
      if (first) {
        first = false
      } else {
        output.write(Comma)
      }
      val optValue: Option[PrimitiveTypeValue] = record.get(field)
      if (optValue.isDefined) {
        val value: PrimitiveTypeValue = optValue.get
        value match {
          case list: ParameterizedListValue =>
            writeParameterizedListIfNotEmpty(output, field, list)
          case link: URIValue =>
            writeLinkIfNotEmpty(output, field, link)
          case _ =>
            writeAsStringIfNotEmpty(output, field, value)
        }

      } else {
        output.write(Null)
      }
    }
    output.write(ParserConstant.NewLine)
  }

  def renderAllRecords(output: UncheckedWriter, table: Table): Unit = {
    val list: Seq[Record] = table.getRecords
    list.foreach(record => {
      render(output, record, table.getType.getFields)
    })
  }

  def renderTypeSelection(output: UncheckedWriter, tableName: String, table: Table): Unit = {
    output.write(Quotes)
    output.write(tableName)
    output.write(Quotes)
    val n: Int = table.getType.getFields.size
    Range(1, n).foreach(index => {
      output.write(Comma)
      output.write(Null)
    })
    output.write(ParserConstant.NewLine)
  }

  def renderTypeDefinition(output: UncheckedWriter, table: Table): Unit = {
    var first: Boolean = true
    for (field: String <- table.getType.getFields) {
      if (first) {
        first = false
      } else {
        output.write(Comma)
      }
      output.write(Quotes)
      output.write(field)
      output.write(Quotes)
    }
    output.write(ParserConstant.NewLine)
  }

  def render(output: UncheckedWriter, tableMap: TableMap): Unit = {
    tableMap.getTableIds.foreach(tableName => {
      val table: Table = tableMap.getTable(tableName).get
      renderTypeSelection(output, tableName, table)
      renderTypeDefinition(output, table)
      renderAllRecords(output, table)
    })
    output.flush()
  }

  override def render(tableMap: TableMap): Unit = {
    render(new UncheckedWriterImpl(this._output), tableMap)
  }

}

object CsvRenderer {

  def apply(): CsvRenderer = new CsvRenderer

}
