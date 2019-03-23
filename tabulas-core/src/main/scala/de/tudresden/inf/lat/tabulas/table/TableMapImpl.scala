
package de.tudresden.inf.lat.tabulas.table

import scala.collection.mutable

/** This is the default implementation of a table map.
  *
  */
case class TableMapImpl(map: mutable.TreeMap[String, Table]) extends TableMap {

  override def put(id: String, table: Table): Option[Table] = {
    map.put(id, table)
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

  override def getTableIds: Seq[String] = {
    val result = new mutable.ArrayBuffer[String]()
    result ++= map.keySet
    result
  }

  override def getTable(id: String): Option[Table] = {
    map.get(id)
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
