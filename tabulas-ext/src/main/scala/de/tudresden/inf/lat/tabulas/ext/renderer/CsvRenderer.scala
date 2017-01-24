
package de.tudresden.inf.lat.tabulas.ext.renderer

import java.io.OutputStreamWriter
import java.io.Writer
import java.util.List
import java.util.Objects
import java.util.Optional

import scala.Range
import scala.collection.JavaConverters.asScalaBufferConverter

import de.tudresden.inf.lat.tabulas.datatype.ParameterizedListValue
import de.tudresden.inf.lat.tabulas.datatype.PrimitiveTypeValue
import de.tudresden.inf.lat.tabulas.datatype.Record
import de.tudresden.inf.lat.tabulas.datatype.StringValue
import de.tudresden.inf.lat.tabulas.datatype.URIValue
import de.tudresden.inf.lat.tabulas.parser.ParserConstant
import de.tudresden.inf.lat.tabulas.renderer.Renderer
import de.tudresden.inf.lat.tabulas.renderer.UncheckedWriter
import de.tudresden.inf.lat.tabulas.renderer.UncheckedWriterImpl
import de.tudresden.inf.lat.tabulas.table.Table
import de.tudresden.inf.lat.tabulas.table.TableMap

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
    return str.replace(Quotes, QuotesReplacement)
  }

  def writeAsStringIfNotEmpty(output: UncheckedWriter, field: String, value: PrimitiveTypeValue): Boolean = {
    if (Objects.nonNull(field) && !field.trim().isEmpty() && Objects.nonNull(value)
      && !value.toString().trim().isEmpty()) {
      output.write(Quotes)
      output.write(sanitize(value.toString()))
      output.write(Quotes)
      return true
    } else {
      output.write(Null)
      return false
    }
  }

  def writeParameterizedListIfNotEmpty(output: UncheckedWriter, field: String, list: ParameterizedListValue): Boolean = {
    if (Objects.nonNull(list) && !list.isEmpty()) {
      output.write(Quotes)
      list.asScala.foreach(value => {
        output.write(sanitize(value.toString()))
        output.write(ParserConstant.Space)
      })
      output.write(Quotes)
      return true
    } else {
      output.write(Null)
      return false
    }
  }

  def writeLinkIfNotEmpty(output: UncheckedWriter, field: String, link: URIValue): Boolean = {
    if (Objects.nonNull(link) && !link.isEmpty()) {
      output.write(Quotes)
      output.write(sanitize(link.toString()))
      output.write(Quotes)
      return true
    } else {
      output.write(Null)
      return false
    }
  }

  def render(output: UncheckedWriter, record: Record, fields: List[String]): Unit = {

    var first: Boolean = true
    for (field: String <- fields.asScala) {
      if (first) {
        first = false
      } else {
        output.write(Comma)
      }
      val optValue: Optional[PrimitiveTypeValue] = record.get(field)
      if (optValue.isPresent()) {
        val value: PrimitiveTypeValue = optValue.get()
        if (value.isInstanceOf[ParameterizedListValue]) {
          val list: ParameterizedListValue = value.asInstanceOf[ParameterizedListValue]
          writeParameterizedListIfNotEmpty(output, field, list)

        } else if (value.isInstanceOf[URIValue]) {
          val link: URIValue = value.asInstanceOf[URIValue]
          writeLinkIfNotEmpty(output, field, link)

        } else {
          writeAsStringIfNotEmpty(output, field, value)

        }

      } else {
        output.write(Null)
      }
    }
    output.write(ParserConstant.NewLine)
  }

  def renderAllRecords(output: UncheckedWriter, table: Table): Unit = {
    val list: List[Record] = table.getRecords()
    list.asScala.foreach(record => {
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
    for (field: String <- table.getType().getFields().asScala) {
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
    tableMap.getTableIds().asScala.foreach(tableName => {
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

