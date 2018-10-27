
package de.tudresden.inf.lat.tabulas.table

import java.util.Objects

import de.tudresden.inf.lat.tabulas.datatype.{PrimitiveTypeValue, Record}

import scala.collection.mutable

/** This is the default implementation of a record.
  *
  */
class RecordImpl extends Record {

  private val _map = new mutable.TreeMap[String, PrimitiveTypeValue]()

  /** Constructs a new record using another one.
    *
    * @param otherRecord
    * other record
    */
  def this(otherRecord: Record) = {
    this()
    otherRecord.getProperties.foreach(property => set(property, otherRecord.get(property).get))
  }

  override def get(key: String): Option[PrimitiveTypeValue] = {
    val result = if (Objects.isNull(key)) {
      None
    } else {
      this._map.get(key)
    }
    result
  }

  override def set(key: String, value: PrimitiveTypeValue): Unit = {
    if (Objects.nonNull(key)) {
      this._map.put(key, value)
    }
  }

  override def getProperties: Seq[String] = {
    val result = new mutable.ArrayBuffer[String]
    result ++= this._map.keySet
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
    this._map.hashCode()
  }

  override def toString: String = {
    this._map.toString
  }

}

object RecordImpl {

  def apply(): RecordImpl = new RecordImpl

}
