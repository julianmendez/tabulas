package de.tudresden.inf.lat.tabulas.ext.parser

import java.io.BufferedReader
import java.io.IOException
import java.io.InputStreamReader
import java.io.Reader
import java.util.ArrayList
import java.util.List
import java.util.Map
import java.util.Objects
import java.util.Optional
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
      return this.line
    }

    def getLineCounter(): Int = {
      return this.lineCounter
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

  def this(input: Reader) = {
    this()
    this.input = input
  }

  def getKey(line: String): Optional[String] = {
    if (Objects.isNull(line)) {
      return Optional.empty()
    } else {
      var pos: Int = line.indexOf(ColonChar)
      if (pos == -1) {
        return Optional.of(line)
      } else {
        var pos2: Int = line.indexOf(SemicolonChar)
        if (pos2 >= 0 && pos2 < pos) {
          pos = pos2
        }
        return Optional.of(line.substring(0, pos).trim())
      }
    }
  }

  def getValue(line: String): Optional[String] = {
    if (Objects.isNull(line)) {
      return Optional.empty()
    } else {
      var pos: Int = line.indexOf(ColonChar)
      if (pos == -1) {
        return Optional.of("")
      } else {
        return Optional.of(line.substring(pos + 1, line.length()).trim())
      }
    }
  }

  def isBeginLine(line: String): Boolean = {
    return Objects.nonNull(line) && line.trim().startsWith(BeginKeyword)
  }

  def isEndLine(line: String): Boolean = {
    return Objects.nonNull(line) && line.trim().startsWith(EndKeyword)
  }

  private def getTypedValue(key: String, value: String,
    type0: CompositeType, lineCounter: Int): PrimitiveTypeValue = {
    if (Objects.isNull(key)) {
      return new StringValue()
    } else {
      try {
        val optTypeStr: Optional[String] = type0.getFieldType(key)
        if (optTypeStr.isPresent()) {
          return (new PrimitiveTypeFactory()).newInstance(optTypeStr.get(), value)
        } else {
          throw new ParseException("Key '" + key + "' has an undefined type.")
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
    val ret: List[Pair] = new ArrayList[Pair]()
    var sbuf: StringBuffer = new StringBuffer()
    var finish: Boolean = false
    var lineCounter: Int = 0
    input.lines().toArray().foreach(obj => {
      val line = obj.asInstanceOf[String]
      if (line.startsWith("" + SpaceChar)) {
        sbuf.append(line)
      } else {
        ret.add(new Pair(lineCounter, sbuf.toString()))
        sbuf = new StringBuffer()
        sbuf.append(line)
      }
      lineCounter += 1
    });
    return ret
  }

  private def parseProperty(line: String, currentTable: TableImpl,
    record: Record, lineCounter: Int): Unit = {
    if (Objects.isNull(currentTable)) {
      throw new ParseException("New record was not declared (line "
        + lineCounter + ")")
    }

    val optKey: Optional[String] = getKey(line)
    val optValueStr: Optional[String] = getValue(line)
    if (optKey.isPresent() && optValueStr.isPresent()) {
      val key: String = optKey.get()
      val valueStr: String = optValueStr.get()
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
    val map: Map[String, TableImpl] = new TreeMap[String, TableImpl]()

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
    val generatedIds: List[Int] = new ArrayList[Int]()

    val lines: List[Pair] = preload(input)
    var lineCounter: Int = 0
    var firstTime: Boolean = true
    for (pair: Pair <- lines) {
      val line: String = pair.getLine()
      lineCounter = pair.getLineCounter()
      if (Objects.nonNull(line) && !line.trim().isEmpty()) {
        if (isBeginLine(line)) {
          val value: String = getValue(line).get()
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
          if (Objects.isNull(refTable)) {
            throw new ParseException("Unknown type '" + value
              + "' (line " + lineCounter + ").")
          }
          currentTableId = value
          currentTable = refTable

        } else if (isEndLine(line)) {
          val foreignKey: String = currentRecord.get(GeneratedIdFieldName)
            .get().render()
          currentTable.add(currentRecord)
          val value: String = getValue(line).get()
          val refTable: TableImpl = map.get(value)
          if (Objects.isNull(refTable)) {
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
          var optSubItems: Optional[PrimitiveTypeValue] = currentRecord.get(SubItemsFieldName)
          if (optSubItems.isPresent()) {
            currentRecord.set(SubItemsFieldName, new StringValue(optSubItems.get().render() + SpaceChar + foreignKey))

          } else {
            currentRecord.set(SubItemsFieldName, new StringValue(foreignKey))

          }

        } else {
          parseProperty(line, currentTable, currentRecord,
            lineCounter)

        }
      }
    }

    if (Objects.nonNull(currentTable) && Objects.nonNull(currentRecord)) {
      currentTable.add(currentRecord)
    }

    if (!tableStack.isEmpty()) {
      throw new ParseException("Too few " + EndKeyword
        + " keywords  (line " + lineCounter + ").")
    }

    val ret: TableMapImpl = new TableMapImpl()
    map.keySet().foreach(key => ret.put(key, map.get(key)))
    return ret
  }

  override def parse(): TableMap = {
    try {
      return parseMap(new BufferedReader(this.input))

    } catch {
      case e: IOException => {
        throw new RuntimeException(e)
      }
    }
  }

}
