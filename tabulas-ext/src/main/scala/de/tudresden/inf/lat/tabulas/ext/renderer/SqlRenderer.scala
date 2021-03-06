
package de.tudresden.inf.lat.tabulas.ext.renderer

import java.io.Writer
import java.util.Objects

import de.tudresden.inf.lat.tabulas.datatype._
import de.tudresden.inf.lat.tabulas.parser.ParserConstant
import de.tudresden.inf.lat.tabulas.renderer.Renderer
import de.tudresden.inf.lat.tabulas.table.{Table, TableMap}

/** Renderer of tables in SQL format.
 */
case class SqlRenderer() extends Renderer {

  final val DefaultSize: Int = 0x800
  final val DefaultDatabaseName: String = "tabula"
  final val CreateDatabase: String = "create database"
  final val Use: String = "use"
  final val CreateTable: String = "create table"
  final val LeftPar: String = "("
  final val RightPar: String = ")"
  final val DefaultFieldType: String = "varchar(" + DefaultSize + ")"
  final val Comma: String = ","
  final val Semicolon: String = ";"
  final val Values: String = "values"
  final val Null: String = "null"
  final val Apostrophe: String = "'"
  final val InsertInto: String = "insert into"
  final val ApostropheReplacement: String = "%27"
  final val Asc: String = "asc"
  final val Desc: String = "desc"
  final val SelectAllFrom: String = "select * from"
  final val OrderBy: String = "order by"

  override def render(output: Writer, tableMap: TableMap): Unit = {
    renderPrefix(output)

    tableMap.getTableIds.foreach(tableId => {
      renderTable(output, tableId, tableMap.getTable(tableId).get)
    })
  }

  def renderTable(output: Writer, tableId: String, table: Table): Unit = {
    renderTypes(output, tableId, table)
    renderAllRecords(output, tableId, table)
    renderOrder(output, tableId, table)
    output.flush()
  }

  def renderAllRecords(output: Writer, tableId: String, table: CompositeTypeValue): Unit = {
    val list: Seq[Record] = table.getRecords
    output.write(ParserConstant.NewLine)
    list.foreach(record => {
      render(output, tableId, record, table.getType.getFields)
      output.write(ParserConstant.NewLine)
    })
    output.write(ParserConstant.NewLine)
  }

  def render(output: Writer, tableId: String, record: Record, fields: Seq[String]): Unit = {
    output.write(ParserConstant.NewLine)
    output.write(InsertInto)
    output.write(ParserConstant.Space)
    output.write(tableId)
    output.write(ParserConstant.Space)
    output.write(Values)
    output.write(ParserConstant.Space)
    output.write(LeftPar)
    output.write(ParserConstant.Space)

    var first: Boolean = true
    for (field: String <- fields) {
      if (first) {
        first = false
      } else {
        output.write(Comma)
      }
      output.write(ParserConstant.NewLine)
      val optValue: Option[PrimitiveTypeValue] = record.get(field)
      if (optValue.isDefined) {
        val value: PrimitiveTypeValue = optValue.get
        value match {
          case list: ParameterizedListValue =>
            writeParameterizedListIfNotEmpty(output, field, list)
          case link: URIValue =>
            writeLinkIfNotEmpty(output, field, link)
          case _ =>
            writeAsStringIfNotEmpty(output, field, value)
        }

      } else {
        output.write(Null)
      }
    }
    output.write(ParserConstant.NewLine)
    output.write(RightPar)
    output.write(Semicolon)
  }

  def writeAsStringIfNotEmpty(output: Writer, field: String, value: PrimitiveTypeValue): Boolean = {
    val result = if (Objects.nonNull(field) && !field.trim().isEmpty && Objects.nonNull(value)
      && !value.toString.trim().isEmpty) {
      output.write(Apostrophe)
      output.write(sanitize(value.toString))
      output.write(Apostrophe)
      true
    } else {
      output.write(Null)
      false
    }
    result
  }

  def sanitize(str: String): String = {
    str.replace(Apostrophe, ApostropheReplacement)
  }

  def writeParameterizedListIfNotEmpty(output: Writer, field: String, list: ParameterizedListValue): Boolean = {
    val result = if (Objects.nonNull(list) && !list.isEmpty) {
      output.write(Apostrophe)
      list.getList.foreach(value => {
        output.write(sanitize(value.toString))
        output.write(ParserConstant.Space)
      })
      output.write(Apostrophe)
      true
    } else {
      output.write(Null)
      false
    }
    result
  }

  def writeLinkIfNotEmpty(output: Writer, prefix: String, link: URIValue): Boolean = {
    val result = if (Objects.nonNull(link) && !link.isEmpty) {
      output.write(prefix)
      output.write(Apostrophe)
      output.write(sanitize(link.toString))
      output.write(Apostrophe)
      true
    } else {
      output.write(Null)
      false
    }
    result
  }

  def renderTypes(output: Writer, tableName: String, table: CompositeTypeValue): Unit = {
    output.write(ParserConstant.NewLine + ParserConstant.NewLine)
    output.write(CreateTable + ParserConstant.Space)
    output.write(tableName + ParserConstant.Space)
    output.write(LeftPar)
    output.write(ParserConstant.NewLine)
    var first: Boolean = true
    for (field: String <- table.getType.getFields) {
      if (first) {
        first = false
      } else {
        output.write(Comma)
        output.write(ParserConstant.NewLine)
      }
      output.write(field)
      output.write(ParserConstant.Space)
      output.write(DefaultFieldType)
    }
    output.write(ParserConstant.NewLine)
    output.write(RightPar)
    output.write(Semicolon)
    output.write(ParserConstant.NewLine)
    output.write(ParserConstant.NewLine)
  }

  def renderOrder(output: Writer, tableName: String, table: Table): Unit = {
    output.write(ParserConstant.NewLine)
    output.write(SelectAllFrom)
    output.write(ParserConstant.Space)
    output.write(tableName)
    output.write(ParserConstant.NewLine)
    output.write(OrderBy)
    output.write(ParserConstant.Space)

    var first: Boolean = true
    for (field: String <- table.getSortingOrder) {
      if (first) {
        first = false
      } else {
        output.write(Comma)
        output.write(ParserConstant.Space)
      }
      output.write(field)
      output.write(ParserConstant.Space)
      if (table.getFieldsWithReverseOrder.contains(field)) {
        output.write(Desc)
      } else {
        output.write(Asc)
      }
    }
    output.write(Semicolon)
    output.write(ParserConstant.NewLine)
    output.write(ParserConstant.NewLine)
  }

  def renderPrefix(output: Writer): Unit = {
    output.write(ParserConstant.NewLine)
    output.write(CreateDatabase + ParserConstant.Space + DefaultDatabaseName + Semicolon)
    output.write(ParserConstant.NewLine)
    output.write(ParserConstant.NewLine)
    output.write(Use + ParserConstant.Space + DefaultDatabaseName + Semicolon)
    output.write(ParserConstant.NewLine)
    output.write(ParserConstant.NewLine)
  }

}

object SqlRenderer {}
