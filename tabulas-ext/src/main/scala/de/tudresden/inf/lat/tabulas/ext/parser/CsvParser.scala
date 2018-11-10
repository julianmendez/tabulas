
package de.tudresden.inf.lat.tabulas.ext.parser

import java.io.{BufferedReader, IOException, InputStreamReader, Reader}
import java.util.Objects

import de.tudresden.inf.lat.tabulas.datatype.{CompositeTypeImpl, ParseException, StringValue}
import de.tudresden.inf.lat.tabulas.parser.{Parser, ParserConstant}
import de.tudresden.inf.lat.tabulas.table.{RecordImpl, TableImpl, TableMap, TableMapImpl}

import scala.collection.mutable

/** Parser of a table in comma-separated values format.
  *
  */
class CsvParser(input: Reader) extends Parser {

  val UnderscoreChar: Char = '_'
  val CommaChar: Char = ','
  val QuotesChar: Char = '"'

  val DefaultTableName: String = "defaultType"
  val DefaultFieldType: String = "String"
  val Underscore: String = "" + UnderscoreChar

  def getColumns(line0: String): Seq[String] = {
    val result = new mutable.ArrayBuffer[String]()
    val line: String = if (Objects.isNull(line0)) {
      ""
    } else {
      line0.trim()
    }
    var current: StringBuffer = new StringBuffer()
    var betweenQuotes: Boolean = false
    for (index <- 0 until line.length()) {
      val ch: Char = line.charAt(index)
      if (ch == QuotesChar) {
        betweenQuotes = !betweenQuotes
      } else if ((ch == CommaChar) && !betweenQuotes) {
        result += current.toString
        current = new StringBuffer()
      } else {
        current.append(ch)
      }
    }
    if (!current.toString.isEmpty) {
      result += current.toString
    }

    result
  }

  private def createSortedTable(fields: Seq[String]): TableImpl = {
    val tableType = fields
      .foldLeft(CompositeTypeImpl())((compType, field) => compType.declareField(field, DefaultFieldType).get)

    val result: TableImpl = new TableImpl(tableType)
    result
  }

  def normalize(fieldName: String): String = {
    val auxName: String = if (Objects.isNull(fieldName)) {
      Underscore
    } else {
      fieldName.trim()
    }
    val name = if (auxName.isEmpty) {
      Underscore
    } else {
      auxName
    }

    val ret: StringBuffer = new StringBuffer()
    Range(0, name.length()).foreach(index => {
      val ch: Char = name.charAt(index)
      val ch2 = if (Character.isLetterOrDigit(ch)) {
        ch
      } else {
        UnderscoreChar
      }
      ret.append(ch2)
    })
    val result = ret.toString
    result
  }

  def normalizeHeaders(headers: Seq[String], lineCounter: Int): Seq[String] = {
    val result = new mutable.ArrayBuffer[String]()
    var idCount: Int = 0
    for (header: String <- headers) {
      val fieldName: String = normalize(header)
      if (fieldName.equals(ParserConstant.IdKeyword)) {
        idCount += 1
      }
      if (idCount > 1) {
        throw ParseException(
          "This cannot have two identifiers (field '"
            + ParserConstant.IdKeyword + "') (line "
            + lineCounter + ")")
      } else {
        result += fieldName
      }
    }
    result
  }

  def parseMap(input: BufferedReader): TableMap = {
    var lineCounter: Int = 0
    var line: String = input.readLine()
    lineCounter += 1
    val headers: Seq[String] = getColumns(line)
    val fieldNames: Seq[String] = normalizeHeaders(headers, lineCounter)
    val currentTable: TableImpl = createSortedTable(fieldNames)

    while (Objects.nonNull(line)) {
      line = input.readLine()
      lineCounter += 1
      if (Objects.nonNull(line) && !line.trim().isEmpty) {
        val columns: Seq[String] = getColumns(line)
        if (columns.size > fieldNames.size) {
          throw ParseException("Too many fields in line: "
            + columns.size + " instead of "
            + fieldNames.size + " (line " + lineCounter
            + ")")
        }

        val record: RecordImpl = RecordImpl()
        var index: Int = 0
        for (column: String <- columns) {
          val field: String = fieldNames(index)
          val value: StringValue = new StringValue(column)
          record.set(field, value)
          index += 1
        }

        currentTable.add(record)
      }
    }

    val result: TableMapImpl = TableMapImpl()
    result.put(DefaultTableName, currentTable)
    result
  }

  override def parse(): TableMap = {
    val result: TableMap = try {
      parseMap(new BufferedReader(this.input))

    } catch {
      case e: IOException => throw new RuntimeException(e)
    }
    result
  }

}

object CsvParser {

  def apply(): CsvParser = new CsvParser(new InputStreamReader(System.in))

  def apply(input: Reader): CsvParser = new CsvParser(input)

}
