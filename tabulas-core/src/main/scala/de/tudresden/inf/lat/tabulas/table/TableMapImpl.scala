
package de.tudresden.inf.lat.tabulas.table

import scala.collection.mutable
import scala.collection.mutable.{ArrayBuffer, Map, TreeMap}

/**
  * This is the default implementation of a table map.
  *
  */
class TableMapImpl extends TableMap {

  private val map: Map[String, Table] = new TreeMap[String, Table]()

  /**
    * Constructs a new table map using another one.
    *
    * @param otherTableMap
    * other table map
    */
  def this(otherTableMap: TableMap) = {
    this()
    otherTableMap.getTableIds.foreach(tableId => put(tableId, otherTableMap.getTable(tableId).get))
  }

  override def getTableIds(): mutable.Buffer[String] = {
    val ret: mutable.Buffer[String] = new ArrayBuffer[String]()
    ret ++= this.map.keySet
    return ret
  }

  override def put(id: String, table: Table): Option[Table] = {
    return this.map.put(id, table)
  }

  override def getTable(id: String): Option[Table] = {
    return this.map.get(id)
  }

  override def hashCode(): Int = {
    return this.map.hashCode()
  }

  override def equals(obj: Any): Boolean = {
    obj match {
      case other: TableMap => {
        var ret: Boolean = getTableIds().equals(other.getTableIds)
        val tableIds: mutable.Buffer[String] = getTableIds()
        ret = ret && tableIds.forall(tableId => getTable(tableId).equals(other.getTable(tableId)))
        return ret
      }
      case _ => return false
    }
  }

  override def toString: String = {
    val sbuf: StringBuffer = new StringBuffer()
    val tableIds: mutable.Buffer[String] = getTableIds()
    tableIds.foreach(tableId => {
      sbuf.append(tableId)
      sbuf.append("=")
      sbuf.append(getTable(tableId))
      sbuf.append("\n")
    })
    return sbuf.toString
  }

}

