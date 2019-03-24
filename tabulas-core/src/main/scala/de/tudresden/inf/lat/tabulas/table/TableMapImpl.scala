
package de.tudresden.inf.lat.tabulas.table

import scala.collection.mutable

/** This is the default implementation of a table map.
  *
  */
case class TableMapImpl(mapOfTables: Map[String, Table]) extends TableMap {

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
    result ++= mapOfTables.keySet
    result
  }

  override def getTable(id: String): Option[Table] = {
    mapOfTables.get(id)
  }

}

object TableMapImpl {

  def apply(): TableMapImpl = new TableMapImpl(Map())

  /** Constructs a new table map using another one.
    *
    * @param otherTableMap
    * other table map
    */
  def apply(otherTableMap: TableMapImpl): TableMapImpl = {
    TableMapImpl(otherTableMap.mapOfTables)
  }

}
