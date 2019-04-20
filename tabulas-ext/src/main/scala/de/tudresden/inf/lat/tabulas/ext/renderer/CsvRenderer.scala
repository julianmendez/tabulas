
package de.tudresden.inf.lat.tabulas.ext.renderer

import java.io.{OutputStreamWriter, Writer}
import java.util.Objects

import de.tudresden.inf.lat.tabulas.datatype.{ParameterizedListValue, PrimitiveTypeValue, Record, URIValue}
import de.tudresden.inf.lat.tabulas.parser.ParserConstant
import de.tudresden.inf.lat.tabulas.renderer.Renderer
import de.tudresden.inf.lat.tabulas.table.{Table, TableMap}

/** Renderer of tables in comma-separated values format.
  */
case class CsvRenderer() extends Renderer {

  final val Quotes: String = "\""
  final val QuotesReplacement: String = "''"
  final val Null: String = ""
  final val Comma: String = ","

  override def render(output: Writer, tableMap: TableMap): Unit = {
    tableMap.getTableIds.foreach(tableName => {
      val table: Table = tableMap.getTable(tableName).get
      renderTypeSelection(output, tableName, table)
      renderTypeDefinition(output, table)
      renderAllRecords(output, table)
    })
    output.flush()
  }

  def renderAllRecords(output: Writer, table: Table): Unit = {
    val list: Seq[Record] = table.getRecords
    list.foreach(record => {
      render(output, record, table.getType.getFields)
    })
  }

  def render(output: Writer, record: Record, fields: Seq[String]): Unit = {
    var first = true
    fields.foreach(field => {
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
    })
    output.write(ParserConstant.NewLine)
  }

  def writeAsStringIfNotEmpty(output: Writer, field: String, value: PrimitiveTypeValue): Boolean = {
    val result = if (Objects.nonNull(field) && !field.trim().isEmpty && Objects.nonNull(value)
      && !value.toString.trim().isEmpty) {
      output.write(Quotes)
      output.write(sanitize(value.toString))
      output.write(Quotes)
      true
    } else {
      output.write(Null)
      false
    }
    result
  }

  def writeParameterizedListIfNotEmpty(output: Writer, field: String, list: ParameterizedListValue): Boolean = {
    val result = if (Objects.nonNull(list) && !list.isEmpty) {
      output.write(Quotes)
      list.getList.foreach(value => {
        output.write(sanitize(value.toString))
        output.write(ParserConstant.Space)
      })
      output.write(Quotes)
      true
    } else {
      output.write(Null)
      false
    }
    result
  }

  def sanitize(str: String): String = {
    str.replace(Quotes, QuotesReplacement)
  }

  def writeLinkIfNotEmpty(output: Writer, field: String, link: URIValue): Boolean = {
    val result = false
    if (Objects.nonNull(link) && !link.isEmpty) {
      output.write(Quotes)
      output.write(sanitize(link.toString))
      output.write(Quotes)
      true
    } else {
      output.write(Null)
      false
    }
    result
  }

  def renderTypeSelection(output: Writer, tableName: String, table: Table): Unit = {
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

  def renderTypeDefinition(output: Writer, table: Table): Unit = {
    var first = true
    table.getType.getFields.foreach(field => {
      if (first) {
        first = false
      } else {
        output.write(Comma)
      }
      output.write(Quotes)
      output.write(field)
      output.write(Quotes)
    })
    output.write(ParserConstant.NewLine)
  }

}

