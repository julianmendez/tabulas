
package de.tudresden.inf.lat.tabulas.renderer

import java.io.{OutputStreamWriter, Writer}

import de.tudresden.inf.lat.tabulas.datatype.Record
import de.tudresden.inf.lat.tabulas.parser.ParserConstant
import de.tudresden.inf.lat.tabulas.table.{Table, TableMap}

import scala.collection.mutable

/**
  * Renderer of a table in simple format.
  */
class SimpleFormatRenderer extends Renderer {

  val Prefix: String = "" +
    ParserConstant.CommentSymbol + " simple format 1.0.0" + ParserConstant.NewLine

  private var output: Writer = new OutputStreamWriter(System.out)

  def this(output: Writer) = {
    this()
    this.output = output
  }

  def renderAllRecords(output: UncheckedWriter, table: Table): Unit = {
    val recordRenderer: SimpleFormatRecordRenderer = new SimpleFormatRecordRenderer(output)
    output.write(ParserConstant.NewLine)
    val list: mutable.Buffer[Record] = table.getRecords()
    list.foreach(record => {
      recordRenderer.render(output, record, table.getType().getFields())
      output.write(ParserConstant.NewLine)
    })
    output.write(ParserConstant.NewLine)
  }

  def renderTypeSelection(output: UncheckedWriter, tableName: String, table: Table): Unit = {
    output.write(ParserConstant.NewLine + ParserConstant.NewLine)
    output.write(ParserConstant.TypeSelectionToken + ParserConstant.Space)
    output.write(ParserConstant.EqualsSign)
    output.write(ParserConstant.Space)
    output.write(tableName)
    output.write(ParserConstant.NewLine)
  }

  def renderTypeDefinition(output: UncheckedWriter, table: Table): Unit = {
    output.write(ParserConstant.NewLine + ParserConstant.NewLine)
    output.write(ParserConstant.TypeDefinitionToken + ParserConstant.Space)
    output.write(ParserConstant.EqualsSign)

    table.getType().getFields().foreach(field => {
      output.write(ParserConstant.Space + ParserConstant.LineContinuationSymbol)
      output.write(ParserConstant.NewLine)
      output.write(ParserConstant.Space)
      output.write(field)
      output.write(ParserConstant.TypeSign)
      output.write(table.getType().getFieldType(field).get)
    })
    output.write(ParserConstant.NewLine)
  }

  def renderOrder(output: UncheckedWriter, table: Table): Unit = {
    output.write(ParserConstant.NewLine + ParserConstant.NewLine)
    output.write(ParserConstant.SortingOrderDeclarationToken + ParserConstant.Space)
    output.write(ParserConstant.EqualsSign)

    table.getSortingOrder().foreach(field => {
      output.write(ParserConstant.Space + ParserConstant.LineContinuationSymbol)
      output.write(ParserConstant.NewLine)
      output.write(ParserConstant.Space)
      if (table.getFieldsWithReverseOrder().contains(field)) {
        output.write(ParserConstant.ReverseOrderSign)
      }
      output.write(field)
    })
    output.write(ParserConstant.NewLine)
  }

  def render(output: UncheckedWriter, tableMap: TableMap): Unit = {
    output.write(Prefix)
    tableMap.getTableIds().foreach(tableName => {
      val table: Table = tableMap.getTable(tableName)
      renderTypeSelection(output, tableName, table)
      renderTypeDefinition(output, table)
      renderOrder(output, table)
      renderAllRecords(output, table)
    })
    output.flush()
  }

  override def render(tableMap: TableMap): Unit = {
    render(new UncheckedWriterImpl(this.output), tableMap)
  }

}

