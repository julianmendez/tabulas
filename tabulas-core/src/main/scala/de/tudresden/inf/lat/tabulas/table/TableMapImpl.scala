
package de.tudresden.inf.lat.tabulas.table

import scala.collection.mutable
import scala.collection.mutable.ArrayBuffer

/** This is the default implementation of a table map.
  *
  */
class TableMapImpl extends TableMap {

  private val _map: mutable.Map[String, Table] = new mutable.TreeMap[String, Table]()

  /** Constructs a new table map using another one.
    *
    * @param otherTableMap
    * other table map
    */
  def this(otherTableMap: TableMap) = {
    this()
    otherTableMap.getTableIds.foreach(tableId => put(tableId, otherTableMap.getTable(tableId).get))
  }

  override def getTableIds: mutable.Buffer[String] = {
    val result: mutable.Buffer[String] = new ArrayBuffer[String]()
    result ++= this._map.keySet
    result
  }

  override def put(id: String, table: Table): Option[Table] = { this._map.put(id, table) }

  override def getTable(id: String): Option[Table] = { this._map.get(id) }

  override def hashCode(): Int = { this._map.hashCode() }

  override def equals(obj: Any): Boolean = {
    var result: Boolean = false
    obj match {
      case other: TableMap =>
        result = getTableIds.equals(other.getTableIds)
        val tableIds: mutable.Buffer[String] = getTableIds
        result = result && tableIds.forall(tableId => getTable(tableId).equals(other.getTable(tableId)))
      case _ => result = false
    }
    result
  }

  override def toString: String = {
    val sbuf: StringBuffer = new StringBuffer()
    val tableIds: mutable.Buffer[String] = getTableIds
    tableIds.foreach(tableId => {
      sbuf.append(tableId)
      sbuf.append("=")
      sbuf.append(getTable(tableId))
      sbuf.append("\n")
    })
    val result: String = sbuf.toString
    result
  }

}

