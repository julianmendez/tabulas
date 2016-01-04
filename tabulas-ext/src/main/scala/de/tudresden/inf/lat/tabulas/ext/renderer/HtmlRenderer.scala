
package de.tudresden.inf.lat.tabulas.ext.renderer

import java.io.IOException
import java.io.OutputStreamWriter
import java.io.Writer
import java.util.List
import java.util.Map

import scala.collection.JavaConversions.asScalaBuffer
import scala.collection.JavaConversions.asScalaSet

import de.tudresden.inf.lat.tabulas.datatype.ParameterizedListValue
import de.tudresden.inf.lat.tabulas.datatype.PrimitiveTypeValue
import de.tudresden.inf.lat.tabulas.datatype.Record
import de.tudresden.inf.lat.tabulas.datatype.StringType
import de.tudresden.inf.lat.tabulas.datatype.StringValue
import de.tudresden.inf.lat.tabulas.datatype.URIType
import de.tudresden.inf.lat.tabulas.datatype.URIValue
import de.tudresden.inf.lat.tabulas.renderer.Renderer
import de.tudresden.inf.lat.tabulas.table.Table
import de.tudresden.inf.lat.tabulas.table.TableMap

/**
 * Renderer of a table that creates an HTML document.
 */
class HtmlRenderer extends Renderer {

  val Prefix: String = "<?xml version=\"1.0\" encoding=\"utf-8\"?>" +
    "\n<!DOCTYPE html PUBLIC \"-//W3C//DTD XHTML 1.1//EN\" \"http://www.w3.org/TR/xhtml11/DTD/xhtml11.dtd\">" +
    "\n" +
    "\n<html xmlns=\"http://www.w3.org/1999/xhtml\" lang=\"en\">" +
    "\n<head>" +
    "\n  <meta http-equiv=\"Content-Type\" content=\"text/html; charset=utf-8\" />" +
    "\n  <title></title>" +
    "\n</head>" +
    "\n<body>" +
    "\n  <div>" +
    "\n" +
    "\n<br />" +
    "\n" +
    "\n"

  val Suffix: String = "\n" +
    "\n" +
    "\n  </div>" +
    "\n</body>" +
    "\n</html>" +
    "\n"

  private var output: Writer = new OutputStreamWriter(System.out)

  def this(output0: Writer) = {
    this()
    output = output0
  }

  def writeStringIfNotEmpty(output: Writer, str: StringValue): Boolean = {
    if (str != null && !str.toString().trim().isEmpty()) {
      output.write(str.toString())
      output.write("\n")
      true
    } else {
      false
    }
  }

  def writeParameterizedListIfNotEmpty(output: Writer, list: ParameterizedListValue): Boolean = {
    if (list != null) {
      for (value: PrimitiveTypeValue <- list) {
        if (value.getType().equals(new URIType())) {
          val link: URIValue = (new URIType()).castInstance(value)
          writeLinkIfNotEmpty(output, link)
        } else {
          val strVal: StringValue = (new StringType()).castInstance(value)
          writeStringIfNotEmpty(output, strVal)
        }
      }
      true
    } else {
      false
    }
  }

  def writeLinkIfNotEmpty(output: Writer, link: URIValue): Boolean = {
    if (link != null && !link.isEmpty()) {
      output.write("<a href=\"")
      output.write(link.getUriNoLabel().toASCIIString())
      output.write("\">(")
      output.write(link.getLabel())
      output.write(")</a>")
      output.write("\n")
      true
    } else {
      false
    }
  }

  def render(output: Writer, record: Record, fields: List[String]): Unit = {
    for (field: String <- fields) {
      val value: PrimitiveTypeValue = record.get(field)
      if (value == null) {
        output.write("<td> </td>\n")
        output.write("\n")
      } else {
        if (value.isInstanceOf[StringValue]) {
          output.write("<td> ")
          val strVal: StringValue = value.asInstanceOf[StringValue]
          writeStringIfNotEmpty(output, strVal)
          output.write(" </td>\n")

        } else if (value.isInstanceOf[ParameterizedListValue]) {
          output.write("<td> ")
          val list: ParameterizedListValue = value.asInstanceOf[ParameterizedListValue]
          writeParameterizedListIfNotEmpty(output, list)
          output.write(" </td>\n")

        } else if (value.isInstanceOf[URIValue]) {
          output.write("<td> ")
          val link: URIValue = value.asInstanceOf[URIValue]
          writeLinkIfNotEmpty(output, link)
          output.write(" </td>\n")

        } else {
          throw new IllegalStateException("Invalid value '" + value.toString() + "'.")
        }

      }
    }
  }

  def renderAllRecords(output: Writer, table: Table): Unit = {
    val list: List[Record] = table.getRecords()
    output.write("<table summary=\"\">\n")
    for (record: Record <- list) {
      output.write("<tr>\n")
      render(output, record, table.getType().getFields())
      output.write("</tr>\n")
    }
    output.write("</table>\n")
  }

  def renderMap(output: Writer, map: Map[String, String]): Unit = {
    output.write("<table summary=\"\" border=\"1\">\n")
    for (key: String <- map.keySet()) {
      val value: String = map.get(key)
      output.write("<tr>\n")
      output.write("<td>")
      output.write(key)
      output.write("</td>\n")
      output.write("<td>")
      output.write(value)
      output.write("</td>\n")
      output.write("</tr>\n")
    }
    output.write("</table>\n")
    output.write("\n")
  }

  def render(output: Writer, tableMap: TableMap): Unit = {
    try {
      output.write(Prefix)
      for (tableName: String <- tableMap.getTableIds()) {
        val table: Table = tableMap.getTable(tableName)
        renderAllRecords(output, table)
      }
      output.write("\n")
      output.write("\n")
      output.write(Suffix)
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

