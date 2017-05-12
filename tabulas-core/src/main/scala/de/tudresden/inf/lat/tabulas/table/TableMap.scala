
package de.tudresden.inf.lat.tabulas.table

import scala.collection.mutable

/**
 * This models a collection of tables with identifiers.
 *
 */
trait TableMap {

  /**
   * Returns the identifiers of the stored tables.
   *
   * @return the identifiers of the stored tables
   */
  def getTableIds(): mutable.Buffer[String]

  /**
   * Stores a table with the given identifier.
   *
   * @param id
   *            identifier
   * @param table
   *            table
   */
  def put(id: String, table: Table): Unit

  /**
   * Returns the table associated to the given identifier.
   *
   * @param id
   *            identifier
   * @return the table associated to the given identifier
   */
  def getTable(id: String): Table

}

