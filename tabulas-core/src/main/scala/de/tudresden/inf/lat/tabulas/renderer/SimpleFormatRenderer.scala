
package de.tudresden.inf.lat.tabulas.renderer

import java.io.Writer

import de.tudresden.inf.lat.tabulas.parser.ParserConstant
import de.tudresden.inf.lat.tabulas.table.{Table, TableMap}

/** Renderer of a table in simple format.
  */
case class SimpleFormatRenderer(fieldSign: String, withMetadata: Boolean) extends Renderer {

  override def render(output: Writer, tableMap: TableMap): Unit = {
    output.write(SimpleFormatRenderer.Header)
    tableMap.getTableIds.foreach(typeName => {
      output.write(ParserConstant.NewLine)
      output.write(ParserConstant.NewLine)
      val table = tableMap.getTable(typeName).get
      val recordRenderer = SimpleFormatRecordRenderer(table.getPrefixMap, fieldSign)
      renderMetadataIfNecessary(output, typeName, table, recordRenderer)
      renderAllRecords(recordRenderer, output, table)
    })
    output.flush()
  }

  def renderMetadataIfNecessary(output: Writer, typeName: String, table: Table, recordRenderer: RecordRenderer): Unit = {
    if (withMetadata) {
      output.write(ParserConstant.TypeSelectionToken + ParserConstant.Space + fieldSign)
      val record = MetadataHelper().getMetadataAsRecord(typeName, table)
      recordRenderer.render(output, record, SimpleFormatRenderer.MetadataTokens)

    }
  }

  def renderAllRecords(recordRenderer: SimpleFormatRecordRenderer, output: Writer, table: Table): Unit = {
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

  def apply(): SimpleFormatRenderer = new SimpleFormatRenderer(ParserConstant.ColonFieldSign, withMetadata = true)

  def apply(withMetadata: Boolean): SimpleFormatRenderer = new SimpleFormatRenderer(ParserConstant.ColonFieldSign, withMetadata)

  def apply(fieldSign: String): SimpleFormatRenderer = new SimpleFormatRenderer(fieldSign, withMetadata = true)

}
