
package de.tudresden.inf.lat.tabulas.renderer

import java.io.{OutputStreamWriter, Writer}

import de.tudresden.inf.lat.tabulas.datatype.Record
import de.tudresden.inf.lat.tabulas.parser.ParserConstant
import de.tudresden.inf.lat.tabulas.table.{Table, TableMap}

/** Renderer of a table in simple format.
  */
class SimpleFormatRenderer extends Renderer {

  val Prefix: String = "" +
    ParserConstant.CommentSymbol + " simple format 1.0.0" + ParserConstant.NewLine

  private var _output: Writer = new OutputStreamWriter(System.out)

  def this(output: Writer) = {
    this()
    this._output = output
  }

  def renderAllRecords(output: UncheckedWriter, table: Table): Unit = {
    val recordRenderer: SimpleFormatRecordRenderer = new SimpleFormatRecordRenderer(output, table.getPrefixMap)
    output.write(ParserConstant.NewLine)
    val list: Seq[Record] = table.getRecords
    list.foreach(record => {
      recordRenderer.render(output, record, table.getType.getFields)
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

    table.getType.getFields.foreach(field => {
      output.write(ParserConstant.Space + ParserConstant.LineContinuationSymbol)
      output.write(ParserConstant.NewLine)
      output.write(ParserConstant.Space)
      output.write(field)
      output.write(ParserConstant.TypeSign)
      output.write(table.getType.getFieldType(field).get)
    })
    output.write(ParserConstant.NewLine)
  }

  def renderPrefixMapIfNecessary(output: UncheckedWriter, table: Table): Unit = {
    if (!table.getPrefixMap.isEmpty) {
      output.write(ParserConstant.NewLine + ParserConstant.NewLine)
      output.write(ParserConstant.PrefixMapToken + ParserConstant.Space)
      output.write(ParserConstant.EqualsSign)

      table.getPrefixMap.getKeysAsStream.foreach(prefix => {
        output.write(ParserConstant.Space + ParserConstant.LineContinuationSymbol)
        output.write(ParserConstant.NewLine)
        output.write(ParserConstant.Space)
        output.write(prefix.toASCIIString)
        output.write(ParserConstant.TypeSign)
        output.write(table.getPrefixMap.get(prefix).get.toASCIIString)
      })
      output.write(ParserConstant.NewLine)
    }
  }

  def renderOrderIfNecessary(output: UncheckedWriter, table: Table): Unit = {
    if (!table.getSortingOrder.isEmpty) {
      output.write(ParserConstant.NewLine + ParserConstant.NewLine)
      output.write(ParserConstant.SortingOrderDeclarationToken + ParserConstant.Space)
      output.write(ParserConstant.EqualsSign)

      table.getSortingOrder.foreach(field => {
        output.write(ParserConstant.Space + ParserConstant.LineContinuationSymbol)
        output.write(ParserConstant.NewLine)
        output.write(ParserConstant.Space)
        if (table.getFieldsWithReverseOrder.contains(field)) {
          output.write(ParserConstant.ReverseOrderSign)
        }
        output.write(field)
      })
      output.write(ParserConstant.NewLine)
    }
  }

  def render(output: UncheckedWriter, tableMap: TableMap): Unit = {
    output.write(Prefix)
    tableMap.getTableIds.foreach(tableName => {
      val table: Table = tableMap.getTable(tableName).get
      renderTypeSelection(output, tableName, table)
      renderTypeDefinition(output, table)
      renderPrefixMapIfNecessary(output, table)
      renderOrderIfNecessary(output, table)
      renderAllRecords(output, table)
    })
    output.flush()
  }

  override def render(tableMap: TableMap): Unit = {
    render(new UncheckedWriterImpl(this._output), tableMap)
  }

}

object SimpleFormatRenderer {

  def apply(): SimpleFormatRecordRenderer = new SimpleFormatRecordRenderer

}
