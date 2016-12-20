
package de.tudresden.inf.lat.tabulas.renderer

import java.io.OutputStreamWriter
import java.io.Writer
import java.util.List
import java.util.Objects
import java.util.Optional

import scala.collection.JavaConversions.asScalaBuffer

import de.tudresden.inf.lat.tabulas.datatype.PrimitiveTypeValue
import de.tudresden.inf.lat.tabulas.datatype.Record
import de.tudresden.inf.lat.tabulas.parser.ParserConstant

/**
 * Renderer of a table in simple format.
 */
class SimpleFormatRecordRenderer extends RecordRenderer {

  private var output: Writer = new OutputStreamWriter(System.out)

  def this(output: Writer) = {
    this()
    Objects.requireNonNull(output)
    this.output = output
  }

  def this(output: UncheckedWriter) = {
    this()
    Objects.requireNonNull(output)
    this.output = output.asWriter()
  }

  def writeIfNotEmpty(output: UncheckedWriter, field: String, value: PrimitiveTypeValue): Boolean = {
    if (Objects.nonNull(field) && !field.trim().isEmpty() && Objects.nonNull(value) && !value.isEmpty()) {
      output.write(ParserConstant.NewLine)
      output.write(field)
      output.write(ParserConstant.Space + ParserConstant.EqualsSign)
      if (value.getType().isList()) {
        val list: List[String] = value.renderAsList()
        list.foreach(link => {
          output.write(ParserConstant.Space + ParserConstant.LineContinuationSymbol)
          output.write(ParserConstant.NewLine)
          output.write(ParserConstant.Space)
          output.write(link.toString())
        })

      } else {
        output.write(ParserConstant.Space)
        output.write(value.toString())

      }
      return true
    } else {
      return false
    }
  }

  def render(output: UncheckedWriter, record: Record, fields: List[String]): Unit = {
    output.write(ParserConstant.NewLine)
    output.write(ParserConstant.NewRecordToken + ParserConstant.Space)
    output.write(ParserConstant.EqualsSign + ParserConstant.Space)

    fields.foreach(field => {
      val optValue: Optional[PrimitiveTypeValue] = record.get(field)
      if (optValue.isPresent()) {
        writeIfNotEmpty(output, field, optValue.get());
      }
    })

    output.write(ParserConstant.NewLine)
    output.flush()
  }

  override def render(record: Record, fields: List[String]): Unit = {
    render(new UncheckedWriterImpl(this.output), record, fields)
  }

}

