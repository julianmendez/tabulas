
package de.tudresden.inf.lat.tabulas.ext.parser

import java.io.{BufferedReader, Reader}
import java.util.Objects

import de.tudresden.inf.lat.tabulas.datatype.{CompositeTypeImpl, ParseException, StringValue}
import de.tudresden.inf.lat.tabulas.parser.{Parser, ParserConstant}
import de.tudresden.inf.lat.tabulas.table.{RecordImpl, TableImpl, TableMapImpl}

import scala.collection.mutable
import scala.util.Try

/** Parser of a table in comma-separated values format.
 *
 */
case class CsvParser() extends Parser {

  final val UnderscoreChar: Char = '_'
  final val CommaChar: Char = ','
  final val QuotesChar: Char = '"'

  final val DefaultTableName: String = "defaultType"
  final val DefaultFieldType: String = "String"
  final val Underscore: String = "" + UnderscoreChar

  override def parse(input: Reader): Try[TableMapImpl] = Try {
    parseMap(new BufferedReader(input))
  }

  def parseMap(input: BufferedReader): TableMapImpl = {
    var lineCounter = 0
    var line = input.readLine()
    lineCounter += 1
    val headers = getColumns(line)
    val fieldNames = normalizeHeaders(headers, lineCounter)
    val currentTable: TableImpl = createSortedTable(fieldNames)

    while (Objects.nonNull(line)) {
      line = input.readLine()
      lineCounter += 1
      if (Objects.nonNull(line) && !line.trim().isEmpty) {
        val columns = getColumns(line)
        if (columns.size > fieldNames.size) {
          throw ParseException("Too many fields in line: "
            + columns.size + " instead of "
            + fieldNames.size + " (line " + lineCounter
            + ")")
        }

        val record: RecordImpl = RecordImpl()
        var index = 0
        for (column: String <- columns) {
          val field = fieldNames(index)
          val value: StringValue = new StringValue(column)
          record.set(field, value)
          index += 1
        }

        currentTable.add(record)
      }
    }

    TableMapImpl(Map() ++ Seq((DefaultTableName, currentTable)))
  }

  def getColumns(line0: String): Seq[String] = {
    val columns = new mutable.ArrayBuffer[String]()
    val line = if (Objects.isNull(line0)) {
      ""
    } else {
      line0.trim()
    }
    var current = new StringBuffer()
    var betweenQuotes: Boolean = false
    for (index <- 0 until line.length()) {
      val ch: Char = line.charAt(index)
      if (ch == QuotesChar) {
        betweenQuotes = !betweenQuotes
      } else if ((ch == CommaChar) && !betweenQuotes) {
        columns += current.toString
        current = new StringBuffer()
      } else {
        current.append(ch)
      }
    }
    if (!current.toString.isEmpty) {
      columns += current.toString
    }
    columns.toSeq
  }

  private def createSortedTable(fields: Seq[String]): TableImpl = {
    val tableType = fields
      .foldLeft(CompositeTypeImpl())((compType, field) => compType.declareField(field, DefaultFieldType).get)

    TableImpl(tableType)
  }

  def normalizeHeaders(headers: Seq[String], lineCounter: Int): Seq[String] = {
    val headers = new mutable.ArrayBuffer[String]()
    var idCount = 0
    for (header: String <- headers) {
      val fieldName = normalize(header)
      if (fieldName.equals(ParserConstant.IdKeyword)) {
        idCount += 1
      }
      if (idCount > 1) {
        throw ParseException(
          "This cannot have two identifiers (field '"
            + ParserConstant.IdKeyword + "') (line "
            + lineCounter + ")")
      } else {
        headers += fieldName
      }
    }
    headers.toSeq
  }

  def normalize(fieldName: String): String = {
    val auxName = if (Objects.isNull(fieldName)) {
      Underscore
    } else {
      fieldName.trim()
    }
    val name = if (auxName.isEmpty) {
      Underscore
    } else {
      auxName
    }

    val result = Range(0, name.length())
      .map(index => {
        val ch: Char = name.charAt(index)
        val newChar = if (Character.isLetterOrDigit(ch)) {
          ch
        } else {
          UnderscoreChar
        }
        newChar
      }).mkString
    result
  }

}

object CsvParser {}
