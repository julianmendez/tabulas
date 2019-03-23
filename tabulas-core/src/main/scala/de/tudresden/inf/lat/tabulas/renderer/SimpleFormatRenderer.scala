
package de.tudresden.inf.lat.tabulas.renderer

import java.io.Writer

import de.tudresden.inf.lat.tabulas.parser.ParserConstant
import de.tudresden.inf.lat.tabulas.table.{Table, TableMap}

/** Renderer of a table in simple format.
  */
case class SimpleFormatRenderer(output: Writer, fieldSign: String) extends Renderer {

  override def render(tableMap: TableMap): Unit = {
    render(UncheckedWriterImpl(output), tableMap)
  }

  def render(output: UncheckedWriter, tableMap: TableMap): Unit = {
    output.write(SimpleFormatRenderer.Header)
    tableMap.getTableIds.foreach(tableName => {
      output.write(ParserConstant.NewLine)
      output.write(ParserConstant.NewLine)
      output.write(ParserConstant.TypeSelectionToken + ParserConstant.Space + fieldSign)
      val table: Table = tableMap.getTable(tableName).get
      val record = MetadataHelper().getMetadataAsRecord(tableName, table)
      val recordRenderer = SimpleFormatRecordRenderer(output.asWriter(), table.getPrefixMap, fieldSign)
      recordRenderer.render(output, record, SimpleFormatRenderer.MetadataTokens)
      renderAllRecords(recordRenderer, output, table)
    })
    output.flush()
  }

  def renderAllRecords(recordRenderer: SimpleFormatRecordRenderer, output: UncheckedWriter, table: Table): Unit = {
    output.write(ParserConstant.NewLine)
    val list = table.getRecords
    list.foreach(record => {
      recordRenderer.renderNew(output)
      recordRenderer.render(output, record, table.getType.getFields)
      output.write(ParserConstant.NewLine)
    })
    output.write(ParserConstant.NewLine)
  }

}

object SimpleFormatRenderer {

  final val MetadataTokens = Seq(
    ParserConstant.TypeNameToken,
    ParserConstant.TypeDefinitionToken,
    ParserConstant.PrefixMapToken,
    ParserConstant.SortingOrderDeclarationToken
  )

  val Header: String = "" +
    ParserConstant.CommentSymbol + " " + ParserConstant.SpecificationFormat + " " +
    ParserConstant.SpecificationVersion + ParserConstant.NewLine

  def apply(output: Writer): SimpleFormatRenderer = new SimpleFormatRenderer(output, ParserConstant.ColonFieldSign)

}
