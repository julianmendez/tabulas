
package de.tudresden.inf.lat.tabulas.ext.parser

import java.io.BufferedReader
import java.io.IOException
import java.io.InputStreamReader
import java.io.Reader
import java.util.ArrayList
import java.util.List

import scala.Range
import scala.collection.JavaConversions.asScalaBuffer

import de.tudresden.inf.lat.tabulas.datatype.CompositeTypeImpl
import de.tudresden.inf.lat.tabulas.datatype.ParseException
import de.tudresden.inf.lat.tabulas.datatype.StringValue
import de.tudresden.inf.lat.tabulas.parser.Parser
import de.tudresden.inf.lat.tabulas.parser.ParserConstant
import de.tudresden.inf.lat.tabulas.table.RecordImpl
import de.tudresden.inf.lat.tabulas.table.TableImpl
import de.tudresden.inf.lat.tabulas.table.TableMap
import de.tudresden.inf.lat.tabulas.table.TableMapImpl

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

  private var input: Reader = new InputStreamReader(System.in)

  /**
   * Constructs a new parser.
   *
   * @param input0
   *            input
   */
  def this(input0: Reader) = {
    this()
    this.input = input0
  }

  def getColumns(line0: String): List[String] = {
    val ret: List[String] = new ArrayList[String]()
    val line: String = if (line0 == null) { "" } else { line0.trim() }
    var current: StringBuffer = new StringBuffer()
    var betweenQuotes: Boolean = false
    for (index <- 0 to (line.length() - 1)) {
      var ch: Char = line.charAt(index)
      if (ch == QuotesChar) {
        betweenQuotes = !betweenQuotes
      } else if ((ch == CommaChar) && !betweenQuotes) {
        ret.add(current.toString())
        current = new StringBuffer()
      } else {
        current.append(ch)
      }
    }
    if (!current.toString().isEmpty()) {
      ret.add(current.toString())
    }
    ret
  }

  private def createSortedTable(fields: List[String]): TableImpl = {
    var tableType: CompositeTypeImpl = new CompositeTypeImpl()
    fields.foreach(fieldName => tableType.declareField(fieldName, DefaultFieldType))

    val ret = new TableImpl()
    ret.setType(tableType)
    ret
  }

  def normalize(fieldName: String): String = {
    var auxName: String = if (fieldName == null) { Underscore } else { fieldName.trim() }
    val name = if (auxName.isEmpty()) { Underscore } else { auxName }

    val ret: StringBuffer = new StringBuffer()
    Range(0, name.length()).foreach(index => {
      var ch: Char = name.charAt(index)
      if (!Character.isLetterOrDigit(ch)) {
        ch = UnderscoreChar
      }
      ret.append(ch)
    })
    ret.toString()
  }

  def normalizeHeaders(headers: List[String], lineCounter: Int): List[String] = {
    val ret: List[String] = new ArrayList[String]()
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
        ret.add(fieldName)
      }
    }
    ret
  }

  def parseMap(input: BufferedReader): TableMap = {
    var lineCounter: Int = 0
    var line: String = input.readLine()
    lineCounter += 1
    val headers: List[String] = getColumns(line)
    val fieldNames: List[String] = normalizeHeaders(headers, lineCounter)
    val currentTable: TableImpl = createSortedTable(fieldNames)

    while (line != null) {
      line = input.readLine()
      lineCounter += 1
      if ((line != null) && !line.trim().isEmpty()) {
        val columns: List[String] = getColumns(line)
        if (columns.size() > fieldNames.size()) {
          throw new ParseException("Too many fields in line: "
            + columns.size() + " instead of "
            + fieldNames.size() + " (line " + lineCounter
            + ")");
        }

        val record: RecordImpl = new RecordImpl()
        var currentId: String = null
        var index: Int = 0
        for (column: String <- columns) {
          var field: String = fieldNames.get(index)
          if (field.equals(ParserConstant.IdKeyword)) {
            currentId = column
          }
          var value: StringValue = new StringValue(column)
          record.set(field, value)
          index += 1
        }

        currentTable.add(record)
        if (currentId != null) {
          currentTable.addId(currentId)
        }
      }
    }

    val ret: TableMapImpl = new TableMapImpl()
    ret.put(DefaultTableName, currentTable)
    ret
  }

  override def parse(): TableMap = {
    try {
      parseMap(new BufferedReader(this.input))

    } catch {
      case e: IOException => {
        throw new RuntimeException(e)
      }
    }
  }

}
