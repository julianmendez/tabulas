
package de.tudresden.inf.lat.tabulas.table

import java.net.URI

import de.tudresden.inf.lat.tabulas.datatype.CompositeTypeValue

import scala.collection.mutable
import scala.collection.mutable.{Map, Set}


/**
  * This models a sorted table with a map of URI prefixes.
  *
  */
trait Table extends CompositeTypeValue {

  /**
    * Returns the map of URI prefixes.
    *
    * @return the map of URI prefixes
    */
  def getPrefixMap: PrefixMap

  /**
    * Sets the map of URI prefixes
    *
    * @param prefixMap
    * map of URI prefixes
    */
  def setPrefixMap(prefixMap: PrefixMap): Unit

  /**
    * Returns the sorting order for the fields.
    *
    * @return the sorting order for the fields
    */
  def getSortingOrder: mutable.Buffer[String]

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
  def getFieldsWithReverseOrder: Set[String]

  /**
    * Sets the fields that are supposed to be sorted in reverse order.
    *
    * @param fieldsWithReverseOrder
    * fields with reverse order
    */
  def setFieldsWithReverseOrder(fieldsWithReverseOrder: Set[String]): Unit

}

