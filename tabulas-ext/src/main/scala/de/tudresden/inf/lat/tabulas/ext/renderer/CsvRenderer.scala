
package de.tudresden.inf.lat.tabulas.ext.renderer

import java.io.IOException
import java.io.OutputStreamWriter
import java.io.Writer
import java.util.List
import scala.collection.JavaConversions.asScalaBuffer
import de.tudresden.inf.lat.tabulas.datatype.ParameterizedListValue
import de.tudresden.inf.lat.tabulas.datatype.PrimitiveTypeValue
import de.tudresden.inf.lat.tabulas.datatype.Record
import de.tudresden.inf.lat.tabulas.datatype.StringValue
import de.tudresden.inf.lat.tabulas.datatype.URIValue
import de.tudresden.inf.lat.tabulas.parser.ParserConstant
import de.tudresden.inf.lat.tabulas.renderer.Renderer
import de.tudresden.inf.lat.tabulas.table.Table
import de.tudresden.inf.lat.tabulas.table.TableMap
import de.tudresden.inf.lat.tabulas.renderer.UncheckedWriter
import de.tudresden.inf.lat.tabulas.renderer.UncheckedWriterImpl

/**
 * Renderer of tables in comma-separated values format.
 */
class CsvRenderer extends Renderer {

  val Quotes: String = "\""
  val QuotesReplacement: String = "''"
  val Null: String = ""
  val Comma: String = ","

  private var output: Writer = new OutputStreamWriter(System.out)

  def this(output: Writer) = {
    this()
    this.output = output
  }

  def sanitize(str: String): String = {
    str.replace(Quotes, QuotesReplacement)
  }

  def writeStringIfNotEmpty(output: UncheckedWriter, field: String, value: StringValue): Boolean = {
    if (field != null && !field.trim().isEmpty() && value != null
      && !value.toString().trim().isEmpty()) {
      output.write(Quotes)
      output.write(sanitize(value.toString()))
      output.write(Quotes)
      true
    } else {
      output.write(Null)
      false
    }
  }

  def writeParameterizedListIfNotEmpty(output: UncheckedWriter, field: String, list: ParameterizedListValue): Boolean = {
    if (list != null && !list.isEmpty()) {
      output.write(Quotes)
      list.foreach(value => {
        output.write(sanitize(value.toString()))
        output.write(ParserConstant.Space)
      })
      output.write(Quotes)
      true
    } else {
      output.write(Null)
      false
    }
  }

  def writeLinkIfNotEmpty(output: UncheckedWriter, field: String, link: URIValue): Boolean = {
    if (link != null && !link.isEmpty()) {
      output.write(Quotes)
      output.write(sanitize(link.toString()))
      output.write(Quotes)
      true
    } else {
      output.write(Null)
      false
    }
  }

  def render(output: UncheckedWriter, record: Record, fields: List[String]): Unit = {

    var first: Boolean = true
    for (field: String <- fields) {
      if (first) {
        first = false
      } else {
        output.write(Comma)
      }
      val value: PrimitiveTypeValue = record.get(field)
      if (value != null) {
        if (value.isInstanceOf[StringValue]) {
          val strVal: StringValue = value.asInstanceOf[StringValue]
          writeStringIfNotEmpty(output, field, strVal)

        } else if (value.isInstanceOf[ParameterizedListValue]) {
          val list: ParameterizedListValue = value.asInstanceOf[ParameterizedListValue]
          writeParameterizedListIfNotEmpty(output, field, list)

        } else if (value.isInstanceOf[URIValue]) {
          val link: URIValue = value.asInstanceOf[URIValue]
          writeLinkIfNotEmpty(output, field, link)

        } else {
          throw new IllegalStateException("Invalid value '"
            + value.toString() + "'.")
        }

      } else {
        output.write(Null)
      }
    }
    output.write(ParserConstant.NewLine)
  }

  def renderAllRecords(output: UncheckedWriter, table: Table): Unit = {
    val list: List[Record] = table.getRecords()
    list.foreach(record => {
      render(output, record, table.getType().getFields())
    })
  }

  def renderTypeSelection(output: UncheckedWriter, tableName: String, table: Table): Unit = {
    output.write(Quotes)
    output.write(tableName)
    output.write(Quotes)
    val n: Int = table.getType().getFields().size()
    Range(1, n).foreach(index => {
      output.write(Comma)
      output.write(Null)
    })
    output.write(ParserConstant.NewLine)
  }

  def renderTypeDefinition(output: UncheckedWriter, table: Table): Unit = {
    var first: Boolean = true
    for (field: String <- table.getType().getFields()) {
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
    tableMap.getTableIds().foreach(tableName => {
      val table: Table = tableMap.getTable(tableName)
      renderTypeSelection(output, tableName, table)
      renderTypeDefinition(output, table)
      renderAllRecords(output, table)
    })
    output.flush()
  }

  override def render(tableMap: TableMap): Unit = {
    render(new UncheckedWriterImpl(this.output), tableMap)
  }

}

