
package de.tudresden.inf.lat.tabulas.parser

import java.io.BufferedReader
import java.io.IOException
import java.io.InputStreamReader
import java.io.Reader
import scala.collection.mutable.ArrayBuffer
import scala.collection.mutable
import scala.collection.mutable.Map
import java.util.Objects
import scala.collection.mutable.Set
import java.util.StringTokenizer
import scala.collection.mutable.TreeMap
import scala.collection.mutable.TreeSet

import scala.collection.JavaConverters.asScalaSetConverter

import de.tudresden.inf.lat.tabulas.datatype.CompositeType
import de.tudresden.inf.lat.tabulas.datatype.CompositeTypeImpl
import de.tudresden.inf.lat.tabulas.datatype.ParseException
import de.tudresden.inf.lat.tabulas.datatype.PrimitiveTypeFactory
import de.tudresden.inf.lat.tabulas.datatype.PrimitiveTypeValue
import de.tudresden.inf.lat.tabulas.datatype.Record
import de.tudresden.inf.lat.tabulas.datatype.StringValue
import de.tudresden.inf.lat.tabulas.table.RecordImpl
import de.tudresden.inf.lat.tabulas.table.TableImpl
import de.tudresden.inf.lat.tabulas.table.TableMap
import de.tudresden.inf.lat.tabulas.table.TableMapImpl

/**
 * Parser of a table in simple format.
 *
 */
class SimpleFormatParser extends Parser {

  private var input: Reader = new InputStreamReader(System.in)

  class Pair(lineCounter0: Int, line0: String) {

    private val line: String = line0
    private val lineCounter: Int = lineCounter0

    def getLine(): String = {
      this.line
    }

    def getLineCounter(): Int = {
      this.lineCounter
    }

  }

  def this(input: Reader) = {
    this()
    this.input = input
  }

  def getKey(line: String): Option[String] = {
    if (Objects.isNull(line)) {
      Option.empty
    } else {
      val pos: Int = line.indexOf(ParserConstant.EqualsSign)
      if (pos == -1) {
        Option.apply(line)
      } else {
        Option.apply(line.substring(0, pos).trim())
      }
    }
  }

  def getValue(line: String): Option[String] = {
    if (Objects.isNull(line)) {
      Option.empty
    } else {
      val pos: Int = line.indexOf(ParserConstant.EqualsSign)
      if (pos == -1) {
        Option.apply("")
      } else {
        Option.apply(line.substring(pos + ParserConstant.EqualsSign.length(), line.length()).trim())
      }
    }
  }

  def parseTypes(line: String, lineCounter: Int): CompositeTypeImpl = {
    val ret = new CompositeTypeImpl()
    val stok: StringTokenizer = new StringTokenizer(getValue(line).get)
    val factory: PrimitiveTypeFactory = new PrimitiveTypeFactory()
    while (stok.hasMoreTokens()) {
      val token: String = stok.nextToken()
      val pos: Int = token.indexOf(ParserConstant.TypeSign)
      if (pos == -1) {
        throw new ParseException("Field '" + line + "' does not have a type. (line " + lineCounter + ")")
      } else {
        val key: String = token.substring(0, pos)
        val value: String = token.substring((pos + ParserConstant.TypeSign.length()), token.length())
        if (factory.contains(value)) {
          ret.declareField(key, value)
        } else {
          throw new ParseException("Type '" + value + "' is undefined. (line " + lineCounter + ")")
        }
      }
    }
    ret
  }

  private def setSortingOrder(line: String, table: TableImpl): Unit = {
    val fieldsWithReverseOrder: Set[String] = new TreeSet[String]()
    val list: mutable.Buffer[String] = new ArrayBuffer[String]
    val stok: StringTokenizer = new StringTokenizer(getValue(line).get)
    while (stok.hasMoreTokens()) {
      var token: String = stok.nextToken()
      if (token.startsWith(ParserConstant.StandardOrderSign)) {
        token = token.substring(ParserConstant.StandardOrderSign
          .length())
      } else if (token.startsWith(ParserConstant.ReverseOrderSign)) {
        token = token.substring(ParserConstant.ReverseOrderSign
          .length())
        fieldsWithReverseOrder.add(token)
      }
      list += token
    }
    table.setSortingOrder(list)
    table.setFieldsWithReverseOrder(fieldsWithReverseOrder)
  }

  def isTypeSelection(line: String): Boolean = {
    Objects.nonNull(line) && line.trim().startsWith(ParserConstant.TypeSelectionToken)
  }

  def isTypeDefinition(line: String): Boolean = {
    Objects.nonNull(line) && line.trim().startsWith(ParserConstant.TypeDefinitionToken)
  }

  def isSortingOrderDeclaration(line: String): Boolean = {
    Objects.nonNull(line) && line.trim().startsWith(ParserConstant.SortingOrderDeclarationToken)
  }

  def isNewRecord(line: String): Boolean = {
    Objects.nonNull(line) && line.trim().startsWith(ParserConstant.NewRecordToken)
  }

  def getTypedValue(key: String, value: String, type0: CompositeType, lineCounter: Int): PrimitiveTypeValue = {
    if (Objects.isNull(key)) {
      new StringValue()
    } else {
      try {
        var optTypeStr: Option[String] = type0.getFieldType(key)
        if (optTypeStr.isDefined) {
          (new PrimitiveTypeFactory()).newInstance(optTypeStr.get, value)

        } else {
          throw new ParseException("Key '" + key + "' has an undefined type.")
        }
      } catch {
        case e: ParseException => {
          throw new ParseException(e.getMessage() + " (line "
            + lineCounter + ")", e.getCause())
        }
      }
    }
  }

  def isMultiLine(line: String): Boolean = {
    line.trim().endsWith(ParserConstant.LineContinuationSymbol)
  }

  def getCleanLine(line: String): String = {
    val trimmedLine: String = line.trim()
    if (isMultiLine(line)) {
      trimmedLine.substring(0, trimmedLine.length() - ParserConstant.LineContinuationSymbol.length()).trim()
    } else {
      trimmedLine
    }
  }

  def readMultiLine(input: BufferedReader, lineCounter0: Int): Pair = {
    var lineCounter: Int = lineCounter0
    var line: String = input.readLine()
    if (Objects.isNull(line)) {
      new Pair(lineCounter, null)
    } else {
      lineCounter += 1
      if (line.startsWith(ParserConstant.CommentSymbol)) {
        new Pair(lineCounter, "")
      } else {
        val sb: StringBuilder = new StringBuilder()
        while (isMultiLine(line)) {
          sb.append(getCleanLine(line))
          sb.append(ParserConstant.Space)
          line = input.readLine()
          if (Objects.nonNull(line)) {
            lineCounter += 1
          }
        }
        sb.append(getCleanLine(line))

        new Pair(lineCounter, sb.toString())
      }
    }
  }

  private def isIdProperty(line: String): Boolean = {
    val optKey: Option[String] = getKey(line)
    if (optKey.isDefined) {
      optKey.get.equals(ParserConstant.IdKeyword)
    } else {
      false
    }
  }

  private def getIdProperty(line: String): Option[String] = {
    val optKey: Option[String] = getKey(line)
    val optValueStr: Option[String] = getValue(line)
    if (optKey.isDefined && optValueStr.isDefined && optKey.get.equals(ParserConstant.IdKeyword)) {
      Option.apply(optValueStr.get)
    } else {
      Option.empty
    }
  }

  private def parseProperty(line: String, currentTable: TableImpl,
    recordIdsOfCurrentTable: Set[String], record: Record, lineCounter: Int): Unit = {
    if (Objects.isNull(currentTable)) {
      throw new ParseException("New record was not declared (line "
        + lineCounter + ")")
    }

    val optKey: Option[String] = getKey(line)
    val optValueStr: Option[String] = getValue(line)
    if (optKey.isDefined && optValueStr.isDefined) {
      val key: String = optKey.get
      val valueStr: String = optValueStr.get
      val value: PrimitiveTypeValue = getTypedValue(key, valueStr, currentTable.getType(), lineCounter)
      if (key.equals(ParserConstant.IdKeyword)) {
        if (recordIdsOfCurrentTable.contains(valueStr)) {
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

  def parseMap(input: BufferedReader): TableMap = {
    val mapOfTables: Map[String, TableImpl] = new TreeMap[String, TableImpl]()
    val mapOfRecordIdsOfTables: Map[String, Set[String]] = new TreeMap[String, Set[String]]()

    var line: String = ""
    var currentTable: TableImpl = null
    var recordIdsOfCurrentTable: Set[String] = null
    var currentId: String = null
    var record: Record = null
    var lineCounter: Int = 0

    while (Objects.nonNull(line)) {
      var pair: Pair = readMultiLine(input, lineCounter)
      line = pair.getLine()
      lineCounter = pair.getLineCounter()
      if (Objects.nonNull(line) && !line.trim().isEmpty()) {
        if (isTypeSelection(line)) {
          val optTableName: Option[String] = getValue(line)
          if (optTableName.isDefined) {
            val tableName: String = optTableName.get
            if (!mapOfTables.get(tableName).isDefined) {
              mapOfTables.put(tableName, new TableImpl(
                new TableImpl()))
              mapOfRecordIdsOfTables.put(tableName, new TreeSet[String]())
            }
            currentTable = mapOfTables.get(tableName).get
            recordIdsOfCurrentTable = mapOfRecordIdsOfTables.get(tableName).get
          }

        } else if (isTypeDefinition(line)) {
          currentTable.setType(parseTypes(line, lineCounter))

        } else if (isSortingOrderDeclaration(line)) {
          setSortingOrder(line, currentTable)

        } else if (isNewRecord(line)) {
          record = new RecordImpl()
          currentTable.add(record)
          currentId = null

        } else {
          parseProperty(line, currentTable, recordIdsOfCurrentTable, record, lineCounter)
          if (isIdProperty(line)) {
            var successful: Boolean = false
            if (Objects.isNull(currentId)) {
              val optCurrentId: Option[String] = getIdProperty(line)
              if (optCurrentId.isDefined) {
                currentId = optCurrentId.get
                successful = recordIdsOfCurrentTable.add(currentId)
              }
            }
            if (!successful) {
              throw new ParseException(
                "Identifier has been already defined ('"
                  + currentId + "') (line "
                  + lineCounter + ")")
            }
          }

        }
      }

    }

    val ret: TableMapImpl = new TableMapImpl()
    mapOfTables.keySet.foreach(key => ret.put(key, mapOfTables.get(key).get))
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

