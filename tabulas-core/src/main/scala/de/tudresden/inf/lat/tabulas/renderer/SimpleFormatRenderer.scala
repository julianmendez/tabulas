
package de.tudresden.inf.lat.tabulas.renderer

import java.io.IOException
import java.io.OutputStreamWriter
import java.io.Writer
import java.util.List

import scala.collection.JavaConversions.asScalaBuffer

import de.tudresden.inf.lat.tabulas.datatype.PrimitiveTypeValue
import de.tudresden.inf.lat.tabulas.datatype.Record
import de.tudresden.inf.lat.tabulas.parser.ParserConstant
import de.tudresden.inf.lat.tabulas.table.Table
import de.tudresden.inf.lat.tabulas.table.TableMap

/**
 * Renderer of a table in simple format.
 */
class SimpleFormatRenderer extends Renderer {

  val Prefix: String = "" +
    ParserConstant.CommentSymbol + " simple format 1.0.0" + ParserConstant.NewLine

  private var output: Writer = new OutputStreamWriter(System.out)

  def this(output0: Writer) = {
    this()
    output = output0
  }

  def writeIfNotEmpty(output: Writer, field: String,
    value: PrimitiveTypeValue): Boolean = {
    if (field != null && !field.trim().isEmpty() && value != null
      && !value.isEmpty()) {
      output.write(ParserConstant.NewLine)
      output.write(field)
      output.write(ParserConstant.Space + ParserConstant.EqualsSign)
      if (value.getType().isList()) {
        val list: List[String] = value.renderAsList()
        for (link: String <- list) {
          output.write(ParserConstant.Space
            + ParserConstant.LineContinuationSymbol)
          output.write(ParserConstant.NewLine)
          output.write(ParserConstant.Space)
          output.write(link.toString())
        }

      } else {
        output.write(ParserConstant.Space)
        output.write(value.toString())

      }
      true
    } else {
      false
    }
  }

  def render(output: Writer, record: Record, fields: List[String]): Unit = {

    output.write(ParserConstant.NewLine + ParserConstant.NewLine)
    output.write(ParserConstant.NewRecordToken + ParserConstant.Space)
    output.write(ParserConstant.EqualsSign + ParserConstant.Space)

    for (field: String <- fields) {
      val value: PrimitiveTypeValue = record.get(field)
      if (value != null) {
        writeIfNotEmpty(output, field, value);
      }
    }
  }

  def renderAllRecords(output: Writer, table: Table): Unit = {
    val list: List[Record] = table.getRecords()
    for (record: Record <- list) {
      render(output, record, table.getType().getFields())
      output.write(ParserConstant.NewLine)
    }
    output.write(ParserConstant.NewLine)
  }

  def renderTypeSelection(output: Writer, tableName: String, table: Table): Unit = {
    output.write(ParserConstant.NewLine + ParserConstant.NewLine)
    output.write(ParserConstant.TypeSelectionToken + ParserConstant.Space)
    output.write(ParserConstant.EqualsSign)
    output.write(ParserConstant.Space)
    output.write(tableName + ParserConstant.Space)
    output.write(ParserConstant.NewLine)
  }

  def renderTypeDefinition(output: Writer, table: Table): Unit = {
    output.write(ParserConstant.NewLine + ParserConstant.NewLine)
    output.write(ParserConstant.TypeDefinitionToken + ParserConstant.Space)
    output.write(ParserConstant.EqualsSign)

    for (field: String <- table.getType().getFields()) {
      output.write(ParserConstant.Space
        + ParserConstant.LineContinuationSymbol)
      output.write(ParserConstant.NewLine)
      output.write(ParserConstant.Space)
      output.write(field)
      output.write(ParserConstant.TypeSign)
      output.write(table.getType().getFieldType(field))
    }
    output.write(ParserConstant.NewLine)
  }

  def renderOrder(output: Writer, table: Table): Unit = {
    output.write(ParserConstant.NewLine + ParserConstant.NewLine)
    output.write(ParserConstant.SortingOrderDeclarationToken + ParserConstant.Space)
    output.write(ParserConstant.EqualsSign)

    for (field: String <- table.getSortingOrder()) {
      output.write(ParserConstant.Space + ParserConstant.LineContinuationSymbol)
      output.write(ParserConstant.NewLine)
      output.write(ParserConstant.Space)
      if (table.getFieldsWithReverseOrder().contains(field)) {
        output.write(ParserConstant.ReverseOrderSign)
      }
      output.write(field)
    }
    output.write(ParserConstant.NewLine)
  }

  def render(output: Writer, tableMap: TableMap): Unit = {
    output.write(Prefix)
    for (tableName: String <- tableMap.getTableIds()) {
      val table: Table = tableMap.getTable(tableName)
      renderTypeSelection(output, tableName, table)
      renderTypeDefinition(output, table)
      renderOrder(output, table)
      renderAllRecords(output, table)
    }
    output.write(ParserConstant.NewLine)
    output.flush()
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

