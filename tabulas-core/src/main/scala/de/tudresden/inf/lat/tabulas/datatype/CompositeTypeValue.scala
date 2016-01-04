
package de.tudresden.inf.lat.tabulas.datatype

import java.util.List

/**
 * This models a composite type value.
 *
 */
trait CompositeTypeValue {

  /**
   * Returns the type of this table.
   *
   * @return the type of this table
   */
  def getType(): CompositeType

  /**
   * Returns all the records.
   *
   * @return all the records
   */
  def getRecords(): List[Record]

}

