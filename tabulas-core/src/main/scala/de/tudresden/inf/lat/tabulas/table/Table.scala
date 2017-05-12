
package de.tudresden.inf.lat.tabulas.table

import de.tudresden.inf.lat.tabulas.datatype.CompositeTypeValue

import scala.collection.mutable
import scala.collection.mutable.Set

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
  def getSortingOrder(): mutable.Buffer[String]

  /**
    * Sets the sorting order for the fields.
    *
    * @param sortingOrder
    * sorting order
    */
  def setSortingOrder(sortingOrder: mutable.Buffer[String]): Unit

  /**
    * Returns the fields that are supposed to be sorted in reverse order.
    *
    * @return the fields that are supposed to be sorted in reverse order
    */
  def getFieldsWithReverseOrder(): Set[String]

  /**
    * Sets the fields that are supposed to be sorted in reverse order.
    *
    * @param fieldsWithReverseOrder
    * fields with reverse order
    */
  def setFieldsWithReverseOrder(fieldsWithReverseOrder: Set[String]): Unit

}

