package de.tudresden.inf.lat.tabulas.ext.parser

import java.io.{BufferedReader, IOException, Reader}
import java.util.Objects

import de.tudresden.inf.lat.tabulas.datatype._
import de.tudresden.inf.lat.tabulas.parser.Parser
import de.tudresden.inf.lat.tabulas.table.{RecordImpl, TableImpl, TableMapImpl}

import scala.collection.mutable
import scala.util.Try

/** Parser of a calendar.
  *
  */
case class CalendarParser() extends Parser {

  final val GeneratedIdFieldName: String = "generatedId"
  final val SubItemsFieldName: String = "subItems"
  final val CalendarTypeLabel: String = "VCALENDAR"
  final val TimeZoneTypeLabel: String = "VTIMEZONE"
  final val DaylightTypeLabel: String = "DAYLIGHT"
  final val StandardTypeLabel: String = "STANDARD"
  final val EventTypeLabel: String = "VEVENT"
  final val AlarmTypeLabel: String = "VALARM"
  final val CalendarTypeFields: Array[String] = Array(GeneratedIdFieldName,
    SubItemsFieldName, "PRODID", "VERSION", "CALSCALE", "METHOD",
    "X-WR-CALNAME", "X-WR-TIMEZONE")
  final val TimeZoneTypeFields: Array[String] = Array(GeneratedIdFieldName,
    SubItemsFieldName, "TZID", "X-LIC-LOCATION")
  final val DaylightTypeFields: Array[String] = Array(GeneratedIdFieldName,
    SubItemsFieldName, "TZOFFSETFROM", "TZOFFSETTO", "TZNAME",
    "DTSTART", "RRULE")
  final val StandardTypeFields: Array[String] = DaylightTypeFields
  final val EventTypeFields: Array[String] = Array(GeneratedIdFieldName,
    SubItemsFieldName, "DTSTART", "DTEND", "RRULE", "ORGANIZER",
    "DTSTAMP", "UID", "ATTENDEE", "CREATED", "DESCRIPTION",
    "LAST-MODIFIED", "LOCATION", "SEQUENCE", "STATUS", "SUMMARY",
    "TRANSP", "X-ALT-DESC", "X-MICROSOFT-CDO-BUSYSTATUS", "CLASS")
  final val AlarmTypeFields: Array[String] = Array(GeneratedIdFieldName,
    SubItemsFieldName, "ACTION", "DESCRIPTION", "SUMMARY", "ATTENDEE",
    "TRIGGER")
  final val EventTyp: SimplifiedCompositeType = SimplifiedCompositeType()
  final val UnderscoreChar: Char = '_'
  final val CommaChar: Char = ','
  final val QuotesChar: Char = '"'
  final val ColonChar: Char = ':'
  final val SemicolonChar: Char = ';'
  final val SpaceChar: Char = ' '
  final val NewLineChar: Char = '\n'
  final val GeneratedIdSeparatorChar: Char = '.'
  final val FirstGeneratedIndex: Int = 0
  final val Underscore: String = "" + UnderscoreChar
  final val NewEvent: String = "BEGIN:" + EventTypeLabel
  final val BeginKeyword: String = "BEGIN"
  final val EndKeyword: String = "END"

  override def parse(input: Reader): Try[TableMapImpl] = Try {
    parseMap(new BufferedReader(input))
  }

  // scalastyle:off method.length
  // scalastyle:off cyclomatic.complexity
  def parseMap(input: BufferedReader): TableMapImpl = {
    val map = new mutable.TreeMap[String, TableImpl]()

    map.put(CalendarTypeLabel, TableImpl(SimplifiedCompositeType(CalendarTypeFields)))
    map.put(TimeZoneTypeLabel, TableImpl(SimplifiedCompositeType(TimeZoneTypeFields)))
    map.put(DaylightTypeLabel, TableImpl(SimplifiedCompositeType(DaylightTypeFields)))
    map.put(StandardTypeLabel, TableImpl(SimplifiedCompositeType(StandardTypeFields)))
    map.put(EventTypeLabel, TableImpl(SimplifiedCompositeType(EventTypeFields)))
    map.put(AlarmTypeLabel, TableImpl(SimplifiedCompositeType(AlarmTypeFields)))

    var currentTable = TableImpl()
    var currentRecord = RecordImpl()
    var currentTableId = ""

    val tableIdStack = new MyStack[String]()
    val recordStack = new MyStack[RecordImpl]()
    val tableStack = new MyStack[TableImpl]()
    val generatedIds = new mutable.ArrayBuffer[Int]()

    val lines = preload(input)
    var lineCounter = 0
    var firstTime = true
    lines.foreach(pair => {
      val line = pair.getLine
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
          currentRecord = RecordImpl()
          currentRecord.set(GeneratedIdFieldName, new StringValue(
            getGeneratedId(generatedIds, tableIdStack.size)))
          currentTableId = value
          val optCurrentTable = map.get(value)
          if (optCurrentTable.isEmpty) {
            throw ParseException("Unknown type '" + value
              + "' (line " + lineCounter + ").")
          }
          currentTable = optCurrentTable.get

        } else if (isEndLine(line)) {
          val foreignKey = currentRecord.get(GeneratedIdFieldName)
            .get.render()
          currentTable.add(currentRecord)
          val value = getValue(line).get
          if (Objects.isNull(map.get(value))) {
            throw ParseException("Unknown type '" + value
              + "' (line " + lineCounter + ").")
          }
          if (!value.equals(currentTableId)) {
            throw ParseException("Closing wrong type '" + value
              + "' (line " + lineCounter + ").")
          }
          if (tableStack.isEmpty) {
            throw ParseException("Too many " + EndKeyword
              + " keywords  (line " + lineCounter + ").")
          }
          currentTableId = tableIdStack.pop()
          currentTable = tableStack.pop()
          currentRecord = recordStack.pop()
          val optSubItems = currentRecord.get(SubItemsFieldName)
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
    })

    if (Objects.nonNull(currentTable) && Objects.nonNull(currentRecord)) {
      currentTable.add(currentRecord)
    }

    if (tableStack.nonEmpty) {
      throw ParseException("Too few " + EndKeyword
        + " keywords  (line " + lineCounter + ").")
    }

    TableMapImpl(map.toMap)
  }
  // scalastyle:on cyclomatic.complexity
  // scalastyle:on method.length

  def isBeginLine(line: String): Boolean = {
    Objects.nonNull(line) && line.trim().startsWith(BeginKeyword)
  }

  def isEndLine(line: String): Boolean = {
    Objects.nonNull(line) && line.trim().startsWith(EndKeyword)
  }

  private def preload(input: BufferedReader): Seq[Pair] = {
    val result = new mutable.ArrayBuffer[Pair]()
    var sbuf = new StringBuffer()
    var lineCounter = 0
    input.lines().toArray().foreach(obj => {
      val line = obj.asInstanceOf[String]
      if (line.startsWith("" + SpaceChar)) {
        sbuf.append(line)
      } else {
        result += Pair(lineCounter, sbuf.toString)
        sbuf = new StringBuffer()
        sbuf.append(line)
      }
      lineCounter += 1
    })
    result
  }

  private def parseProperty(line: String, currentTable: TableImpl,
    record: RecordImpl, lineCounter: Int): Unit = {
    if (Objects.isNull(currentTable)) {
      throw ParseException("New record was not declared (line "
        + lineCounter + ")")
    }

    val optKey = getKey(line)
    val optValueStr = getValue(line)
    if (optKey.isDefined && optValueStr.isDefined) {
      val key = optKey.get
      val valueStr = optValueStr.get
      val value = getTypedValue(key, valueStr, currentTable.getType, lineCounter)
      record.set(key, value)
    }
  }

  def getKey(line: String): Option[String] = {
    val result = if (Objects.isNull(line)) {
      None
    } else {
      val pos = line.indexOf(ColonChar)
      val res = if (pos == -1) {
        Some(line)
      } else {
        val pos2 = line.indexOf(SemicolonChar)
        val lastPos = if (pos2 >= 0 && pos2 < pos) {
          pos2
        } else {
          pos
        }
        Some(line.substring(0, lastPos).trim())
      }
      res
    }
    result
  }

  private def getTypedValue(key: String, value: String, type0: CompositeType, lineCounter: Int): PrimitiveTypeValue = {
    val result = if (Objects.isNull(key)) {
      StringValue()
    } else {
      try {
        val optTypeStr = type0.getFieldType(key)
        val primType = if (optTypeStr.isDefined) {
          PrimitiveTypeFactory().getType(optTypeStr.get).get // caught by the try
        } else {
          throw ParseException("Key '" + key + "' has an undefined type.")
        }
        primType.parse(value)
      } catch {
        case e: IOException => throw new ParseException(e.getMessage + " (line "
          + lineCounter + ")", e.getCause)
      }
    }
    result
  }

  def getValue(line: String): Option[String] = {
    val result = if (Objects.isNull(line)) {
      None
    } else {
      val pos = line.indexOf(ColonChar)
      val res = if (pos == -1) {
        Some("")
      } else {
        Some(line.substring(pos + 1, line.length()).trim())
      }
      res
    }
    result
  }

  def getGeneratedId(generatedIds: Seq[Int], level: Int): String = {
    val auxGeneratedIds = new mutable.ArrayBuffer[Int]
    auxGeneratedIds ++= generatedIds
    while (level >= auxGeneratedIds.size) {
      auxGeneratedIds += FirstGeneratedIndex
    }
    val newValue = generatedIds(level) + 1
    while (level < auxGeneratedIds.size) {
      auxGeneratedIds.remove(auxGeneratedIds.size - 1)
    }
    auxGeneratedIds += newValue
    val result = auxGeneratedIds.mkString("" + GeneratedIdSeparatorChar)
    result
  }

  case class Pair(lineCounter: Int, line: String) {

    def getLine: String = line

    def getLineCounter: Int = lineCounter

  }

  class MyStack[A] extends mutable.ArrayBuffer[A] {

    def push(elem: A): MyStack[A] = {
      insert(0, elem)
      this
    }

    def pop(): A = {
      iterator.next() // this throws a NoSuchElementException in an empty stack
      val result: A = remove(0)
      result
    }

  }

}

