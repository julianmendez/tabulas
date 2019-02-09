
package de.tudresden.inf.lat.tabulas.renderer

import java.io.Writer
import java.net.URI
import java.util.Objects

import de.tudresden.inf.lat.tabulas.datatype.{ParameterizedListValue, PrimitiveTypeValue, Record, URIType}
import de.tudresden.inf.lat.tabulas.parser.ParserConstant
import de.tudresden.inf.lat.tabulas.table.PrefixMap

/** Renderer of a table in simple format.
  */
class SimpleFormatRecordRenderer(output: Writer, prefixMap: PrefixMap, fieldSign: String) extends RecordRenderer {

  def writeIfNotEmpty(output: UncheckedWriter, field: String, value: PrimitiveTypeValue): Boolean = {
    val result: Boolean = if (Objects.nonNull(field) && !field.trim().isEmpty && Objects.nonNull(value) && !value.isEmpty) {
      output.write(ParserConstant.NewLine)
      output.write(ParserConstant.Space)
      output.write(field)
      output.write(ParserConstant.Space + fieldSign)
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
          output.write(ParserConstant.Space)
          if (hasUris) {
            output.write(prefixMap.getWithPrefix(URI.create(elem)).toASCIIString)
          } else {
            output.write(elem.toString)
          }
        })

      } else {
        output.write(ParserConstant.Space)
        if (value.getType.equals(new URIType())) {
          output.write(prefixMap.getWithPrefix(URI.create(value.toString)).toASCIIString)
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

  def renderNew(output: UncheckedWriter): Unit = {
    output.write(ParserConstant.NewLine)
    output.write(ParserConstant.NewRecordToken + ParserConstant.Space)
    output.write(fieldSign + ParserConstant.Space)
  }

  def render(output: UncheckedWriter, record: Record, fields: Seq[String]): Unit = {
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
    render(new UncheckedWriterImpl(output), record, fields)
  }

}

object SimpleFormatRecordRenderer {

  def apply(output: Writer, prefixMap: PrefixMap): SimpleFormatRecordRenderer = new SimpleFormatRecordRenderer(output, prefixMap, ParserConstant.ColonFieldSign)

  def apply(output: Writer, prefixMap: PrefixMap, fieldSign: String): SimpleFormatRecordRenderer = new SimpleFormatRecordRenderer(output, prefixMap, fieldSign)

}
