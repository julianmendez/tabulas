
package de.tudresden.inf.lat.tabulas.ext.renderer

import java.io.IOException
import java.io.OutputStreamWriter
import java.io.Writer
import java.util.List
import java.util.Map

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
import de.tudresden.inf.lat.tabulas.table.Table
import de.tudresden.inf.lat.tabulas.table.TableMap

/**
 * Renderer of a table.
 */
class WikitextRenderer extends Renderer {

  private var output: Writer = new OutputStreamWriter(System.out)

  def this(output0: Writer) = {
    this()
    output = output0
  }

  def writeStringIfNotEmpty(output: Writer, prefix: String, str: StringValue): Boolean = {
    if (str != null && !str.toString().trim().isEmpty()) {
      output.write(prefix)
      output.write(str.toString())
      output.write("\n")
      true
    } else {
      false
    }
  }

  def writeParameterizedListIfNotEmpty(output: Writer, prefix: String, list: ParameterizedListValue): Boolean = {
    if (list != null) {
      output.write(prefix);
      for (value: PrimitiveTypeValue <- list) {
        if (value.getType().equals(new URIType())) {
          val link: URIValue = (new URIType()).castInstance(value)
          writeLinkIfNotEmpty(output, "", link)
        } else {
          val strVal: StringValue = (new StringType()).castInstance(value)
          writeStringIfNotEmpty(output, "", strVal)
        }
      }
      true
    } else {
      false
    }
  }

  def writeLinkIfNotEmpty(output: Writer, prefix: String, link: URIValue): Boolean = {
    if (link != null && !link.isEmpty()) {
      output.write(prefix);
      output.write("[")
      output.write(link.getUriNoLabel().toASCIIString())
      output.write(" (")
      output.write(link.getLabel())
      output.write(")]")
      output.write("\n")
      true
    } else {
      false
    }
  }

  def render(output: Writer, record: Record, fields: List[String]): Unit = {

    for (field: String <- fields) {
      val value: PrimitiveTypeValue = record.get(field)
      output.write("|")
      if (value == null) {
        output.write("\n")
      } else {
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

      }
    }
  }

  def renderAllRecords(output: Writer, table: CompositeTypeValue): Unit = {
    val list: List[Record] = table.getRecords()
    output.write("{|\n")
    output.write("|-\n")
    for (record: Record <- list) {
      render(output, record, table.getType().getFields())
      output.write("|-\n")
    }
    output.write("|}\n")
  }

  def renderMap(output: Writer, map: Map[String, String]): Unit = {
    output.write("{| border=\"1\"\n")
    output.write("|-\n")
    for (key: String <- map.keySet()) {
      val value: String = map.get(key)
      output.write("| ")
      output.write(key)
      output.write("\n")
      output.write("| ")
      output.write(value)
      output.write("\n")
      output.write("|-\n")
    }
    output.write("|}\n")
    output.write("\n")
  }

  def render(output: Writer, tableMap: TableMap): Unit = {
    try {
      output.write("\n")
      for (tableId: String <- tableMap.getTableIds()) {
        val table: Table = tableMap.getTable(tableId)
        renderAllRecords(output, table)
      }
      output.write("\n")
      output.write("\n")
      output.flush()
    } catch {
      case e: IOException => {
        throw new RuntimeException(e)
      }
    }
  }

  override def render(tableMap: TableMap): Unit = {
    try {
      render(this.output, tableMap)
    } catch {
      case e: IOException => {
        throw new RuntimeException(e)
      }
    }
  }

}

