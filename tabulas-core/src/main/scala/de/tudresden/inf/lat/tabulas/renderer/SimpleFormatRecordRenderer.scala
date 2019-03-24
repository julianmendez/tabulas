
package de.tudresden.inf.lat.tabulas.renderer

import java.io.Writer
import java.net.URI
import java.util.Objects

import de.tudresden.inf.lat.tabulas.datatype.{ParameterizedListValue, PrimitiveTypeValue, Record, URIType}
import de.tudresden.inf.lat.tabulas.parser.ParserConstant
import de.tudresden.inf.lat.tabulas.table.PrefixMap

/** Renderer of a table in simple format.
  */
case class SimpleFormatRecordRenderer(output: Writer, prefixMap: PrefixMap, fieldSign: String) extends RecordRenderer {

  def renderNew(output: Writer): Unit = {
    output.write(ParserConstant.NewLine)
    output.write(ParserConstant.NewRecordToken + ParserConstant.Space)
    output.write(fieldSign)
  }

  override def render(record: Record, fields: Seq[String]): Unit = {
    render(output, record, fields)
  }

  def render(output: Writer, record: Record, fields: Seq[String]): Unit = {
    fields.foreach(field => {
      val optValue: Option[PrimitiveTypeValue] = record.get(field)
      if (optValue.isDefined) {
        writeIfNotEmpty(output, field, optValue.get)
      }
    })

    output.write(ParserConstant.NewLine)
    output.flush()
  }

  def writeIfNotEmpty(output: Writer, field: String, value: PrimitiveTypeValue): Boolean = {
    val result: Boolean = if (Objects.nonNull(field) && !field.trim().isEmpty && Objects.nonNull(value) && !value.isEmpty) {
      output.write(ParserConstant.NewLine)
      output.write(ParserConstant.Space)
      output.write(field)
      output.write(ParserConstant.Space + fieldSign)
      if (value.getType.isList) {
        val hasUris: Boolean = value match {
          case list: ParameterizedListValue =>
            list.getParameter.equals(URIType())
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
            output.write(prefixMap.getWithPrefix(URI.create(elem)).toString)
          } else {
            output.write(elem.toString)
          }
        })

      } else {
        output.write(ParserConstant.Space)
        if (value.getType.equals(URIType())) {
          output.write(prefixMap.getWithPrefix(URI.create(value.toString)).toString)
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

}

object SimpleFormatRecordRenderer {

  def apply(output: Writer, prefixMap: PrefixMap): SimpleFormatRecordRenderer = new SimpleFormatRecordRenderer(output, prefixMap, ParserConstant.ColonFieldSign)

}
