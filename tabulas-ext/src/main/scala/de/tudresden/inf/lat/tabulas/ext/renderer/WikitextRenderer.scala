
package de.tudresden.inf.lat.tabulas.ext.renderer

import java.io.OutputStreamWriter
import java.io.Writer
import java.util.List
import java.util.Map
import java.util.Objects
import java.util.Optional

import scala.collection.JavaConversions.asScalaBuffer
import scala.collection.JavaConversions.asScalaSet

import de.tudresden.inf.lat.tabulas.datatype.CompositeTypeValue
import de.tudresden.inf.lat.tabulas.datatype.ParameterizedListValue
import de.tudresden.inf.lat.tabulas.datatype.PrimitiveTypeValue
import de.tudresden.inf.lat.tabulas.datatype.Record
import de.tudresden.inf.lat.tabulas.datatype.StringType
import de.tudresden.inf.lat.tabulas.datatype.StringValue
import de.tudresden.inf.lat.tabulas.datatype.URIType
import de.tudresden.inf.lat.tabulas.datatype.URIValue
import de.tudresden.inf.lat.tabulas.parser.ParserConstant
import de.tudresden.inf.lat.tabulas.renderer.Renderer
import de.tudresden.inf.lat.tabulas.renderer.UncheckedWriter
import de.tudresden.inf.lat.tabulas.renderer.UncheckedWriterImpl
import de.tudresden.inf.lat.tabulas.table.Table
import de.tudresden.inf.lat.tabulas.table.TableMap

/**
 * Renderer of a table.
 */
class WikitextRenderer extends Renderer {

  private var output: Writer = new OutputStreamWriter(System.out)

  def this(output: Writer) = {
    this()
    this.output = output
  }

  def writeStringIfNotEmpty(output: UncheckedWriter, prefix: String, str: StringValue): Boolean = {
    if (Objects.nonNull(str) && !str.toString().trim().isEmpty()) {
      output.write(prefix)
      output.write(str.toString())
      output.write("\n")
      return true
    } else {
      return false
    }
  }

  def writeParameterizedListIfNotEmpty(output: UncheckedWriter, prefix: String, list: ParameterizedListValue): Boolean = {
    if (Objects.nonNull(list)) {
      output.write(prefix);
      list.foreach(value => {
        if (value.getType().equals(new URIType())) {
          val link: URIValue = (new URIType()).castInstance(value)
          writeLinkIfNotEmpty(output, "", link)
        } else {
          val strVal: StringValue = (new StringType()).castInstance(value)
          writeStringIfNotEmpty(output, "", strVal)
        }
      })
      return true
    } else {
      return false
    }
  }

  def writeLinkIfNotEmpty(output: UncheckedWriter, prefix: String, link: URIValue): Boolean = {
    if (Objects.nonNull(link) && !link.isEmpty()) {
      output.write(prefix);
      output.write("[")
      output.write(link.getUriNoLabel().toASCIIString())
      output.write(" (")
      output.write(link.getLabel())
      output.write(")]")
      output.write("\n")
      return true
    } else {
      return false
    }
  }

  def render(output: UncheckedWriter, record: Record, fields: List[String]): Unit = {

    fields.foreach(field => {
      val optValue: Optional[PrimitiveTypeValue] = record.get(field)
      output.write("|")
      if (optValue.isPresent()) {
        val value: PrimitiveTypeValue = optValue.get();
        val prefix = field + ParserConstant.EqualsSign
        if (value.isInstanceOf[StringValue]) {
          val strVal: StringValue = value.asInstanceOf[StringValue]
          writeStringIfNotEmpty(output, prefix, strVal)

        } else if (value.isInstanceOf[ParameterizedListValue]) {
          val list: ParameterizedListValue = value.asInstanceOf[ParameterizedListValue]
          writeParameterizedListIfNotEmpty(output, prefix, list)

        } else if (value.isInstanceOf[URIValue]) {
          val link: URIValue = value.asInstanceOf[URIValue]
          writeLinkIfNotEmpty(output, prefix, link)

        } else {
          throw new IllegalStateException("Invalid value '" + value.toString() + "'.")
        }

      } else {
        output.write("\n")
      }
    })
  }

  def renderAllRecords(output: UncheckedWriter, table: CompositeTypeValue): Unit = {
    val list: List[Record] = table.getRecords()
    output.write("{|\n")
    output.write("|-\n")
    list.foreach(record => {
      render(output, record, table.getType().getFields())
      output.write("|-\n")
    })
    output.write("|}\n")
  }

  def renderMap(output: UncheckedWriter, map: Map[String, String]): Unit = {
    output.write("{| border=\"1\"\n")
    output.write("|-\n")
    map.keySet().foreach(key => {
      val value: String = map.get(key)
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
    tableMap.getTableIds().foreach(tableId => {
      val table: Table = tableMap.getTable(tableId)
      renderAllRecords(output, table)
    })
    output.write("\n")
    output.write("\n")
    output.flush()
  }

  override def render(tableMap: TableMap): Unit = {
    render(new UncheckedWriterImpl(this.output), tableMap)
  }

}

