
package de.tudresden.inf.lat.tabulas.table

import java.util.ArrayList
import java.util.List
import java.util.Map
import java.util.TreeMap

import scala.collection.JavaConversions.asScalaBuffer

import de.tudresden.inf.lat.tabulas.datatype.PrimitiveTypeValue
import de.tudresden.inf.lat.tabulas.datatype.Record

/**
 * This is the default implementation of a record.
 *
 */
class RecordImpl extends Record {

  private val map: Map[String, PrimitiveTypeValue] = new TreeMap[String, PrimitiveTypeValue]()

  override def get(key: String): PrimitiveTypeValue = {
    if (key == null) { null } else { this.map.get(key) }
  }

  override def set(key: String, value: PrimitiveTypeValue): Unit = {
    if (key != null) {
      this.map.put(key, value)
    }
  }

  override def getProperties(): List[String] = {
    val ret: List[String] = new ArrayList[String]
    ret.addAll(map.keySet())
    ret
  }

  override def equals(o: Any): Boolean = {
    if (this == o) {
      true
    } else if (o.isInstanceOf[Record]) {
      val other: Record = o.asInstanceOf[Record]
      var ret: Boolean = getProperties().equals(other.getProperties())
      for (property: String <- getProperties()) {
        ret = ret && get(property).equals(other.get(property))
      }
      ret
    } else {
      false
    }
  }

  override def hashCode(): Int = {
    this.map.hashCode()
  }

  override def toString(): String = {
    this.map.toString()
  }

}


