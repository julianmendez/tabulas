
package de.tudresden.inf.lat.tabulas.parser

import java.io.{BufferedReader, Reader}
import java.net.{URI, URISyntaxException}
import java.util.{Objects, StringTokenizer}

import de.tudresden.inf.lat.tabulas.datatype._
import de.tudresden.inf.lat.tabulas.table._

import scala.annotation.tailrec
import scala.collection.mutable
import scala.collection.mutable.ArrayBuffer
import scala.util.Try

/** Parser of a table in simple format.
 *
 */
case class SimpleFormatParser(permissive: Boolean) extends Parser {

  def getKeyLength(line: String): Int = {
    val result = if (Objects.isNull(line)) {
      0
    } else {
      val posA = line.indexOf(ParserConstant.ColonFieldSign)
      val posB = line.indexOf(ParserConstant.EqualsFieldSign)
      val res = if (posA == -1 && posB == -1) {
        line.length
      } else {
        val pos = if (posA == -1) {
          posB
        } else if (posB == -1) {
          posA
        } else {
          Math.min(posA, posB)
        }
        pos
      }
      res
    }
    result
  }

  def getKey(line: String): Option[String] = {
    val result = if (Objects.isNull(line)) {
      None
    } else {
      Some(line.substring(0, getKeyLength(line)).trim())
    }
    result
  }

  def hasKey(line: String, key: String): Boolean = {
    val optKey = getKey(line)
    optKey.isDefined && (optKey.get == key)
  }

  def getValue(line: String): Option[String] = {
    val result = if (Objects.isNull(line)) {
      None
    } else {
      Some(line.substring(getKeyLength(line) + ParserConstant.EqualsFieldSign.length(), line.length()).trim())
    }
    result
  }

  def parseTypes(line: String, lineCounter: Int): CompositeTypeImpl = {
    var result = CompositeTypeImpl()
    val stok = new StringTokenizer(getValue(line).get)
    val factory = PrimitiveTypeFactory()
    while (stok.hasMoreTokens) {
      val token = stok.nextToken()
      val pos = token.indexOf(ParserConstant.TypeSign)
      if (pos == -1) {
        throw ParseException("Field '" + line + "' does not have a type. (line " + lineCounter + ")")
      } else {
        val key = token.substring(0, pos)
        val value = token.substring(pos + ParserConstant.TypeSign.length(), token.length())
        if (factory.contains(value)) {
          result = result.declareField(key, value).get
        } else {
          throw ParseException("Type '" + value + "' is undefined. (line " + lineCounter + ")")
        }
      }
    }
    result
  }

  def parsePrefixMap(line: String, lineCounter: Int): PrefixMap = {
    val mapOfUris = mutable.HashMap[URI, URI]()
    val listOfUris = mutable.ArrayBuffer[URI]()
    val stok = new StringTokenizer(getValue(line).get)
    while (stok.hasMoreTokens) {
      val token = stok.nextToken()
      val pos = token.indexOf(ParserConstant.PrefixSign)
      if (pos == -1) {
        throw ParseException("Prefix '" + line + "' does not have a definition. (line " + lineCounter + ")")
      } else {
        val key = token.substring(0, pos)
        val value = token.substring(pos + ParserConstant.PrefixSign.length(), token.length())
        val keyPair = asUri(key, lineCounter)
        val valuePair = asUri(value, lineCounter)
        mapOfUris.put(keyPair, valuePair)
        listOfUris += keyPair
      }
    }
    PrefixMapImpl(mapOfUris.toMap, listOfUris.toSeq)
  }

  def getTypedValue(key: String, value: String, tableType: CompositeType, prefixMap: PrefixMap, lineCounter: Int): PrimitiveTypeValue = {
    var result: PrimitiveTypeValue = StringValue()
    if (Objects.isNull(key)) {
      result = StringValue()
    } else {
      try {
        val optTypeStr = tableType.getFieldType(key)
        optTypeStr match {
          case Some(typeStr) =>
            val optPrimType = PrimitiveTypeFactory().getType(typeStr)
            val primType = optPrimType.get // caught by the try
            if (primType == URIType()) {
              val uri = URI.create(value)
              result = new URIValue(prefixMap.getWithoutPrefix(uri))

            } else if (primType.isList) {
              val param = primType.asInstanceOf[ParameterizedListType].getParameter
              val lines = value.split(ParserConstant.NewLine)
              if (param == URIType()) {
                val newList = lines.map(elem => {
                  val uri = URI.create(elem)
                  new URIValue(prefixMap.getWithoutPrefix(uri))
                })
                result = ParameterizedListValue(URIType(), newList.toIndexedSeq)
              } else {
                val newList = lines.map(x => param.parse(x))
                result = ParameterizedListValue(param, newList.toIndexedSeq)
              }

            } else {
              result = primType.parse(value)
            }

          case None =>
            if (permissive) {
              result = StringType().parse(value)
            } else {
              throw ParseException("Key '" + key + "' has an undefined type.")
            }
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
    var lineCounter = lineCounter0
    var result = Pair(lineCounter, None)
    var line = input.readLine()
    if (Objects.isNull(line)) {
      result = Pair(lineCounter, None)
    } else {
      lineCounter += 1
      if (line.startsWith(ParserConstant.CommentSymbol)) {
        result = Pair(lineCounter, Some(""))
      } else {
        val multiline = ArrayBuffer[String]()
        while (isMultiLine(line)) {
          multiline += getCleanLine(line)
          line = input.readLine()
          if (Objects.nonNull(line)) {
            lineCounter += 1
          }
        }
        multiline += getCleanLine(line)

        result = Pair(lineCounter, Some(multiline.mkString(ParserConstant.NewLine).toString))
      }
    }
    result
  }

  def getMultiLines(input: BufferedReader): Seq[Pair] = {
    getMultiLines(input, Seq(), Pair(0, Some("")))
  }

  // scalastyle:off
  def parseMap(input: BufferedReader): TableMapImpl = {
    val mapOfTables = new mutable.TreeMap[String, TableImpl]()
    val mapOfRecordIdsOfTables = new mutable.TreeMap[String, mutable.TreeSet[String]]()

    var tableName = ""
    var currentTable = TableImpl()
    var recordIdsOfCurrentTable = mutable.TreeSet[String]()
    var optCurrentId: Option[String] = None
    var record = RecordImpl()
    var lineCounter = 0
    var isDefiningType = false
    var mustAddRecord = false

    val pairs = getMultiLines(input)
    pairs.foreach(pair => {
      lineCounter = pair.lineCounter
      if (pair.line.isDefined && !pair.line.get.trim().isEmpty) {
        val line = pair.line.get
        if (hasKey(line, ParserConstant.TypeSelectionToken)) {
          if (mustAddRecord) {
            currentTable = currentTable.add(record)
            mapOfTables.put(tableName, currentTable)
            mustAddRecord = false
          }
          isDefiningType = true
        }
        if (isDefiningType &&
          (hasKey(line, ParserConstant.TypeNameToken) ||
            hasKey(line, ParserConstant.TypeSelectionToken))) {
          val optTableName = getValue(line)
          if (optTableName.isDefined && optTableName.get.trim.nonEmpty) {
            tableName = optTableName.get
            if (!mapOfTables.contains(tableName)) {
              mapOfTables.put(tableName, TableImpl())
              mapOfRecordIdsOfTables.put(tableName, new mutable.TreeSet[String]())
            }
            currentTable = mapOfTables(tableName)
            recordIdsOfCurrentTable = mapOfRecordIdsOfTables(tableName)
          }

        } else if (isDefiningType && hasKey(line, ParserConstant.TypeDefinitionToken)) {
          val tableType = parseTypes(line, lineCounter)
          currentTable = TableImpl(tableType, currentTable)
          mapOfTables.put(tableName, currentTable)

        } else if (isDefiningType && hasKey(line, ParserConstant.PrefixMapToken)) {
          currentTable = currentTable.copy(prefixMap = parsePrefixMap(line, lineCounter))
          mapOfTables.put(tableName, currentTable)

        } else if (isDefiningType && hasKey(line, ParserConstant.SortingOrderDeclarationToken)) {
          currentTable = setSortingOrder(line, currentTable)
          mapOfTables.put(tableName, currentTable)

        } else if (hasKey(line, ParserConstant.NewRecordToken)) {
          isDefiningType = false
          if (mustAddRecord) {
            currentTable = currentTable.add(record)
            mapOfTables.put(tableName, currentTable)
          }
          record = RecordImpl()
          mustAddRecord = true
          optCurrentId = None

        } else {
          record = parseProperty(line, currentTable, recordIdsOfCurrentTable, record, lineCounter)
          if (permissive) {
            val declaredFields = currentTable.tableType.getFields
            record.getMap.keySet
              .filter(newKey => !declaredFields.contains(newKey))
              .foreach(newKey => {
                val oldType = CompositeTypeImpl(currentTable.tableType)
                val newType = oldType.declareField(newKey, StringType().getTypeName).get
                currentTable = TableImpl(newType, currentTable)
              })
          }
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
    })
    if (mustAddRecord) {
      currentTable = currentTable.add(record)
      mapOfTables.put(tableName, currentTable)
      mustAddRecord = false
    }

    TableMapImpl(mapOfTables.toMap)
  }

  override def parse(input: Reader): Try[TableMapImpl] = Try {
    parseMap(new BufferedReader(input))
  }

  @tailrec
  private def getMultiLines(input: BufferedReader, elements: Seq[Pair], lastPair: Pair): Seq[Pair] = {
    if (lastPair.line.isEmpty) {
      elements
    } else {
      val lineCounter = lastPair.lineCounter
      val newPair = readMultiLine(input, lineCounter)
      getMultiLines(input, elements ++ Seq(lastPair), newPair)
    }
  }

  private def asUri(uriStr: String, lineCounter: Int): URI = {
    val result = try {
      new URI(uriStr)
    } catch {
      case e: URISyntaxException => throw ParseException("String '" + uriStr + "' is not a valid URI. (line " + lineCounter + ")")
    }
    result
  }

  private def setSortingOrder(line: String, table: TableImpl): TableImpl = {
    var result = table
    val fieldsWithReverseOrder = new mutable.TreeSet[String]()
    val list = new mutable.ArrayBuffer[String]
    val stok = new StringTokenizer(getValue(line).get)
    while (stok.hasMoreTokens) {
      var token = stok.nextToken()
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
    result = result.copy(sortingOrder = list.toSeq)
    result = result.copy(fieldsWithReverseOrder = fieldsWithReverseOrder.toSet)
    result
  }

  private def isIdProperty(line: String): Boolean = {
    val optKey = getKey(line)
    val result = if (optKey.isDefined) {
      optKey.get.equals(ParserConstant.IdKeyword)
    } else {
      false
    }
    result
  }

  private def getIdProperty(line: String): Option[String] = {
    val optKey = getKey(line)
    val optValueStr = getValue(line)
    val result = if (optKey.isDefined && optValueStr.isDefined && optKey.get.equals(ParserConstant.IdKeyword)) {
      Some(optValueStr.get)
    } else {
      None
    }
    result
  }

  private def parseProperty(line: String, currentTable: TableImpl,
                            recordIdsOfCurrentTable: mutable.TreeSet[String], record: RecordImpl, lineCounter: Int): RecordImpl = {
    if (Objects.isNull(currentTable)) {
      throw ParseException("New record was not declared (line "
        + lineCounter + ")")
    }

    val optKey = getKey(line)
    val optValueStr = getValue(line)
    val result = if (optKey.isDefined && optValueStr.isDefined) {
      val key = optKey.get
      val valueStr = optValueStr.get
      val value = getTypedValue(key, valueStr, currentTable.getType, currentTable.getPrefixMap, lineCounter)
      if (key.equals(ParserConstant.IdKeyword)) {
        if (recordIdsOfCurrentTable.contains(valueStr)) {
          throw ParseException("Identifier '"
            + ParserConstant.IdKeyword + ParserConstant.Space
            + ParserConstant.ColonFieldSign + ParserConstant.Space
            + valueStr + "' is duplicated (line " + lineCounter
            + ").")
        }
      }
      record.set(key, value)
    } else {
      record
    }
    result
  }

  // scalastyle:on

  case class Pair(lineCounter: Int, line: Option[String])

}

object SimpleFormatParser {

  def apply(): SimpleFormatParser = SimpleFormatParser(permissive = false)

}
