
package de.tudresden.inf.lat.tabulas.parser

import java.io.{BufferedReader, IOException, InputStreamReader, Reader}
import java.net.{URI, URISyntaxException}
import java.util.{Objects, StringTokenizer}

import de.tudresden.inf.lat.tabulas.datatype.{CompositeType, CompositeTypeImpl, ParameterizedListValue, ParseException}
import de.tudresden.inf.lat.tabulas.datatype.{PrimitiveTypeFactory, PrimitiveTypeValue, Record, StringValue, URIType, URIValue}
import de.tudresden.inf.lat.tabulas.table.{PrefixMap, PrefixMapImpl, RecordImpl, TableImpl, TableMap, TableMapImpl}

import scala.collection.mutable

/** Parser of a table in simple format.
  *
  */
class SimpleFormatParser extends Parser {

  private var _input: Reader = new InputStreamReader(System.in)

  class Pair(lineCounter0: Int, line0: String) {

    private val _line: String = line0
    private val _lineCounter: Int = lineCounter0

    def getLine: String = {
      this._line
    }

    def getLineCounter: Int = {
      this._lineCounter
    }

  }

  def this(input: Reader) = {
    this()
    this._input = input
  }

  def getKey(line: String): Option[String] = {
    val result = if (Objects.isNull(line)) {
      None
    } else {
      val pos: Int = line.indexOf(ParserConstant.EqualsSign)
      val res = if (pos == -1) {
        Some(line)
      } else {
        Some(line.substring(0, pos).trim())
      }
      res
    }
    result
  }

  def hasKey(line: String, key: String): Boolean = {
    val optKey: Option[String] = getKey(line)
    val result: Boolean = optKey.isDefined && (optKey.get == key)
    result
  }

  def getValue(line: String): Option[String] = {
    val result = if (Objects.isNull(line)) {
      None
    } else {
      val pos: Int = line.indexOf(ParserConstant.EqualsSign)
      val res = if (pos == -1) {
        Some("")
      } else {
        Some(line.substring(pos + ParserConstant.EqualsSign.length(), line.length()).trim())
      }
      res
    }
    result
  }

  def parseTypes(line: String, lineCounter: Int): CompositeTypeImpl = {
    var result: CompositeTypeImpl = CompositeTypeImpl()
    val stok: StringTokenizer = new StringTokenizer(getValue(line).get)
    val factory: PrimitiveTypeFactory = PrimitiveTypeFactory()
    while (stok.hasMoreTokens) {
      val token: String = stok.nextToken()
      val pos: Int = token.indexOf(ParserConstant.TypeSign)
      if (pos == -1) {
        throw ParseException("Field '" + line + "' does not have a type. (line " + lineCounter + ")")
      } else {
        val key: String = token.substring(0, pos)
        val value: String = token.substring(pos + ParserConstant.TypeSign.length(), token.length())
        if (factory.contains(value)) {
          result = result.declareField(key, value).get
        } else {
          throw ParseException("Type '" + value + "' is undefined. (line " + lineCounter + ")")
        }
      }
    }
    result
  }

  private def asUri(uriStr: String, lineCounter: Int): URI = {
    val result: URI = try {
      new URI(uriStr)
    } catch {
      case e: URISyntaxException => throw ParseException("String '" + uriStr + "' is not a valid URI. (line " + lineCounter + ")")
    }
    result
  }

  def parsePrefixMap(line: String, lineCounter: Int): PrefixMap = {
    val result: PrefixMap = new PrefixMapImpl()
    val stok: StringTokenizer = new StringTokenizer(getValue(line).get)
    while (stok.hasMoreTokens) {
      val token: String = stok.nextToken()
      val pos: Int = token.indexOf(ParserConstant.PrefixSign)
      if (pos == -1) {
        throw ParseException("Prefix '" + line + "' does not have a definition. (line " + lineCounter + ")")
      } else {
        val key: String = token.substring(0, pos)
        val value: String = token.substring(pos + ParserConstant.PrefixSign.length(), token.length())
        result.put(asUri(key, lineCounter), asUri(value, lineCounter))
      }
    }
    result
  }

  private def setSortingOrder(line: String, table: TableImpl): Unit = {
    val fieldsWithReverseOrder = new mutable.TreeSet[String]()
    val list = new mutable.ArrayBuffer[String]
    val stok: StringTokenizer = new StringTokenizer(getValue(line).get)
    while (stok.hasMoreTokens) {
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
    table.setFieldsWithReverseOrder(fieldsWithReverseOrder.toSet)
  }

  def getTypedValue(key: String, value: String, type0: CompositeType, prefixMap: PrefixMap, lineCounter: Int): PrimitiveTypeValue = {
    var result: PrimitiveTypeValue = StringValue()
    if (Objects.isNull(key)) {
      result = StringValue()
    } else {
      try {
        val optTypeStr: Option[String] = type0.getFieldType(key)
        if (optTypeStr.isDefined) {
          val typeStr: String = optTypeStr.get
          result = PrimitiveTypeFactory().newInstance(typeStr, value)
          if (result.getType.equals(new URIType())) {
            val uri: URIValue = result.asInstanceOf[URIValue]
            result = new URIValue(prefixMap.getWithoutPrefix(uri.getUri))
          } else if (result.isInstanceOf[ParameterizedListValue]) {
            val list: ParameterizedListValue = result.asInstanceOf[ParameterizedListValue]
            if (list.getParameter.equals(new URIType())) {
              val newList = new ParameterizedListValue(new URIType())
              list.foreach(elem => {
                val uri: URIValue = elem.asInstanceOf[URIValue]
                newList += new URIValue(prefixMap.getWithoutPrefix(uri.getUri))
              })
              result = newList
            }
          }

        } else {
          throw ParseException("Key '" + key + "' has an undefined type.")
        }
      } catch {
        case e: ParseException => throw new ParseException(e.getMessage + " (line "
          + lineCounter + ")", e.getCause)
      }
    }
    result
  }

  def isMultiLine(line: String): Boolean = {
    line.trim().endsWith(ParserConstant.LineContinuationSymbol)
  }

  def getCleanLine(line: String): String = {
    val trimmedLine = line.trim()
    val result = if (isMultiLine(line)) {
      trimmedLine.substring(0, trimmedLine.length() - ParserConstant.LineContinuationSymbol.length()).trim()
    } else {
      trimmedLine
    }
    result
  }

  def readMultiLine(input: BufferedReader, lineCounter0: Int): Pair = {
    var lineCounter: Int = lineCounter0
    var result: Pair = new Pair(lineCounter, null)
    var line: String = input.readLine()
    if (Objects.isNull(line)) {
      result = new Pair(lineCounter, null)
    } else {
      lineCounter += 1
      if (line.startsWith(ParserConstant.CommentSymbol)) {
        result = new Pair(lineCounter, "")
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

        result = new Pair(lineCounter, sb.toString)
      }
    }
    result
  }

  private def isIdProperty(line: String): Boolean = {
    val optKey: Option[String] = getKey(line)
    val result = if (optKey.isDefined) {
      optKey.get.equals(ParserConstant.IdKeyword)
    } else {
      false
    }
    result
  }

  private def getIdProperty(line: String): Option[String] = {
    val optKey: Option[String] = getKey(line)
    val optValueStr: Option[String] = getValue(line)
    val result = if (optKey.isDefined && optValueStr.isDefined && optKey.get.equals(ParserConstant.IdKeyword)) {
      Some(optValueStr.get)
    } else {
      None
    }
    result
  }

  private def parseProperty(line: String, currentTable: TableImpl,
                            recordIdsOfCurrentTable: mutable.TreeSet[String], record: Record, lineCounter: Int): Unit = {
    if (Objects.isNull(currentTable)) {
      throw ParseException("New record was not declared (line "
        + lineCounter + ")")
    }

    val optKey: Option[String] = getKey(line)
    val optValueStr: Option[String] = getValue(line)
    if (optKey.isDefined && optValueStr.isDefined) {
      val key: String = optKey.get
      val valueStr: String = optValueStr.get
      val value: PrimitiveTypeValue = getTypedValue(key, valueStr, currentTable.getType, currentTable.getPrefixMap, lineCounter)
      if (key.equals(ParserConstant.IdKeyword)) {
        if (recordIdsOfCurrentTable.contains(valueStr)) {
          throw ParseException("Identifier '"
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
    val mapOfTables = new mutable.TreeMap[String, TableImpl]()
    val mapOfRecordIdsOfTables = new mutable.TreeMap[String, mutable.TreeSet[String]]()

    var line: String = ""
    var currentTable = new TableImpl()
    var recordIdsOfCurrentTable = mutable.TreeSet.empty
    var optCurrentId: Option[String] = None
    var record: Record = new RecordImpl()
    var lineCounter: Int = 0
    var isDefiningType: Boolean = false

    while (Objects.nonNull(line)) {
      val pair: Pair = readMultiLine(input, lineCounter)
      line = pair.getLine
      lineCounter = pair.getLineCounter
      if (Objects.nonNull(line) && !line.trim().isEmpty) {
        if (hasKey(line, ParserConstant.TypeSelectionToken)) {
          isDefiningType = true
          val optTableName: Option[String] = getValue(line)
          if (optTableName.isDefined) {
            val tableName: String = optTableName.get
            if (!mapOfTables.get(tableName).isDefined) {
              mapOfTables.put(tableName, new TableImpl(
                new TableImpl()))
              mapOfRecordIdsOfTables.put(tableName, new mutable.TreeSet[String]())
            }
            currentTable = mapOfTables.get(tableName).get
            recordIdsOfCurrentTable = mapOfRecordIdsOfTables.get(tableName).get
          }

        } else if (isDefiningType && hasKey(line, ParserConstant.TypeDefinitionToken)) {
          currentTable.setType(parseTypes(line, lineCounter))

        } else if (isDefiningType && hasKey(line, ParserConstant.PrefixMapToken)) {
          currentTable.setPrefixMap(parsePrefixMap(line, lineCounter))

        } else if (isDefiningType && hasKey(line, ParserConstant.SortingOrderDeclarationToken)) {
          setSortingOrder(line, currentTable)

        } else if (hasKey(line, ParserConstant.NewRecordToken)) {
          isDefiningType = false
          record = new RecordImpl()
          currentTable.add(record)
          optCurrentId = None

        } else {
          parseProperty(line, currentTable, recordIdsOfCurrentTable, record, lineCounter)
          if (isIdProperty(line)) {
            val successful = if (optCurrentId.isEmpty) {
              optCurrentId = getIdProperty(line)
              if (optCurrentId.isDefined) {
                recordIdsOfCurrentTable.add(optCurrentId.get)
              } else {
                false
              }
            } else {
              false
            }
            if (!successful) {
              throw ParseException(
                "Identifier has been already defined ('"
                  + optCurrentId.get + "') (line "
                  + lineCounter + ")")
            }
          }

        }
      }

    }

    val result = new TableMapImpl()
    mapOfTables.keySet.foreach(key => result.put(key, mapOfTables.get(key).get))
    result
  }

  override def parse(): TableMap = {
    val result = try {
      parseMap(new BufferedReader(this._input))

    } catch {
      case e: IOException => throw new RuntimeException(e)
    }
    result
  }

}

object SimpleFormatParser {

  def apply(): SimpleFormatParser = new SimpleFormatParser

}
