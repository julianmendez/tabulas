
package de.tudresden.inf.lat.tabulas.table

/** This models a collection of tables with identifiers.
  *
  */
trait TableMap {

  /** Returns the identifiers of the stored tables.
    *
    * @return the identifiers of the stored tables
    */
  def getTableIds: Seq[String]

  /** Stores a table with the given identifier.
    *
    * @param id
    * identifier
    * @param table
    * table
    * @return an optional containing the previous value associated to the given
    *         key, or an empty optional if there was no association before
    */
  def put(id: String, table: Table): Option[Table]

  /** Returns an optional containing the value associated to the given key, or
    * an empty optional if there is no association.
    *
    * @param id
    * identifier
    * @return an optional containing the value associated to the given key, or
    *         an empty optional if there is no association
    */
  def getTable(id: String): Option[Table]

}

