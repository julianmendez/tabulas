
package de.tudresden.inf.lat.tabulas.parser

import java.io.{BufferedReader, IOException, InputStreamReader, Reader}
import java.net.{URI, URISyntaxException}
import java.util.{Objects, StringTokenizer}

import de.tudresden.inf.lat.tabulas.datatype._
import de.tudresden.inf.lat.tabulas.table._

import scala.collection.mutable
import scala.collection.mutable.{ArrayBuffer, Map, Set, TreeMap, TreeSet}

/**
  * Parser of a table in simple format.
  *
  */
class SimpleFormatParser extends Parser {

  private var _input: Reader = new InputStreamReader(System.in)

  class Pair(lineCounter0: Int, line0: String) {

    private val _line: String = line0
    private val _lineCounter: Int = lineCounter0

    def getLine(): String = {
      return this._line
    }

    def getLineCounter(): Int = {
      return this._lineCounter
    }

  }

  def this(input: Reader) = {
    this()
    this._input = input
  }

  def getKey(line: String): Option[String] = {
    if (Objects.isNull(line)) {
      return Option.empty
    } else {
      val pos: Int = line.indexOf(ParserConstant.EqualsSign)
      if (pos == -1) {
        return Option.apply(line)
      } else {
        return Option.apply(line.substring(0, pos).trim())
      }
    }
  }

  def hasKey(line: String, key: String): Boolean = {
    val optKey: Option[String] = getKey(line)
    return (optKey.isDefined && (optKey.get == key))
  }

  def getValue(line: String): Option[String] = {
    if (Objects.isNull(line)) {
      return Option.empty
    } else {
      val pos: Int = line.indexOf(ParserConstant.EqualsSign)
      if (pos == -1) {
        return Option.apply("")
      } else {
        return Option.apply(line.substring(pos + ParserConstant.EqualsSign.length(), line.length()).trim())
      }
    }
  }

  def parseTypes(line: String, lineCounter: Int): CompositeTypeImpl = {
    val ret: CompositeTypeImpl = new CompositeTypeImpl()
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
    return ret
  }

  private def asUri(uriStr: String, lineCounter: Int): URI = {
    try {
      val uri: URI = new URI(uriStr)
      return uri
    } catch {
      case e: URISyntaxException => {
        throw new ParseException("String '" + uriStr + "' is not a valid URI. (line " + lineCounter + ")")
      }
    }
  }

  def parsePrefixMap(line: String, lineCounter: Int): PrefixMap = {
    val ret: PrefixMap = new PrefixMapImpl()
    val stok: StringTokenizer = new StringTokenizer(getValue(line).get)
    while (stok.hasMoreTokens()) {
      val token: String = stok.nextToken()
      val pos: Int = token.indexOf(ParserConstant.PrefixSign)
      if (pos == -1) {
        throw new ParseException("Prefix '" + line + "' does not have a definition. (line " + lineCounter + ")")
      } else {
        val key: String = token.substring(0, pos)
        val value: String = token.substring((pos + ParserConstant.PrefixSign.length()), token.length())
        ret.put(asUri(key, lineCounter), asUri(value, lineCounter))
      }
    }
    return ret
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

  def getTypedValue(key: String, value: String, type0: CompositeType, prefixMap: PrefixMap, lineCounter: Int): PrimitiveTypeValue = {
    if (Objects.isNull(key)) {
      return new StringValue()
    } else {
      try {
        var optTypeStr: Option[String] = type0.getFieldType(key)
        if (optTypeStr.isDefined) {
          val typeStr: String = optTypeStr.get
          var ret: PrimitiveTypeValue = (new PrimitiveTypeFactory()).newInstance(typeStr, value)
          if (ret.getType.equals(new URIType())) {
            val uri: URIValue = ret.asInstanceOf[URIValue]
            ret = new URIValue(prefixMap.getWithoutPrefix(uri.getUri))
          } else if (ret.isInstanceOf[ParameterizedListValue]) {
            val list: ParameterizedListValue = ret.asInstanceOf[ParameterizedListValue]
            if (list.getParameter.equals(new URIType())) {
              val newList = new ParameterizedListValue(new URIType())
              list.foreach(elem => {
                val uri: URIValue = elem.asInstanceOf[URIValue]
                newList += new URIValue(prefixMap.getWithoutPrefix(uri.getUri))
              })
              ret = newList
            }
          }
          return ret

        } else {
          throw new ParseException("Key '" + key + "' has an undefined type.")
        }
      } catch {
        case e: ParseException => {
          throw new ParseException(e.getMessage + " (line "
            + lineCounter + ")", e.getCause)
        }
      }
    }
  }

  def isMultiLine(line: String): Boolean = {
    return line.trim().endsWith(ParserConstant.LineContinuationSymbol)
  }

  def getCleanLine(line: String): String = {
    val trimmedLine: String = line.trim()
    if (isMultiLine(line)) {
      return trimmedLine.substring(0, trimmedLine.length() - ParserConstant.LineContinuationSymbol.length()).trim()
    } else {
      return trimmedLine
    }
  }

  def readMultiLine(input: BufferedReader, lineCounter0: Int): Pair = {
    var lineCounter: Int = lineCounter0
    var line: String = input.readLine()
    if (Objects.isNull(line)) {
      return new Pair(lineCounter, null)
    } else {
      lineCounter += 1
      if (line.startsWith(ParserConstant.CommentSymbol)) {
        return new Pair(lineCounter, "")
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

        return new Pair(lineCounter, sb.toString)
      }
    }
  }

  private def isIdProperty(line: String): Boolean = {
    val optKey: Option[String] = getKey(line)
    if (optKey.isDefined) {
      return optKey.get.equals(ParserConstant.IdKeyword)
    } else {
      return false
    }
  }

  private def getIdProperty(line: String): Option[String] = {
    val optKey: Option[String] = getKey(line)
    val optValueStr: Option[String] = getValue(line)
    if (optKey.isDefined && optValueStr.isDefined && optKey.get.equals(ParserConstant.IdKeyword)) {
      return Option.apply(optValueStr.get)
    } else {
      return Option.empty
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
      val value: PrimitiveTypeValue = getTypedValue(key, valueStr, currentTable.getType, currentTable.getPrefixMap, lineCounter)
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
    var currentTable: TableImpl = new TableImpl()
    var recordIdsOfCurrentTable: Set[String] = Set.empty
    var optCurrentId: Option[String] = Option.empty
    var record: Record = new RecordImpl()
    var lineCounter: Int = 0
    var isDefiningType: Boolean = false

    while (Objects.nonNull(line)) {
      var pair: Pair = readMultiLine(input, lineCounter)
      line = pair.getLine()
      lineCounter = pair.getLineCounter()
      if (Objects.nonNull(line) && !line.trim().isEmpty) {
        if (hasKey(line, ParserConstant.TypeSelectionToken)) {
          isDefiningType = true
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
          optCurrentId = Option.empty

        } else {
          parseProperty(line, currentTable, recordIdsOfCurrentTable, record, lineCounter)
          if (isIdProperty(line)) {
            var successful: Boolean = false
            if (optCurrentId.isEmpty) {
              optCurrentId = getIdProperty(line)
              if (optCurrentId.isDefined) {
                successful = recordIdsOfCurrentTable.add(optCurrentId.get)
              }
            }
            if (!successful) {
              throw new ParseException(
                "Identifier has been already defined ('"
                  + optCurrentId.get + "') (line "
                  + lineCounter + ")")
            }
          }

        }
      }

    }

    val ret: TableMapImpl = new TableMapImpl()
    mapOfTables.keySet.foreach(key => ret.put(key, mapOfTables.get(key).get))
    return ret
  }

  override def parse(): TableMap = {
    try {
      return parseMap(new BufferedReader(this._input))

    } catch {
      case e: IOException => {
        throw new RuntimeException(e)
      }
    }
  }

}

