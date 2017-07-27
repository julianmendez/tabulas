package de.tudresden.inf.lat.tabulas.ext.parser

import java.io.{BufferedReader, IOException, InputStreamReader, Reader}
import java.util.Objects

import de.tudresden.inf.lat.tabulas.datatype._
import de.tudresden.inf.lat.tabulas.parser.Parser
import de.tudresden.inf.lat.tabulas.table.{RecordImpl, TableImpl, TableMap, TableMapImpl}

import scala.collection.mutable.{ArrayBuffer, Buffer, Map, TreeMap}

/**
  * Parser of a calendar.
  *
  */
class CalendarParser extends Parser {

  class Pair(lineCounter0: Int, line0: String) {

    private val _line: String = line0
    private val _lineCounter: Int = lineCounter0

    def getLine: String = {
      return this._line
    }

    def getLineCounter: Int = {
      return this._lineCounter
    }

  }

  class MyStack[A] extends ArrayBuffer[A] {

    def push(elem: A): MyStack[A] = {
      insert(0, elem)

      return this
    }

    def pop(): A = {
      iterator.next() // this throws an NoSuchElementException in an empty stack
      val result: A = remove(0)

      return result
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

  var EventTyp: SimplifiedCompositeType = new SimplifiedCompositeType()

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

  def getKey(line: String): Option[String] = {
    var result: Option[String] = Option.empty
    if (Objects.isNull(line)) {
      result = Option.empty
    } else {
      var pos: Int = line.indexOf(ColonChar)
      if (pos == -1) {
        result = Option.apply(line)
      } else {
        val pos2: Int = line.indexOf(SemicolonChar)
        if (pos2 >= 0 && pos2 < pos) {
          pos = pos2
        }
        result = Option.apply(line.substring(0, pos).trim())
      }
    }

    return result
  }

  def getValue(line: String): Option[String] = {
    var result: Option[String] = Option.empty
    if (Objects.isNull(line)) {
      result = Option.empty
    } else {
      val pos: Int = line.indexOf(ColonChar)
      if (pos == -1) {
        result = Option.apply("")
      } else {
        result = Option.apply(line.substring(pos + 1, line.length()).trim())
      }
    }

    return result
  }

  def isBeginLine(line: String): Boolean = {
    return Objects.nonNull(line) && line.trim().startsWith(BeginKeyword)
  }

  def isEndLine(line: String): Boolean = {
    return Objects.nonNull(line) && line.trim().startsWith(EndKeyword)
  }

  private def getTypedValue(key: String, value: String,
                            type0: CompositeType, lineCounter: Int): PrimitiveTypeValue = {
    var result: PrimitiveTypeValue = new StringValue()
    if (Objects.isNull(key)) {
      result = new StringValue()
    } else {
      try {
        val optTypeStr: Option[String] = type0.getFieldType(key)
        if (optTypeStr.isDefined) {
          result = (new PrimitiveTypeFactory()).newInstance(optTypeStr.get, value)
        } else {
          throw new ParseException("Key '" + key + "' has an undefined type.")
        }
      } catch {
        case e: IOException => throw new ParseException(e.getMessage + " (line "
          + lineCounter + ")", e.getCause)
      }
    }

    return result
  }

  private def preload(input: BufferedReader): Buffer[Pair] = {
    val result: Buffer[Pair] = new ArrayBuffer[Pair]()
    var sbuf: StringBuffer = new StringBuffer()
    var finish: Boolean = false
    var lineCounter: Int = 0
    input.lines().toArray().foreach(obj => {
      val line = obj.asInstanceOf[String]
      if (line.startsWith("" + SpaceChar)) {
        sbuf.append(line)
      } else {
        result += new Pair(lineCounter, sbuf.toString)
        sbuf = new StringBuffer()
        sbuf.append(line)
      }
      lineCounter += 1
    })

    return result
  }

  private def parseProperty(line: String, currentTable: TableImpl,
                            record: Record, lineCounter: Int): Unit = {
    if (Objects.isNull(currentTable)) {
      throw new ParseException("New record was not declared (line "
        + lineCounter + ")")
    }

    val optKey: Option[String] = getKey(line)
    val optValueStr: Option[String] = getValue(line)
    if (optKey.isDefined && optValueStr.isDefined) {
      val key: String = optKey.get
      val valueStr: String = optValueStr.get
      val value: PrimitiveTypeValue = getTypedValue(key, valueStr,
        currentTable.getType, lineCounter)
      record.set(key, value)
    }
  }

  def getGeneratedId(generatedIds: Buffer[Int], level: Int): String = {
    while (level >= generatedIds.size) {
      generatedIds += FirstGeneratedIndex
    }
    val newValue: Int = generatedIds(level) + 1
    while (level < generatedIds.size) {
      generatedIds.remove(generatedIds.size - 1)
    }
    generatedIds += newValue
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
    val result: String = sbuf.toString

    return result
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

    var currentTable: TableImpl = new TableImpl()
    var currentRecord: Record = new RecordImpl()
    var currentTableId: String = ""

    val tableIdStack: MyStack[String] = new MyStack[String]()
    val recordStack: MyStack[Record] = new MyStack[Record]()
    val tableStack: MyStack[TableImpl] = new MyStack[TableImpl]()
    val generatedIds: Buffer[Int] = new ArrayBuffer[Int]()

    val lines: Buffer[Pair] = preload(input)
    var lineCounter: Int = 0
    var firstTime: Boolean = true
    for (pair: Pair <- lines) {
      val line: String = pair.getLine
      lineCounter = pair.getLineCounter
      if (Objects.nonNull(line) && !line.trim().isEmpty) {
        if (isBeginLine(line)) {
          val value: String = getValue(line).get
          if (firstTime) {
            firstTime = false
          } else {
            tableIdStack.push(currentTableId)
            tableStack.push(currentTable)
            recordStack.push(currentRecord)
          }
          currentRecord = new RecordImpl()
          currentRecord.set(GeneratedIdFieldName, new StringValue(
            getGeneratedId(generatedIds, tableIdStack.size)))
          currentTableId = value
          val optCurrentTable: Option[TableImpl] = map.get(value)
          if (optCurrentTable.isEmpty) {
            throw new ParseException("Unknown type '" + value
              + "' (line " + lineCounter + ").")
          }
          currentTable = optCurrentTable.get

        } else if (isEndLine(line)) {
          val foreignKey: String = currentRecord.get(GeneratedIdFieldName)
            .get.render()
          currentTable.add(currentRecord)
          val value: String = getValue(line).get
          if (Objects.isNull(map.get(value))) {
            throw new ParseException("Unknown type '" + value
              + "' (line " + lineCounter + ").")
          }
          if (!value.equals(currentTableId)) {
            throw new ParseException("Closing wrong type '" + value
              + "' (line " + lineCounter + ").")
          }
          if (tableStack.isEmpty) {
            throw new ParseException("Too many " + EndKeyword
              + " keywords  (line " + lineCounter + ").")
          }
          currentTableId = tableIdStack.pop()
          currentTable = tableStack.pop()
          currentRecord = recordStack.pop()
          val optSubItems: Option[PrimitiveTypeValue] = currentRecord.get(SubItemsFieldName)
          if (optSubItems.isDefined) {
            currentRecord.set(SubItemsFieldName, new StringValue(optSubItems.get.render() + SpaceChar + foreignKey))

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

    if (!tableStack.isEmpty) {
      throw new ParseException("Too few " + EndKeyword
        + " keywords  (line " + lineCounter + ").")
    }

    val result: TableMapImpl = new TableMapImpl()
    map.keySet.foreach(key => result.put(key, map.get(key).get))

    return result
  }

  override def parse(): TableMap = {
    var result: TableMap = new TableMapImpl()
    try {
      result = parseMap(new BufferedReader(this.input))

    } catch {
      case e: IOException => throw new RuntimeException(e)
    }

    return result
  }

}
