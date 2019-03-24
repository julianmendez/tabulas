
package de.tudresden.inf.lat.tabulas.parser

import java.io.{BufferedReader, IOException, InputStreamReader, Reader}
import java.net.{URI, URISyntaxException}
import java.util.{Objects, StringTokenizer}

import de.tudresden.inf.lat.tabulas.datatype._
import de.tudresden.inf.lat.tabulas.table._

import scala.annotation.tailrec
import scala.collection.mutable
import scala.collection.mutable.ArrayBuffer

/** Parser of a table in simple format.
  *
  */
case class SimpleFormatParser(input: Reader) extends Parser {

  def getKeyLength(line: String): Int = {
    val result = if (Objects.isNull(line)) {
      0
    } else {
      val posA: Int = line.indexOf(ParserConstant.ColonFieldSign)
      val posB: Int = line.indexOf(ParserConstant.EqualsFieldSign)
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
    val optKey: Option[String] = getKey(line)
    val result: Boolean = optKey.isDefined && (optKey.get == key)
    result
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

  def parsePrefixMap(line: String, lineCounter: Int): PrefixMap = {
    val mapOfUris = mutable.HashMap[URI, URI]()
    val listOfUris = mutable.ArrayBuffer[URI]()
    val stok: StringTokenizer = new StringTokenizer(getValue(line).get)
    while (stok.hasMoreTokens) {
      val token: String = stok.nextToken()
      val pos: Int = token.indexOf(ParserConstant.PrefixSign)
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
    val result = PrefixMapImpl(mapOfUris.toMap, listOfUris)
    result
  }

  def getTypedValue(key: String, value: String, type0: CompositeType, prefixMap: PrefixMap, lineCounter: Int): PrimitiveTypeValue = {
    var result: PrimitiveTypeValue = StringValue()
    if (Objects.isNull(key)) {
      result = StringValue()
    } else {
      try {
        val optTypeStr: Option[String] = type0.getFieldType(key)
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
                result = ParameterizedListValue(URIType(), newList)
              } else {
                val newList = lines.map(x => param.parse(x))
                result = ParameterizedListValue(param, newList)
              }

            } else {
              result = primType.parse(value)
            }

          case None =>
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
    var result: Pair = Pair(lineCounter, None)
    var line: String = input.readLine()
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
    var lineCounter: Int = 0
    var isDefiningType: Boolean = false

    val pairs = getMultiLines(input)
    pairs.foreach(pair => {
      lineCounter = pair.lineCounter
      if (pair.line.isDefined && !pair.line.get.trim().isEmpty) {
        val line = pair.line.get
        if (hasKey(line, ParserConstant.TypeSelectionToken)) {
          isDefiningType = true
        }
        if (isDefiningType &&
          (hasKey(line, ParserConstant.TypeNameToken) ||
            hasKey(line, ParserConstant.TypeSelectionToken))) {
          val optTableName: Option[String] = getValue(line)
          if (optTableName.isDefined && optTableName.get.trim.nonEmpty) {
            tableName = optTableName.get
            if (!mapOfTables.get(tableName).isDefined) {
              mapOfTables.put(tableName, TableImpl())
              mapOfRecordIdsOfTables.put(tableName, new mutable.TreeSet[String]())
            }
            currentTable = mapOfTables.get(tableName).get
            recordIdsOfCurrentTable = mapOfRecordIdsOfTables.get(tableName).get
          }

        } else if (isDefiningType && hasKey(line, ParserConstant.TypeDefinitionToken)) {
          val tableType = parseTypes(line, lineCounter)
          currentTable = TableImpl(tableType, currentTable)
          mapOfTables.put(tableName, currentTable)

        } else if (isDefiningType && hasKey(line, ParserConstant.PrefixMapToken)) {
          currentTable.setPrefixMap(parsePrefixMap(line, lineCounter))

        } else if (isDefiningType && hasKey(line, ParserConstant.SortingOrderDeclarationToken)) {
          setSortingOrder(line, currentTable)

        } else if (hasKey(line, ParserConstant.NewRecordToken)) {
          isDefiningType = false
          record = RecordImpl()
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
    })

    val result = TableMapImpl(mapOfTables.toMap)
    result
  }

  override def parse(): TableMapImpl = {
    val result = try {
      parseMap(new BufferedReader(this.input))

    } catch {
      case e: IOException => throw new RuntimeException(e)
    }
    result
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
    val result: URI = try {
      new URI(uriStr)
    } catch {
      case e: URISyntaxException => throw ParseException("String '" + uriStr + "' is not a valid URI. (line " + lineCounter + ")")
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
            + ParserConstant.EqualsFieldSign + ParserConstant.Space
            + valueStr + "' is duplicated (line " + lineCounter
            + ").")
        }
      }
      record.set(key, value)
    }
  }

  // scalastyle:on

  case class Pair(lineCounter: Int, line: Option[String])

}

object SimpleFormatParser {

  def apply(): SimpleFormatParser = new SimpleFormatParser(new InputStreamReader(System.in))

}
