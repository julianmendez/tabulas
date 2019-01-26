package de.tudresden.inf.lat.tabulas.ext.renderer

import de.tudresden.inf.lat.tabulas.datatype.{ParameterizedListValue, PrimitiveTypeValue, Record, StringType, StringValue}
import de.tudresden.inf.lat.tabulas.parser.ParserConstant
import de.tudresden.inf.lat.tabulas.table.{RecordImpl, Table}

import scala.collection.mutable

/** This helps in the serialization of metadata.
  */
class MetadataHelper {

  private def getTypeEntry(typeName: String): PrimitiveTypeValue = {
    new StringValue(typeName)
  }

  private def getDefEntry(table: Table): PrimitiveTypeValue = {
    val list = table.getType.getFields
      .map(key => key + ParserConstant.TypeSign + table.getType.getFieldType(key).get)
      .map(x => new StringValue(x))
    new ParameterizedListValue(StringType(), list)
  }

  private def getPrefixEntry(table: Table): PrimitiveTypeValue = {
    val list = table.getPrefixMap.getKeysAsStream
      .map(key => key + ParserConstant.TypeSign + table.getPrefixMap.get(key).get)
      .map(x => new StringValue(x))
    new ParameterizedListValue(StringType(), list)
  }

  private def getOrderEntry(table: Table): PrimitiveTypeValue = {
    val list = table.getSortingOrder
      .map(elem => {
        val prefix = if (table.getFieldsWithReverseOrder.contains(elem)) {
          ParserConstant.ReverseOrderSign
        } else {
          ParserConstant.StandardOrderSign
        }
        prefix + elem
      })
      .map(x => StringValue(x))
    new ParameterizedListValue(StringType(), list)
  }

  def getMetadataAsRecord(typeName: String, table: Table): Record = {
    val map = new mutable.HashMap[String, PrimitiveTypeValue]
    map.put(ParserConstant.TypeSelectionToken, getTypeEntry(typeName))
    map.put(ParserConstant.TypeDefinitionToken, getDefEntry(table))
    map.put(ParserConstant.PrefixMapToken, getPrefixEntry(table))
    map.put(ParserConstant.SortingOrderDeclarationToken, getOrderEntry(table))
    new RecordImpl(map)
  }

}

object MetadataHelper {

  final val MetadataTokens = Seq(
    ParserConstant.TypeSelectionToken,
    ParserConstant.TypeDefinitionToken,
    ParserConstant.PrefixMapToken,
    ParserConstant.SortingOrderDeclarationToken
  )

  def apply(): MetadataHelper = new MetadataHelper

}
