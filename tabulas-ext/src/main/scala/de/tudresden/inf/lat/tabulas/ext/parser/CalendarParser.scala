package de.tudresden.inf.lat.tabulas.ext.parser

import java.io.BufferedReader
import java.io.IOException
import java.io.InputStreamReader
import java.io.Reader
import java.util.ArrayList
import java.util.List
import java.util.Stack
import java.util.TreeMap

import scala.collection.JavaConversions.asScalaBuffer
import scala.collection.JavaConversions.asScalaSet

import de.tudresden.inf.lat.tabulas.datatype.CompositeType
import de.tudresden.inf.lat.tabulas.datatype.ParseException
import de.tudresden.inf.lat.tabulas.datatype.PrimitiveTypeFactory
import de.tudresden.inf.lat.tabulas.datatype.PrimitiveTypeValue
import de.tudresden.inf.lat.tabulas.datatype.Record
import de.tudresden.inf.lat.tabulas.datatype.SimplifiedCompositeType
import de.tudresden.inf.lat.tabulas.datatype.StringValue
import de.tudresden.inf.lat.tabulas.parser.Parser
import de.tudresden.inf.lat.tabulas.parser.ParserConstant
import de.tudresden.inf.lat.tabulas.table.RecordImpl
import de.tudresden.inf.lat.tabulas.table.TableImpl
import de.tudresden.inf.lat.tabulas.table.TableMap
import de.tudresden.inf.lat.tabulas.table.TableMapImpl

/**
 * Parser of a calendar.
 *
 */
class CalendarParser extends Parser {

  class Pair(lineCounter0: Int, line0: String) {

    private val line: String = line0;
    private val lineCounter: Int = lineCounter0;

    def getLine(): String = {
      this.line
    }

    def getLineCounter(): Int = {
      this.lineCounter
    }

  }

  val GeneratedIdFieldName: String = "generatedId"
  val SubItemsFieldName: String = "subItems"
  val CalendarTypeLabel: String = "VCALENDAR"
  val TimeZoneTypeLabel: String = "VTIMEZONE"
  val DaylightTypeLabel: String = "DAYLIGHT"
  val StandardTypeLabel: String = "STANDARD"
  val EventTypeLabel: String = "VEVENT"
  val AlarmTypeLabel: String = "VALARM"

  val CalendarTypeFields: Array[String] = Array(GeneratedIdFieldName,
    SubItemsFieldName, "PRODID", "VERSION", "CALSCALE", "METHOD",
    "X-WR-CALNAME", "X-WR-TIMEZONE")

  val TimeZoneTypeFields: Array[String] = Array(GeneratedIdFieldName,
    SubItemsFieldName, "TZID", "X-LIC-LOCATION")

  val DaylightTypeFields: Array[String] = Array(GeneratedIdFieldName,
    SubItemsFieldName, "TZOFFSETFROM", "TZOFFSETTO", "TZNAME",
    "DTSTART", "RRULE")

  val StandardTypeFields: Array[String] = DaylightTypeFields

  val EventTypeFields: Array[String] = Array(GeneratedIdFieldName,
    SubItemsFieldName, "DTSTART", "DTEND", "RRULE", "ORGANIZER",
    "DTSTAMP", "UID", "ATTENDEE", "CREATED", "DESCRIPTION",
    "LAST-MODIFIED", "LOCATION", "SEQUENCE", "STATUS", "SUMMARY",
    "TRANSP", "X-ALT-DESC", "X-MICROSOFT-CDO-BUSYSTATUS", "CLASS")

  val AlarmTypeFields: Array[String] = Array(GeneratedIdFieldName,
    SubItemsFieldName, "ACTION", "DESCRIPTION", "SUMMARY", "ATTENDEE",
    "TRIGGER")

  var EventTyp: SimplifiedCompositeType = null

  val UnderscoreChar: Char = '_'
  val CommaChar: Char = ','
  val QuotesChar: Char = '"'
  val ColonChar: Char = ':'
  val SemicolonChar: Char = ';'
  val SpaceChar: Char = ' '
  val NewLineChar: Char = '\n'
  val GeneratedIdSeparatorChar: Char = '.'
  val FirstGeneratedIndex: Int = 0

  val Underscore: String = "" + UnderscoreChar

  val NewEvent: String = "BEGIN:" + EventTypeLabel
  val BeginKeyword: String = "BEGIN"
  val EndKeyword: String = "END"

  var input: Reader = new InputStreamReader(System.in)

  def this(input0: Reader) = {
    this()
    this.input = input0
  }

  def getKey(line: String): String = {
    if (line == null) {
      return null
    } else {
      var pos: Int = line.indexOf(ColonChar)
      if (pos == -1) {
        return line
      } else {
        var pos2: Int = line.indexOf(SemicolonChar)
        if (pos2 >= 0 && pos2 < pos) {
          pos = pos2
        }
        return line.substring(0, pos).trim()
      }
    }
  }

  def getValue(line: String): String = {
    if (line == null) {
      return null
    } else {
      var pos: Int = line.indexOf(ColonChar)
      if (pos == -1) {
        return ""
      } else {
        return line.substring(pos + 1, line.length()).trim()
      }
    }
  }

  def isBeginLine(line: String): Boolean = {
    return (line != null) && line.trim().startsWith(BeginKeyword)
  }

  def isEndLine(line: String): Boolean = {
    return (line != null) && line.trim().startsWith(EndKeyword)
  }

  private def getTypedValue(key: String, value: String,
    type0: CompositeType, lineCounter: Int): PrimitiveTypeValue = {
    if (key == null) {
      return new StringValue()
    } else {
      try {
        val typeStr: String = type0.getFieldType(key)
        if (typeStr == null) {
          throw new ParseException("Key '" + key
            + "' has an undefined type.")
        } else {
          val ret: PrimitiveTypeValue = (new PrimitiveTypeFactory())
            .newInstance(typeStr, value)
          return ret
        }
      } catch {
        case e: IOException => {
          throw new ParseException(e.getMessage() + " (line "
            + lineCounter + ")", e.getCause())
        }
      }
    }
  }

  private def preload(input: BufferedReader): List[Pair] = {
    val ret: ArrayList[Pair] = new ArrayList[Pair]()
    var sbuf: StringBuffer = new StringBuffer()
    var finish: Boolean = false
    var lineCounter: Int = 0
    while (!finish) {
      var line: String = input.readLine()
      if (line == null) {
        finish = true
      } else if (line.startsWith("" + SpaceChar)) {
        sbuf.append(line)
      } else {
        ret.add(new Pair(lineCounter, sbuf.toString()))
        sbuf = new StringBuffer()
        sbuf.append(line)
      }
      lineCounter += 1
    }
    return ret
  }

  private def parseProperty(line: String, currentTable: TableImpl,
    record: Record, lineCounter: Int): Unit = {
    if (currentTable == null) {
      throw new ParseException("New record was not declared (line "
        + lineCounter + ")")
    }

    val key: String = getKey(line)
    val valueStr: String = getValue(line)
    val value: PrimitiveTypeValue = getTypedValue(key, valueStr,
      currentTable.getType(), lineCounter)
    if (key.equals(ParserConstant.IdKeyword)) {
      if (currentTable.getIdentifiers().contains(valueStr)) {
        throw new ParseException("Identifier '"
          + ParserConstant.IdKeyword + ParserConstant.Space
          + ParserConstant.EqualsSign + ParserConstant.Space
          + valueStr + "' is duplicated (line " + lineCounter
          + ").")
      }
    }
    record.set(key, value)
  }

  def getGeneratedId(generatedIds: List[Int], level: Int): String = {
    while (level >= generatedIds.size()) {
      generatedIds.add(FirstGeneratedIndex)
    }
    val newValue: Int = generatedIds.get(level) + 1
    while (level < generatedIds.size()) {
      generatedIds.remove(generatedIds.size() - 1)
    }
    generatedIds.add(newValue)
    val sbuf: StringBuffer = new StringBuffer()
    var firstTime: Boolean = true
    for (counter: Int <- generatedIds) {
      if (firstTime) {
        firstTime = false
      } else {
        sbuf.append(GeneratedIdSeparatorChar)
      }
      sbuf.append(counter)
    }
    return sbuf.toString()
  }

  def parseMap(input: BufferedReader): TableMap = {
    val map: TreeMap[String, TableImpl] = new TreeMap[String, TableImpl]()

    map.put(CalendarTypeLabel, new TableImpl(new SimplifiedCompositeType(
      CalendarTypeFields)))
    map.put(TimeZoneTypeLabel, new TableImpl(new SimplifiedCompositeType(
      TimeZoneTypeFields)))
    map.put(DaylightTypeLabel, new TableImpl(new SimplifiedCompositeType(
      DaylightTypeFields)))
    map.put(StandardTypeLabel, new TableImpl(new SimplifiedCompositeType(
      StandardTypeFields)))
    map.put(EventTypeLabel, new TableImpl(new SimplifiedCompositeType(
      EventTypeFields)))
    map.put(AlarmTypeLabel, new TableImpl(new SimplifiedCompositeType(
      AlarmTypeFields)))

    var currentTable: TableImpl = null
    var currentRecord: Record = null
    var currentTableId: String = null

    val tableIdStack: Stack[String] = new Stack[String]()
    val recordStack: Stack[Record] = new Stack[Record]()
    val tableStack: Stack[TableImpl] = new Stack[TableImpl]()
    val generatedIds: ArrayList[Int] = new ArrayList[Int]()

    val lines: List[Pair] = preload(input)
    var lineCounter: Int = 0
    var firstTime: Boolean = true
    for (pair: Pair <- lines) {
      val line: String = pair.getLine()
      lineCounter = pair.getLineCounter()
      if (line != null && !line.trim().isEmpty()) {
        if (isBeginLine(line)) {
          val value: String = getValue(line)
          if (firstTime) {
            firstTime = false
          } else {
            tableIdStack.push(currentTableId)
            tableStack.push(currentTable)
            recordStack.push(currentRecord)
          }
          currentRecord = new RecordImpl()
          currentRecord.set(GeneratedIdFieldName, new StringValue(
            getGeneratedId(generatedIds, tableIdStack.size())))
          val refTable: TableImpl = map.get(value)
          if (refTable == null) {
            throw new ParseException("Unknown type '" + value
              + "' (line " + lineCounter + ").")
          }
          currentTableId = value
          currentTable = refTable

        } else if (isEndLine(line)) {
          val foreignKey: String = currentRecord.get(GeneratedIdFieldName)
            .render()
          currentTable.add(currentRecord)
          val value: String = getValue(line)
          val refTable: TableImpl = map.get(value)
          if (refTable == null) {
            throw new ParseException("Unknown type '" + value
              + "' (line " + lineCounter + ").")
          }
          if (!value.equals(currentTableId)) {
            throw new ParseException("Closing wrong type '" + value
              + "' (line " + lineCounter + ").")
          }
          if (tableStack.isEmpty()) {
            throw new ParseException("Too many " + EndKeyword
              + " keywords  (line " + lineCounter + ").")
          }
          currentTableId = tableIdStack.pop()
          currentTable = tableStack.pop()
          currentRecord = recordStack.pop()
          var subItems: PrimitiveTypeValue = currentRecord
            .get(SubItemsFieldName)
          if (subItems == null) {
            subItems = new StringValue(foreignKey)
          } else {
            subItems = new StringValue(subItems.render()
              + SpaceChar + foreignKey)
          }
          currentRecord.set(SubItemsFieldName, subItems)

        } else {
          parseProperty(line, currentTable, currentRecord,
            lineCounter)

        }
      }
    }

    if (currentTable != null && currentRecord != null) {
      currentTable.add(currentRecord)
    }

    if (!tableStack.isEmpty()) {
      throw new ParseException("Too few " + EndKeyword
        + " keywords  (line " + lineCounter + ").")
    }

    val ret: TableMapImpl = new TableMapImpl()
    for (key: String <- map.keySet()) {
      ret.put(key, map.get(key))
    }
    return ret
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
