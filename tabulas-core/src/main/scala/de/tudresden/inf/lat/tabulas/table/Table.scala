
package de.tudresden.inf.lat.tabulas.table

import java.util.List
import java.util.Set

import de.tudresden.inf.lat.tabulas.datatype.CompositeTypeValue

/**
 * This models a sorted table.
 *
 */
trait Table extends CompositeTypeValue {

  /**
   * Returns the sorting order for the fields.
   *
   * @return the sorting order for the fields
   */
  def getSortingOrder(): List[String]

  /**
   * Sets the sorting order for the fields.
   *
   * @param sortingOrder
   *            sorting order
   */
  def setSortingOrder(sortingOrder: List[String])

  /**
   * Returns the fields that are supposed to be sorted in reverse order.
   *
   * @return the fields that are supposed to be sorted in reverse order
   */
  def getFieldsWithReverseOrder(): Set[String]

  /**
   *  Sets the fields that are supposed to be sorted in reverse order.
   *
   * @param fieldsWithReverseOrder
   *            fields with reverse order
   */
  def setFieldsWithReverseOrder(fieldsWithReverseOrder: Set[String]): Unit

}

