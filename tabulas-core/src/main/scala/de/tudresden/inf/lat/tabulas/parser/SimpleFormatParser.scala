
package de.tudresden.inf.lat.tabulas.parser

import java.io.BufferedReader
import java.io.IOException
import java.io.InputStreamReader
import java.io.Reader
import java.util.ArrayList
import java.util.List
import java.util.Map
import java.util.Set
import java.util.StringTokenizer
import java.util.TreeMap
import java.util.TreeSet

import scala.collection.JavaConversions.asScalaSet

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

    private val line: String = line0;
    private val lineCounter: Int = lineCounter0;

    def getLine(): String = {
      this.line
    }

    def getLineCounter(): Int = {
      this.lineCounter
    }

  }

  def this(input0: Reader) = {
    this()
    this.input = input0
  }

  def getKey(line: String): String = {
    if (line == null) {
      null
    } else {
      val pos: Int = line.indexOf(ParserConstant.EqualsSign)
      if (pos == -1) {
        line
      } else {
        line.substring(0, pos).trim()
      }
    }
  }

  def getValue(line: String): String = {
    if (line == null) {
      null
    } else {
      val pos: Int = line.indexOf(ParserConstant.EqualsSign)
      if (pos == -1) {
        ""
      } else {
        line.substring(pos + ParserConstant.EqualsSign.length(), line.length()).trim()
      }
    }
  }

  def parseTypes(line: String, lineCounter: Int): CompositeTypeImpl = {
    val ret = new CompositeTypeImpl()
    val stok: StringTokenizer = new StringTokenizer(getValue(line))
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
    val list: List[String] = new ArrayList[String]
    val stok: StringTokenizer = new StringTokenizer(getValue(line))
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
      list.add(token)
    }
    table.setSortingOrder(list)
    table.setFieldsWithReverseOrder(fieldsWithReverseOrder)
  }

  def isTypeSelection(line: String): Boolean = {
    (line != null) && line.trim().startsWith(ParserConstant.TypeSelectionToken)
  }

  def isTypeDefinition(line: String): Boolean = {
    (line != null) && line.trim().startsWith(ParserConstant.TypeDefinitionToken)
  }

  def isSortingOrderDeclaration(line: String): Boolean = {
    (line != null) && line.trim().startsWith(ParserConstant.SortingOrderDeclarationToken)
  }

  def isNewRecord(line: String): Boolean = {
    (line != null) && line.trim().startsWith(ParserConstant.NewRecordToken)
  }

  def getTypedValue(key: String, value: String, type0: CompositeType, lineCounter: Int): PrimitiveTypeValue = {
    if (key == null) {
      new StringValue()
    } else {
      try {
        var typeStr: String = type0.getFieldType(key)
        if (typeStr == null) {
          throw new ParseException("Key '" + key + "' has an undefined type.")
        } else {
          val ret: PrimitiveTypeValue = (new PrimitiveTypeFactory())
            .newInstance(typeStr, value);
          ret
        }
      } catch {
        case e: ParseException => {
          throw new ParseException(e.getMessage() + " (line "
            + lineCounter + ")", e.getCause())
        }
      }
    }
  }

  def readMultiLine(input: BufferedReader, lineCounter0: Int): Pair = {
    var lineCounter: Int = lineCounter0
    var line: String = input.readLine()
    if (line == null) {
      new Pair(lineCounter, null)
    } else {
      lineCounter += 1
      if (line.startsWith(ParserConstant.CommentSymbol)) {
        new Pair(lineCounter, "")
      } else {
        var multiLine: String = line
        while (multiLine.endsWith(ParserConstant.LineContinuationSymbol)) {
          multiLine = multiLine.substring(0, multiLine.length() - ParserConstant.LineContinuationSymbol.length()) + ParserConstant.Space
          line = input.readLine()
          if (line != null) {
            lineCounter += 1
            multiLine += line
          }
        }
        new Pair(lineCounter, multiLine)
      }
    }
  }

  private def isIdProperty(line: String): Boolean = {
    val key: String = getKey(line)
    key.equals(ParserConstant.IdKeyword)
  }

  private def getIdProperty(line: String): String = {
    val key: String = getKey(line)
    val valueStr: String = getValue(line)
    if (key.equals(ParserConstant.IdKeyword)) {
      valueStr
    } else {
      null
    }
  }

  private def parseProperty(line: String, currentTable: TableImpl,
    record: Record, lineCounter: Int): Unit = {
    if (currentTable == null) {
      throw new ParseException("New record was not declared (line "
        + lineCounter + ")")
    }

    val key: String = getKey(line)
    val valueStr: String = getValue(line)
    val value: PrimitiveTypeValue = getTypedValue(key, valueStr, currentTable.getType(), lineCounter)
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

  def parseMap(input: BufferedReader): TableMap = {
    val map: Map[String, TableImpl] = new TreeMap[String, TableImpl]()

    var line: String = ""
    var currentTable: TableImpl = null
    var currentId: String = null
    var record: Record = null
    var lineCounter: Int = 0
    while (line != null) {
      var pair: Pair = readMultiLine(input, lineCounter)
      line = pair.getLine()
      lineCounter = pair.getLineCounter()
      if (line != null && !line.trim().isEmpty()) {
        if (isTypeSelection(line)) {
          val tableName: String = getValue(line)
          if (!map.containsKey(tableName)) {
            map.put(tableName, new TableImpl(
              new TableImpl()))
          }
          currentTable = map.get(tableName)

        } else if (isTypeDefinition(line)) {
          currentTable.setType(parseTypes(line, lineCounter))

        } else if (isSortingOrderDeclaration(line)) {
          setSortingOrder(line, currentTable)

        } else if (isNewRecord(line)) {
          record = new RecordImpl()
          currentTable.add(record)
          currentId = null

        } else {
          parseProperty(line, currentTable, record, lineCounter)
          if (isIdProperty(line)) {
            var successful: Boolean = false
            if (currentId == null) {
              currentId = getIdProperty(line)
              successful = currentTable.addId(currentId)
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
    map.keySet().foreach(key => ret.put(key, map.get(key)))
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

