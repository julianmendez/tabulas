
package de.tudresden.inf.lat.tabulas.table

import java.util.Objects

import de.tudresden.inf.lat.tabulas.datatype.{PrimitiveTypeValue, Record}

import scala.collection.mutable

/** This is the default implementation of a record.
  *
  */
case class RecordImpl(map: Map[String, PrimitiveTypeValue]) extends Record {

  override def get(key: String): Option[PrimitiveTypeValue] = {
    val result = if (Objects.isNull(key)) {
      None
    } else {
      map.get(key)
    }
    result
  }

  override def getMap: Map[String, PrimitiveTypeValue] = map

  def set(key: String, value: PrimitiveTypeValue): RecordImpl = {
    RecordImpl(map ++ Seq((key, value)))
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

  def apply(): RecordImpl = RecordImpl(Map[String, PrimitiveTypeValue]())

  /** Constructs a new record using another one.
    *
    * @param otherRecord
    * other record
    */
  def apply(otherRecord: Record): RecordImpl = {
    RecordImpl(otherRecord.getMap)
  }

}
