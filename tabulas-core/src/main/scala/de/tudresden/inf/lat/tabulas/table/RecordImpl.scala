
package de.tudresden.inf.lat.tabulas.table

import java.util.Objects

import de.tudresden.inf.lat.tabulas.datatype.{PrimitiveTypeValue, Record}

import scala.collection.mutable

/** This is the default implementation of a record.
  *
  */
class RecordImpl(map: mutable.Map[String, PrimitiveTypeValue]) extends Record {

  override def get(key: String): Option[PrimitiveTypeValue] = {
    val result = if (Objects.isNull(key)) {
      None
    } else {
      this.map.get(key)
    }
    result
  }

  override def set(key: String, value: PrimitiveTypeValue): Unit = {
    if (Objects.nonNull(key)) {
      this.map.put(key, value)
    }
  }

  override def getProperties: Seq[String] = {
    val result = new mutable.ArrayBuffer[String]
    result ++= this.map.keySet
    result
  }

  override def equals(obj: Any): Boolean = {
    val result = obj match {
      case other: Record =>
        getProperties.equals(other.getProperties) &&
          getProperties.forall(property => get(property).equals(other.get(property)))
      case _ => false
    }
    result
  }

  override def hashCode(): Int = {
    this.map.hashCode()
  }

  override def toString: String = {
    this.map.toString
  }

}

object RecordImpl {

  def apply(): RecordImpl = new RecordImpl(mutable.TreeMap[String, PrimitiveTypeValue]())

  def apply(map: mutable.Map[String, PrimitiveTypeValue]): RecordImpl = new RecordImpl(map)


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
