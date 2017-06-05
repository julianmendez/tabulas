
package de.tudresden.inf.lat.tabulas.renderer

import java.io.{OutputStreamWriter, Writer}
import java.net.URI
import java.util.Objects

import de.tudresden.inf.lat.tabulas.datatype._
import de.tudresden.inf.lat.tabulas.parser.ParserConstant

import scala.collection.mutable
import scala.collection.mutable.{Map, TreeMap}

/**
  * Renderer of a table in simple format.
  */
class SimpleFormatRecordRenderer extends RecordRenderer {

  private var output: Writer = new OutputStreamWriter(System.out)
  private var prefixMap: Map[URI, URI] = new TreeMap[URI, URI]

  def this(output: Writer, prefixMap: Map[URI, URI]) = {
    this()
    Objects.requireNonNull(output)
    this.output = output
    this.prefixMap = prefixMap
  }

  def this(output: UncheckedWriter, prefixMap: Map[URI, URI]) = {
    this()
    Objects.requireNonNull(output)
    this.output = output.asWriter()
    this.prefixMap = prefixMap
  }

  def renderWithPrefix(uriStr: String): String = {
    var ret: String = uriStr
    var found: Boolean = false
    prefixMap.keySet.foreach(key => {
      val expansion = prefixMap.get(key).get.toASCIIString()
      if (!found && uriStr.startsWith(expansion)) {
        ret = ParserConstant.PrefixAmpersand + key.toASCIIString() + ParserConstant.PrefixSemicolon + uriStr.substring(expansion.length)
        found = true
      }
    })
    return ret
  }

  def writeIfNotEmpty(output: UncheckedWriter, field: String, value: PrimitiveTypeValue): Boolean = {
    if (Objects.nonNull(field) && !field.trim().isEmpty() && Objects.nonNull(value) && !value.isEmpty()) {
      output.write(ParserConstant.NewLine)
      output.write(field)
      output.write(ParserConstant.Space + ParserConstant.EqualsSign)
      if (value.getType().isList()) {
        var hasUris: Boolean = false
        if (value.isInstanceOf[ParameterizedListValue]) {
          hasUris = (value.asInstanceOf[ParameterizedListValue]).getParameter().equals(new URIType())
        }
        value.getType()
        val list: mutable.Buffer[String] = value.renderAsList()
        list.foreach(elem => {
          output.write(ParserConstant.Space + ParserConstant.LineContinuationSymbol)
          output.write(ParserConstant.NewLine)
          output.write(ParserConstant.Space)
          if (hasUris) {
            output.write(renderWithPrefix(elem))
          } else {
            output.write(elem.toString())
          }
        })

      } else {
        output.write(ParserConstant.Space)
        if (value.getType().equals(new URIType())) {
          output.write(renderWithPrefix(value.toString()))
        } else {
          output.write(value.toString())
        }
      }
      return true
    } else {
      return false
    }
  }

  def render(output: UncheckedWriter, record: Record, fields: mutable.Buffer[String]): Unit = {
    output.write(ParserConstant.NewLine)
    output.write(ParserConstant.NewRecordToken + ParserConstant.Space)
    output.write(ParserConstant.EqualsSign + ParserConstant.Space)

    fields.foreach(field => {
      val optValue: Option[PrimitiveTypeValue] = record.get(field)
      if (optValue.isDefined) {
        writeIfNotEmpty(output, field, optValue.get)
      }
    })

    output.write(ParserConstant.NewLine)
    output.flush()
  }

  override def render(record: Record, fields: mutable.Buffer[String]): Unit = {
    render(new UncheckedWriterImpl(this.output), record, fields)
  }

}

