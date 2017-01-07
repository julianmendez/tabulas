
package de.tudresden.inf.lat.tabulas.table

import java.util.ArrayList
import java.util.List
import java.util.Map
import java.util.TreeMap

import scala.collection.JavaConverters.asScalaBufferConverter

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
   *            other table map
   */
  def this(otherTableMap: TableMap) = {
    this()
    otherTableMap.getTableIds().asScala.foreach(tableId => put(tableId, otherTableMap.getTable(tableId)))
  }

  /**
   * Returns the identifiers of the stored tables.
   *
   * @return the identifiers of the stored tables
   */
  def getTableIds(): List[String] = {
    val ret: List[String] = new ArrayList[String]()
    ret.addAll(this.map.keySet())
    return ret
  }

  /**
   * Stores a table with the given identifier.
   *
   * @param id
   *            identifier
   * @param table
   *            table
   */
  def put(id: String, table: Table): Unit = {
    this.map.put(id, table)
  }

  /**
   * Returns the table associated to the given identifier.
   *
   * @param id
   *            identifier
   * @return the table associated to the given identifier
   */
  def getTable(id: String): Table = {
    return this.map.get(id)
  }

  override def hashCode(): Int = {
    return this.map.hashCode()
  }

  override def equals(obj: Any): Boolean = {
    if (obj.isInstanceOf[TableMap]) {
      val other: TableMap = obj.asInstanceOf[TableMap]
      var ret: Boolean = getTableIds().equals(other.getTableIds())
      val tableIds: List[String] = getTableIds()
      ret = ret && tableIds.asScala.forall(tableId => getTable(tableId).equals(other.getTable(tableId)))
      return ret
    } else {
      return false
    }
  }

  override def toString(): String = {
    val sbuf: StringBuffer = new StringBuffer()
    val tableIds: List[String] = getTableIds()
    tableIds.asScala.foreach(tableId => {
      sbuf.append(tableId)
      sbuf.append("=")
      sbuf.append(getTable(tableId))
      sbuf.append("\n")
    })
    return sbuf.toString()
  }

}

