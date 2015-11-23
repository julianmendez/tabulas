
package de.tudresden.inf.lat.tabulas.table

import java.util.List
import java.util.Set

import de.tudresden.inf.lat.tabulas.datatype._

/**
 * This models a sorted table.
 *
 */
trait Table extends CompositeTypeValue {

  /**
   * Returns the sorting order of the fields.
   *
   * @return the sorting order of the fields
   */
  def getSortingOrder(): List[String]

  /**
   * Returns the fields that are supposed to be sorted in reverse order.
   *
   * @return the fields that are supposed to be sorted in reverse order
   */
  def getFieldsWithReverseOrder(): Set[String]

  /**
   * Returns the identifiers of all added records.
   *
   * @return the identifiers of all added records
   */
  def getIdentifiers(): Set[String]

}


