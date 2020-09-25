
package de.tudresden.inf.lat.tabulas.table

/** This is the default implementation of a table map.
 *
 */
case class TableMapImpl(mapOfTables: Map[String, Table]) extends TableMap {

  override val getTableIds: Seq[String] = mapOfTables.keySet.toSeq

  override val toString: String = {
    getTableIds.map(tableId => tableId + "=" + getTable(tableId) + "\n")
      .mkString
  }

  def put(id: String, table: Table): TableMapImpl = {
    TableMapImpl(mapOfTables ++ Seq((id, table)))
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
  def apply(otherTableMap: TableMap): TableMapImpl = {
    val result = otherTableMap match {

      case otherTableMapImpl: TableMapImpl =>
        TableMapImpl(otherTableMapImpl.mapOfTables)

      case _ =>
        val mapOfTables = otherTableMap
          .getTableIds
          .map(tableId => (tableId, otherTableMap.getTable(tableId).get))
          .toMap
        TableMapImpl(mapOfTables)
    }
    result
  }

}
