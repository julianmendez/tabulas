
package de.tudresden.inf.lat.tabulas.datatype

import java.util.List

/**
 * This models a record.
 *
 */
trait Record {

  /**
   * Returns the value of a given property.
   *
   * @param key
   *            property name
   * @return the value of a given property
   */
  def get(key: String): PrimitiveTypeValue

  /**
   * Sets the value of a given property.
   *
   * @param key
   *            property name
   * @param value
   *            value
   */
  def set(key: String, value: PrimitiveTypeValue): Unit

  /**
   * Returns the property names.
   *
   * @return the property names
   */
  def getProperties(): List[String]

}

