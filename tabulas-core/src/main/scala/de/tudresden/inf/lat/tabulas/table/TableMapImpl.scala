
package de.tudresden.inf.lat.tabulas.table

import scala.collection.mutable

/** This is the default implementation of a table map.
  *
  */
case class TableMapImpl(mapOfTables: Map[String, Table]) extends TableMap {

  def put(id: String, table: Table): TableMapImpl = {
    TableMapImpl(mapOfTables ++ Seq((id, table)))
  }

  override def toString: String = {
    getTableIds.map(tableId => tableId + "=" + getTable(tableId) + "\n")
      .mkString
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
