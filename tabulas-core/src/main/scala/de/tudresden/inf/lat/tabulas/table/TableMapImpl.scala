
package de.tudresden.inf.lat.tabulas.table

import java.util.ArrayList
import java.util.List
import java.util.TreeMap

import scala.collection.JavaConversions.asScalaBuffer

/**
 * This is the default implementation of a table map.
 *
 */
class TableMapImpl extends TableMap {

  private val map: TreeMap[String, Table] = new TreeMap[String, Table]()

  /**
   * Returns the identifiers of the stored tables.
   *
   * @return the identifiers of the stored tables
   */
  def getTableIds(): List[String] = {
    val ret: ArrayList[String] = new ArrayList[String]()
    ret.addAll(this.map.keySet())
    ret
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
    this.map.get(id)
  }

  override def hashCode(): Int = {
    this.map.hashCode()
  }

  override def equals(obj: Any): Boolean = {
    if (this == obj) {
      true
    } else if (obj.isInstanceOf[TableMap]) {
      val other: TableMap = obj.asInstanceOf[TableMap]
      var ret: Boolean = getTableIds().equals(other.getTableIds())
      val tableIds: List[String] = getTableIds()
      for (tableId: String <- tableIds) {
        ret = ret && getTable(tableId).equals(other.getTable(tableId))
      }
      ret
    } else {
      false
    }
  }

  override def toString(): String = {
    val sbuf: StringBuffer = new StringBuffer()
    val tableIds: List[String] = getTableIds()
    for (tableId: String <- tableIds) {
      sbuf.append(tableId)
      sbuf.append("=")
      sbuf.append(getTable(tableId))
      sbuf.append("\n")
    }
    sbuf.toString()
  }

}

