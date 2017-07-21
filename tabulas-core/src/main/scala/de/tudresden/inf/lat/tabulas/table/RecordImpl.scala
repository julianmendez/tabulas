
package de.tudresden.inf.lat.tabulas.table

import java.util.Objects

import de.tudresden.inf.lat.tabulas.datatype.{PrimitiveTypeValue, Record}

import scala.collection.mutable
import scala.collection.mutable.ArrayBuffer

/**
  * This is the default implementation of a record.
  *
  */
class RecordImpl extends Record {

  private val map: mutable.Map[String, PrimitiveTypeValue] = new mutable.TreeMap[String, PrimitiveTypeValue]()

  /**
    * Constructs a new record using another one.
    *
    * @param otherRecord
    * other record
    */
  def this(otherRecord: Record) = {
    this()
    otherRecord.getProperties.foreach(property => set(property, otherRecord.get(property).get))
  }

  override def get(key: String): Option[PrimitiveTypeValue] = {
    if (Objects.isNull(key)) {
      return Option.empty
    } else {
      return this.map.get(key)
    }
  }

  override def set(key: String, value: PrimitiveTypeValue): Unit = {
    if (Objects.nonNull(key)) {
      this.map.put(key, value)
    }
  }

  override def getProperties: mutable.Buffer[String] = {
    val ret: mutable.Buffer[String] = new ArrayBuffer[String]
    ret ++= map.keySet
    return ret
  }

  override def equals(obj: Any): Boolean = {
    obj match {
      case other: Record => {
        var ret: Boolean = getProperties.equals(other.getProperties)
        ret = ret && getProperties.forall(property => get(property).equals(other.get(property)))
        return ret
      }
      case _ => return false
    }
  }

  override def hashCode(): Int = {
    return this.map.hashCode()
  }

  override def toString: String = {
    return this.map.toString
  }

}

