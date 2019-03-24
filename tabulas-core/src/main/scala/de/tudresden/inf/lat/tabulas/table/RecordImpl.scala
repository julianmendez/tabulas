
package de.tudresden.inf.lat.tabulas.table

import java.util.Objects

import de.tudresden.inf.lat.tabulas.datatype.{PrimitiveTypeValue, Record}

import scala.collection.mutable

/** This is the default implementation of a record.
  *
  */
case class RecordImpl(map: mutable.Map[String, PrimitiveTypeValue]) extends Record {

  override def get(key: String): Option[PrimitiveTypeValue] = {
    val result = if (Objects.isNull(key)) {
      None
    } else {
      map.get(key)
    }
    result
  }

  override def getMap: Map[String, PrimitiveTypeValue] = map.toMap

  def set(key: String, value: PrimitiveTypeValue): RecordImpl = {
    if (Objects.nonNull(key)) {
      map.put(key, value)
    }
    this
  }

  override def getProperties: Seq[String] = {
    val result = new mutable.ArrayBuffer[String]
    result ++= map.keySet
    result
  }

  override def toString: String = {
    map.toString
  }

}

object RecordImpl {

  def apply(): RecordImpl = new RecordImpl(mutable.TreeMap[String, PrimitiveTypeValue]())

  /** Constructs a new record using another one.
    *
    * @param otherRecord
    * other record
    */
  def apply(otherRecord: Record): RecordImpl = {
    val newMap = otherRecord.getProperties
      .map(property => (property, otherRecord.get(property).get))
      .toMap
    val mutableMap = mutable.TreeMap[String, PrimitiveTypeValue]() ++ newMap
    RecordImpl(mutableMap)
  }

}
