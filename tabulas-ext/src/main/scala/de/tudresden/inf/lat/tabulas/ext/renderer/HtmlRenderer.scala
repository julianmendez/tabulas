
package de.tudresden.inf.lat.tabulas.ext.renderer

import java.io.{OutputStreamWriter, Writer}
import java.util.Objects

import de.tudresden.inf.lat.tabulas.datatype._
import de.tudresden.inf.lat.tabulas.renderer.{Renderer, UncheckedWriter, UncheckedWriterImpl}
import de.tudresden.inf.lat.tabulas.table.{Table, TableMap}

import scala.collection.mutable

/** Renderer of a table that creates an HTML document.
  */
class HtmlRenderer extends Renderer {

  val Prefix: String = "<?xml version=\"1.0\" encoding=\"utf-8\"?>" +
    "\n<!DOCTYPE html PUBLIC \"-//W3C//DTD XHTML 1.1//EN\" \"https://www.w3.org/TR/xhtml11/DTD/xhtml11.dtd\">" +
    "\n" +
    "\n<html xmlns=\"https://www.w3.org/1999/xhtml\" lang=\"en\">" +
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

  private var _output: Writer = new OutputStreamWriter(System.out)

  def this(output: Writer) = {
    this()
    this._output = output
  }

  def writeAsStringIfNotEmpty(output: UncheckedWriter, value: PrimitiveTypeValue): Boolean = {
    var result: Boolean = false
    if (Objects.nonNull(value) && !value.toString.trim().isEmpty) {
      output.write(value.toString)
      output.write("\n")
      result = true
    } else {
      result = false
    }
    result
  }

  def writeParameterizedListIfNotEmpty(output: UncheckedWriter, list: ParameterizedListValue): Boolean = {
    var result: Boolean = false
    if (Objects.nonNull(list)) {
      list.foreach(value => {
        if (value.getType.equals(new URIType())) {
          val link: URIValue = (new URIType()).castInstance(value)
          writeLinkIfNotEmpty(output, link)
        } else {
          val strVal: StringValue = (new StringType()).castInstance(value)
          writeAsStringIfNotEmpty(output, strVal)
        }
      })
      result = true
    } else {
      result = false
    }
    result
  }

  def writeLinkIfNotEmpty(output: UncheckedWriter, link: URIValue): Boolean = {
    var result: Boolean = false
    if (Objects.nonNull(link) && !link.isEmpty) {
      output.write("<a href=\"")
      output.write(link.getUriNoLabel.toASCIIString)
      output.write("\">(")
      output.write(link.getLabel)
      output.write(")</a>")
      output.write("\n")
      result = true
    } else {
      result = false
    }
    result
  }

  def render(output: UncheckedWriter, record: Record, fields: mutable.Buffer[String]): Unit = {
    fields.foreach(field => {
      val optValue: Option[PrimitiveTypeValue] = record.get(field)
      if (optValue.isDefined) {
        val value: PrimitiveTypeValue = optValue.get
        value match {
          case list: ParameterizedListValue=>
            output.write("<td> ")
            writeParameterizedListIfNotEmpty(output, list)
            output.write(" </td>\n")
          case link: URIValue =>
            output.write("<td> ")
            writeLinkIfNotEmpty(output, link)
            output.write(" </td>\n")
          case _ =>
            output.write("<td> ")
            writeAsStringIfNotEmpty(output, value)
            output.write(" </td>\n")
        }

      } else {
        output.write("<td> </td>\n")
        output.write("\n")
      }
    })
  }

  def renderAllRecords(output: UncheckedWriter, table: Table): Unit = {
    val list: mutable.Buffer[Record] = table.getRecords
    output.write("<table summary=\"\">\n")
    list.foreach(record => {
      output.write("<tr>\n")
      render(output, record, table.getType.getFields)
      output.write("</tr>\n")
    })
    output.write("</table>\n")
  }

  def renderMap(output: UncheckedWriter, map: mutable.Map[String, String]): Unit = {
    output.write("<table summary=\"\" border=\"1\">\n")
    map.keySet.foreach(key => {
      val value: String = map.get(key).get
      output.write("<tr>\n")
      output.write("<td>")
      output.write(key)
      output.write("</td>\n")
      output.write("<td>")
      output.write(value)
      output.write("</td>\n")
      output.write("</tr>\n")
    })
    output.write("</table>\n")
    output.write("\n")
  }

  def render(output: UncheckedWriter, tableMap: TableMap): Unit = {
    output.write(Prefix)
    tableMap.getTableIds.foreach(tableName => {
      val table: Table = tableMap.getTable(tableName).get
      renderAllRecords(output, table)
    })
    output.write("\n")
    output.write("\n")
    output.write(Suffix)
    output.flush()
  }

  override def render(tableMap: TableMap): Unit = {
    render(new UncheckedWriterImpl(this._output), tableMap)
  }

}

