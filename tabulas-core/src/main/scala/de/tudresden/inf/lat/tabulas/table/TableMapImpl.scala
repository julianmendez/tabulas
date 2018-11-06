
package de.tudresden.inf.lat.tabulas.table

import scala.collection.mutable

/** This is the default implementation of a table map.
  *
  */
class TableMapImpl(map: mutable.TreeMap[String, Table]) extends TableMap {

  override def getTableIds: Seq[String] = {
    val result = new mutable.ArrayBuffer[String]()
    result ++= this.map.keySet
    result
  }

  override def put(id: String, table: Table): Option[Table] = { this.map.put(id, table) }

  override def getTable(id: String): Option[Table] = { this.map.get(id) }

  override def hashCode(): Int = { this.map.hashCode() }

  override def equals(obj: Any): Boolean = {
    val result = obj match {
      case other: TableMap =>
        getTableIds.equals(other.getTableIds) &&
          getTableIds.forall(tableId => getTable(tableId).equals(other.getTable(tableId)))
      case _ => false
    }
    result
  }

  override def toString: String = {
    val sbuf: StringBuffer = new StringBuffer()
    val tableIds: Seq[String] = getTableIds
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

object TableMapImpl {

  def apply(): TableMapImpl = new TableMapImpl(mutable.TreeMap[String, Table]())

  /** Constructs a new table map using another one.
    *
    * @param otherTableMap
    * other table map
    */
  def apply(otherTableMap: TableMap): TableMapImpl = {
    val map = mutable.TreeMap[String, Table]()
    otherTableMap.getTableIds
      .foreach(tableId => map.put(tableId, otherTableMap.getTable(tableId).get))
    new TableMapImpl(map)
  }

}
