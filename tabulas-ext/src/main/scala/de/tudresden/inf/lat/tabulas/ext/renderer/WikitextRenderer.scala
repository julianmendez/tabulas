
package de.tudresden.inf.lat.tabulas.ext.renderer

import java.io.Writer
import java.util.Objects

import de.tudresden.inf.lat.tabulas.datatype._
import de.tudresden.inf.lat.tabulas.parser.ParserConstant
import de.tudresden.inf.lat.tabulas.renderer.Renderer
import de.tudresden.inf.lat.tabulas.table.{Table, TableMap}

/** Renderer of a table.
  */
case class WikitextRenderer() extends Renderer {

  override def render(output: Writer, tableMap: TableMap): Unit = {
    tableMap.getTableIds.foreach(tableId => {
      renderTable(output, tableId, tableMap.getTable(tableId).get)
    })
  }

  def renderTable(output: Writer, tableId: String, table: Table): Unit = {
    renderTable(output, table)
  }

  def renderTable(output: Writer, table: Table): Unit = {
    val list: Seq[Record] = table.getRecords
    output.write("{|\n")
    output.write("|-\n")
    list.foreach(record => {
      render(output, record, table.getType.getFields)
      output.write("|-\n")
    })
    output.write("|}\n")
    output.flush()
  }

  def render(output: Writer, record: Record, fields: Seq[String]): Unit = {
    fields.foreach(field => {
      val optValue: Option[PrimitiveTypeValue] = record.get(field)
      output.write("|")
      if (optValue.isDefined) {
        val value: PrimitiveTypeValue = optValue.get
        val prefix = field + ParserConstant.EqualsFieldSign
        value match {
          case list: ParameterizedListValue =>
            writeParameterizedListIfNotEmpty(output, prefix, list)
          case link: URIValue =>
            writeLinkIfNotEmpty(output, prefix, link)
          case _ =>
            writeAsStringIfNotEmpty(output, prefix, value)
        }

      } else {
        output.write("\n")
      }
    })
  }

  def writeParameterizedListIfNotEmpty(output: Writer, prefix: String, list: ParameterizedListValue): Boolean = {
    val result = if (Objects.nonNull(list)) {
      output.write(prefix)
      list.getList.foreach(value => {
        if (value.getType.equals(URIType())) {
          val link: URIValue = URIType().castInstance(value)
          writeLinkIfNotEmpty(output, "", link)
        } else {
          val strVal: StringValue = StringType().castInstance(value)
          writeAsStringIfNotEmpty(output, "", strVal)
        }
      })
      true
    } else {
      false
    }
    result
  }

  def writeAsStringIfNotEmpty(output: Writer, prefix: String, value: PrimitiveTypeValue): Boolean = {
    val result = if (Objects.nonNull(value) && !value.toString.trim().isEmpty) {
      output.write(prefix)
      output.write(value.toString)
      output.write("\n")
      true
    } else {
      false
    }
    result
  }

  def writeLinkIfNotEmpty(output: Writer, prefix: String, link: URIValue): Boolean = {
    val result = if (Objects.nonNull(link) && !link.isEmpty) {
      output.write(prefix)
      output.write("[")
      output.write(link.getUriNoLabel.toString)
      output.write(" (")
      output.write(link.getLabel)
      output.write(")]")
      output.write("\n")
      true
    } else {
      false
    }
    result
  }

}

object WikitextRenderer {}
