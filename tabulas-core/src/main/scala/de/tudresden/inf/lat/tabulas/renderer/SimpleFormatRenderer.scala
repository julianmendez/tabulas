
package de.tudresden.inf.lat.tabulas.renderer

import java.io.Writer

import de.tudresden.inf.lat.tabulas.parser.ParserConstant
import de.tudresden.inf.lat.tabulas.table.{Table, TableMap}

/** Renderer of a table in simple format.
  */
class SimpleFormatRenderer(output: Writer) extends Renderer {

  def renderAllRecords(output: UncheckedWriter, table: Table): Unit = {
    val recordRenderer = new SimpleFormatRecordRenderer(output.asWriter(), table.getPrefixMap)
    output.write(ParserConstant.NewLine)
    val list = table.getRecords
    list.foreach(record => {
      recordRenderer.renderNew(output)
      recordRenderer.render(output, record, table.getType.getFields)
      output.write(ParserConstant.NewLine)
    })
    output.write(ParserConstant.NewLine)
  }

  def render(output: UncheckedWriter, tableMap: TableMap): Unit = {
    output.write(SimpleFormatRenderer.Prefix)
    tableMap.getTableIds.foreach(tableName => {
      output.write(ParserConstant.NewLine)
      val table: Table = tableMap.getTable(tableName).get
      val record = MetadataHelper().getMetadataAsRecord(tableName, table)
      val recordRenderer = new SimpleFormatRecordRenderer(output.asWriter(), table.getPrefixMap)
      recordRenderer.render(output, record, MetadataHelper.MetadataTokens)
      renderAllRecords(output, table)
    })
    output.flush()
  }

  override def render(tableMap: TableMap): Unit = {
    render(new UncheckedWriterImpl(this.output), tableMap)
  }

}

object SimpleFormatRenderer {

  val Prefix: String = "" +
    ParserConstant.CommentSymbol + " simple format 1.0.0" + ParserConstant.NewLine

  def apply(output: Writer): SimpleFormatRenderer = new SimpleFormatRenderer(output)

}
