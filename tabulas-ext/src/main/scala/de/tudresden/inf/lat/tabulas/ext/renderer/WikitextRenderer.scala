
package de.tudresden.inf.lat.tabulas.ext.renderer

import java.io.{OutputStreamWriter, Writer}
import java.util.Objects

import de.tudresden.inf.lat.tabulas.datatype._
import de.tudresden.inf.lat.tabulas.parser.ParserConstant
import de.tudresden.inf.lat.tabulas.renderer.{Renderer, UncheckedWriter, UncheckedWriterImpl}
import de.tudresden.inf.lat.tabulas.table.{Table, TableMap}

import scala.collection.mutable.{Buffer, Map}

/**
  * Renderer of a table.
  */
class WikitextRenderer extends Renderer {

  private var _output: Writer = new OutputStreamWriter(System.out)

  def this(output: Writer) = {
    this()
    this._output = output
  }

  def writeAsStringIfNotEmpty(output: UncheckedWriter, prefix: String, value: PrimitiveTypeValue): Boolean = {
    if (Objects.nonNull(value) && !value.toString.trim().isEmpty) {
      output.write(prefix)
      output.write(value.toString)
      output.write("\n")
      return true
    } else {
      return false
    }
  }

  def writeParameterizedListIfNotEmpty(output: UncheckedWriter, prefix: String, list: ParameterizedListValue): Boolean = {
    if (Objects.nonNull(list)) {
      output.write(prefix)
      list.foreach(value => {
        if (value.getType.equals(new URIType())) {
          val link: URIValue = (new URIType()).castInstance(value)
          writeLinkIfNotEmpty(output, "", link)
        } else {
          val strVal: StringValue = (new StringType()).castInstance(value)
          writeAsStringIfNotEmpty(output, "", strVal)
        }
      })
      return true
    } else {
      return false
    }
  }

  def writeLinkIfNotEmpty(output: UncheckedWriter, prefix: String, link: URIValue): Boolean = {
    if (Objects.nonNull(link) && !link.isEmpty) {
      output.write(prefix)
      output.write("[")
      output.write(link.getUriNoLabel.toASCIIString)
      output.write(" (")
      output.write(link.getLabel)
      output.write(")]")
      output.write("\n")
      return true
    } else {
      return false
    }
  }

  def render(output: UncheckedWriter, record: Record, fields: Buffer[String]): Unit = {

    fields.foreach(field => {
      val optValue: Option[PrimitiveTypeValue] = record.get(field)
      output.write("|")
      if (optValue.isDefined) {
        val value: PrimitiveTypeValue = optValue.get
        val prefix = field + ParserConstant.EqualsSign
        if (value.isInstanceOf[ParameterizedListValue]) {
          val list: ParameterizedListValue = value.asInstanceOf[ParameterizedListValue]
          writeParameterizedListIfNotEmpty(output, prefix, list)

        } else if (value.isInstanceOf[URIValue]) {
          val link: URIValue = value.asInstanceOf[URIValue]
          writeLinkIfNotEmpty(output, prefix, link)

        } else {
          writeAsStringIfNotEmpty(output, prefix, value)

        }

      } else {
        output.write("\n")
      }
    })
  }

  def renderAllRecords(output: UncheckedWriter, table: CompositeTypeValue): Unit = {
    val list: Buffer[Record] = table.getRecords
    output.write("{|\n")
    output.write("|-\n")
    list.foreach(record => {
      render(output, record, table.getType.getFields)
      output.write("|-\n")
    })
    output.write("|}\n")
  }

  def renderMap(output: UncheckedWriter, map: Map[String, String]): Unit = {
    output.write("{| border=\"1\"\n")
    output.write("|-\n")
    map.keySet.foreach(key => {
      val value: String = map.get(key).get
      output.write("| ")
      output.write(key)
      output.write("\n")
      output.write("| ")
      output.write(value)
      output.write("\n")
      output.write("|-\n")
    })
    output.write("|}\n")
    output.write("\n")
  }

  def render(output: UncheckedWriter, tableMap: TableMap): Unit = {
    output.write("\n")
    tableMap.getTableIds.foreach(tableId => {
      val table: Table = tableMap.getTable(tableId).get
      renderAllRecords(output, table)
    })
    output.write("\n")
    output.write("\n")
    output.flush()
  }

  override def render(tableMap: TableMap): Unit = {
    render(new UncheckedWriterImpl(this._output), tableMap)
  }

}

