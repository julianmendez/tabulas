
package de.tudresden.inf.lat.tabulas.table

import scala.collection.mutable.ArrayBuffer
import scala.collection.mutable
import scala.collection.mutable.Map
import java.util.Objects
import scala.collection.mutable.TreeMap

import scala.collection.JavaConverters._
import scala.collection.JavaConverters.asScalaBufferConverter

import de.tudresden.inf.lat.tabulas.datatype.PrimitiveTypeValue
import de.tudresden.inf.lat.tabulas.datatype.Record

/**
 * This is the default implementation of a record.
 *
 */
class RecordImpl extends Record {

  private val map: Map[String, PrimitiveTypeValue] = new TreeMap[String, PrimitiveTypeValue]()

  /**
   * Constructs a new record using another one.
   *
   * @param otherRecord
   *            other record
   */
  def this(otherRecord: Record) = {
    this()
    otherRecord.getProperties().foreach(property => set(property, otherRecord.get(property).get))
  }

  override def get(key: String): Option[PrimitiveTypeValue] = {
    if (Objects.isNull(key)) {
      return Option.empty
    } else {
      val optValue: Option[PrimitiveTypeValue] = this.map.get(key)
      if (optValue.isEmpty) {
        return Option.empty
      } else {
        return Option.apply(optValue.get)
      }
    }
  }

  override def set(key: String, value: PrimitiveTypeValue): Unit = {
    if (Objects.nonNull(key)) {
      this.map.put(key, value)
    }
  }

  override def getProperties(): mutable.Buffer[String] = {
    val ret: mutable.Buffer[String] = new ArrayBuffer[String]
    ret ++= map.keySet
    return ret
  }

  override def equals(o: Any): Boolean = {
    if (o.isInstanceOf[Record]) {
      val other: Record = o.asInstanceOf[Record]
      var ret: Boolean = getProperties().equals(other.getProperties())
      ret = ret && getProperties().forall(property => get(property).equals(other.get(property)))
      return ret
    } else {
      return false
    }
  }

  override def hashCode(): Int = {
    return this.map.hashCode()
  }

  override def toString(): String = {
    return this.map.toString()
  }

}

