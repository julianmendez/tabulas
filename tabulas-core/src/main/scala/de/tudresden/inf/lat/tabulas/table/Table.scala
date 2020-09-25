
package de.tudresden.inf.lat.tabulas.table

import de.tudresden.inf.lat.tabulas.datatype.CompositeTypeValue


/** This models a sorted table with a map of URI prefixes.
 *
 */
trait Table extends CompositeTypeValue {

  /** Returns the map of URI prefixes.
   *
   * @return the map of URI prefixes
   */
  def getPrefixMap: PrefixMap

  /** Returns the sorting order for the fields.
   *
   * @return the sorting order for the fields
   */
  def getSortingOrder: Seq[String]

  /** Returns the fields that are supposed to be sorted in reverse order.
   *
   * @return the fields that are supposed to be sorted in reverse order
   */
  def getFieldsWithReverseOrder: Set[String]

}

