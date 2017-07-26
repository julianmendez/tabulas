
package de.tudresden.inf.lat.tabulas.ext.parser

import java.io.{BufferedReader, IOException, InputStreamReader, Reader}
import java.util.Objects

import de.tudresden.inf.lat.tabulas.datatype.{CompositeTypeImpl, ParseException, StringValue}
import de.tudresden.inf.lat.tabulas.parser.{Parser, ParserConstant}
import de.tudresden.inf.lat.tabulas.table.{RecordImpl, TableImpl, TableMap, TableMapImpl}

import scala.collection.mutable.{ArrayBuffer, Buffer}

/**
  * Parser of a table in comma-separated values format.
  *
  */
class CsvParser extends Parser {

  val UnderscoreChar: Char = '_'
  val CommaChar: Char = ','
  val QuotesChar: Char = '"'

  val DefaultTableName: String = "defaultType"
  val DefaultFieldType: String = "String"
  val Underscore: String = "" + UnderscoreChar

  private var _input: Reader = new InputStreamReader(System.in)

  /**
    * Constructs a new parser.
    *
    * @param input
    * input
    */
  def this(input: Reader) = {
    this()
    this._input = input
  }

  def getColumns(line0: String): Buffer[String] = {
    val result: Buffer[String] = new ArrayBuffer[String]()
    val line: String = if (Objects.isNull(line0)) {
      ""
    } else {
      line0.trim()
    }
    var current: StringBuffer = new StringBuffer()
    var betweenQuotes: Boolean = false
    for (index <- 0 to (line.length() - 1)) {
      var ch: Char = line.charAt(index)
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

    return result
  }

  private def createSortedTable(fields: Buffer[String]): TableImpl = {
    var tableType: CompositeTypeImpl = new CompositeTypeImpl()
    fields.foreach(fieldName => tableType.declareField(fieldName, DefaultFieldType))

    val result: TableImpl = new TableImpl()
    result.setType(tableType)

    return result
  }

  def normalize(fieldName: String): String = {
    var auxName: String = if (Objects.isNull(fieldName)) {
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
      var ch: Char = name.charAt(index)
      if (!Character.isLetterOrDigit(ch)) {
        ch = UnderscoreChar
      }
      ret.append(ch)
    })
    val result = ret.toString

    return result
  }

  def normalizeHeaders(headers: Buffer[String], lineCounter: Int): Buffer[String] = {
    val result: Buffer[String] = new ArrayBuffer[String]()
    var idCount: Int = 0
    for (header: String <- headers) {
      val fieldName: String = normalize(header)
      if (fieldName.equals(ParserConstant.IdKeyword)) {
        idCount += 1
      }
      if (idCount > 1) {
        throw new ParseException(
          "This cannot have two identifiers (field '"
            + ParserConstant.IdKeyword + "') (line "
            + lineCounter + ")")
      } else {
        result += fieldName
      }
    }

    return result
  }

  def parseMap(input: BufferedReader): TableMap = {
    var lineCounter: Int = 0
    var line: String = input.readLine()
    lineCounter += 1
    val headers: Buffer[String] = getColumns(line)
    val fieldNames: Buffer[String] = normalizeHeaders(headers, lineCounter)
    val currentTable: TableImpl = createSortedTable(fieldNames)

    while (Objects.nonNull(line)) {
      line = input.readLine()
      lineCounter += 1
      if (Objects.nonNull(line) && !line.trim().isEmpty) {
        val columns: Buffer[String] = getColumns(line)
        if (columns.size > fieldNames.size) {
          throw new ParseException("Too many fields in line: "
            + columns.size + " instead of "
            + fieldNames.size + " (line " + lineCounter
            + ")")
        }

        val record: RecordImpl = new RecordImpl()
        var index: Int = 0
        for (column: String <- columns) {
          var field: String = fieldNames(index)
          var value: StringValue = new StringValue(column)
          record.set(field, value)
          index += 1
        }

        currentTable.add(record)
      }
    }

    val result: TableMapImpl = new TableMapImpl()
    result.put(DefaultTableName, currentTable)

    return result
  }

  override def parse(): TableMap = {
    var result: TableMap = null
    try {
      result = parseMap(new BufferedReader(this._input))

    } catch {
      case e: IOException => {
        throw new RuntimeException(e)
      }
    }

    return result
  }

}
