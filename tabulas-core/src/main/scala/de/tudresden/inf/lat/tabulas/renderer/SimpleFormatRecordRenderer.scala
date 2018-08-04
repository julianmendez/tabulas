
package de.tudresden.inf.lat.tabulas.renderer

import java.io.{OutputStreamWriter, Writer}
import java.net.URI
import java.util.Objects

import de.tudresden.inf.lat.tabulas.datatype._
import de.tudresden.inf.lat.tabulas.parser.ParserConstant
import de.tudresden.inf.lat.tabulas.table.{PrefixMap, PrefixMapImpl}

/** Renderer of a table in simple format.
  */
class SimpleFormatRecordRenderer extends RecordRenderer {

  private var _output: Writer = new OutputStreamWriter(System.out)
  private var _prefixMap: PrefixMap = new PrefixMapImpl()

  def this(output: Writer, prefixMap: PrefixMap) = {
    this()
    Objects.requireNonNull(output)
    this._output = output
    this._prefixMap = prefixMap
  }

  def this(output: UncheckedWriter, prefixMap: PrefixMap) = {
    this()
    Objects.requireNonNull(output)
    this._output = output.asWriter()
    this._prefixMap = prefixMap
  }


  def writeIfNotEmpty(output: UncheckedWriter, field: String, value: PrimitiveTypeValue): Boolean = {
    val result: Boolean = if (Objects.nonNull(field) && !field.trim().isEmpty && Objects.nonNull(value) && !value.isEmpty) {
      output.write(ParserConstant.NewLine)
      output.write(field)
      output.write(ParserConstant.Space + ParserConstant.EqualsSign)
      if (value.getType.isList) {
        val hasUris: Boolean = value match {
          case list: ParameterizedListValue =>
            list.getParameter.equals(new URIType())
          case _ =>
            false
        }
        value.getType
        val list: Seq[String] = value.renderAsList()
        list.foreach(elem => {
          output.write(ParserConstant.Space + ParserConstant.LineContinuationSymbol)
          output.write(ParserConstant.NewLine)
          output.write(ParserConstant.Space)
          if (hasUris) {
            output.write(_prefixMap.getWithPrefix(URI.create(elem)).toASCIIString)
          } else {
            output.write(elem.toString)
          }
        })

      } else {
        output.write(ParserConstant.Space)
        if (value.getType.equals(new URIType())) {
          output.write(_prefixMap.getWithPrefix(URI.create(value.toString)).toASCIIString)
        } else {
          output.write(value.toString)
        }
      }
      true
    } else {
      false
    }
    result
  }

  def render(output: UncheckedWriter, record: Record, fields: Seq[String]): Unit = {
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

  override def render(record: Record, fields: Seq[String]): Unit = {
    render(new UncheckedWriterImpl(this._output), record, fields)
  }

}

object SimpleFormatRecordRenderer {

  def apply(): SimpleFormatRecordRenderer = new SimpleFormatRecordRenderer

}
