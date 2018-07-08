
package de.tudresden.inf.lat.tabulas.table

import java.util.Objects

import de.tudresden.inf.lat.tabulas.datatype.{PrimitiveTypeValue, Record}

import scala.collection.mutable
import scala.collection.mutable.ArrayBuffer

/** This is the default implementation of a record.
  *
  */
class RecordImpl extends Record {

  private val _map: mutable.Map[String, PrimitiveTypeValue] = new mutable.TreeMap[String, PrimitiveTypeValue]()

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
    var result: Option[PrimitiveTypeValue] = Option.empty
    if (Objects.isNull(key)) {
      result = Option.empty
    } else {
      result = this._map.get(key)
    }
    result
  }

  override def set(key: String, value: PrimitiveTypeValue): Unit = {
    if (Objects.nonNull(key)) {
      this._map.put(key, value)
    }
  }

  override def getProperties: mutable.Buffer[String] = {
    val result: mutable.Buffer[String] = new ArrayBuffer[String]
    result ++= this._map.keySet
    result
  }

  override def equals(obj: Any): Boolean = {
    var result: Boolean = false
    obj match {
      case other: Record =>
        result = getProperties.equals(other.getProperties)
        result = result && getProperties.forall(property => get(property).equals(other.get(property)))
      case _ => result = false
    }
    result
  }

  override def hashCode(): Int = { this._map.hashCode() }

  override def toString: String = { this._map.toString }

}

