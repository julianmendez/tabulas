package de.tudresden.inf.lat.tabulas.renderer

import de.tudresden.inf.lat.tabulas.datatype._
import de.tudresden.inf.lat.tabulas.parser.ParserConstant
import de.tudresden.inf.lat.tabulas.table.{RecordImpl, Table}

import scala.collection.mutable

/** This helps in the serialization of metadata.
  */
case class MetadataHelper() {

  def getMetadataAsRecord(typeName: String, table: Table): RecordImpl = {
    val map = new mutable.HashMap[String, PrimitiveTypeValue]
    map.put(ParserConstant.TypeSelectionToken, getTypeEntry(typeName))
    map.put(ParserConstant.TypeNameToken, getNameEntry(typeName))
    map.put(ParserConstant.TypeDefinitionToken, getDefEntry(table))
    map.put(ParserConstant.PrefixMapToken, getPrefixEntry(table))
    map.put(ParserConstant.SortingOrderDeclarationToken, getOrderEntry(table))
    RecordImpl(map.toMap)
  }

  private def getTypeEntry(typeName: String): PrimitiveTypeValue = {
    StringValue(typeName)
  }

  private def getNameEntry(typeName: String): PrimitiveTypeValue = {
    StringValue(typeName)
  }

  private def getDefEntry(table: Table): PrimitiveTypeValue = {
    val list = table.getType.getFields
      .map(key => key + ParserConstant.TypeSign + table.getType.getFieldType(key).get)
      .map(x => StringValue(x))
    ParameterizedListValue(StringType(), list)
  }

  private def getPrefixEntry(table: Table): PrimitiveTypeValue = {
    val list = table.getPrefixMap.getKeys
      .map(key => key.toString + ParserConstant.TypeSign + table.getPrefixMap.get(key).get)
      .map(x => StringValue(x))
    ParameterizedListValue(StringType(), list)
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
    ParameterizedListValue(StringType(), list)
  }

}
