
package de.tudresden.inf.lat.tabulas.datatype

import java.util.List

/**
 * This models a composite type.
 *
 */
trait CompositeType extends DataType {

  /**
   * Returns all the fields.
   *
   * @return all the fields
   */
  def getFields(): List[String]

  /**
   * Returns the type of the given field.
   *
   * @param field
   *            field
   * @return the type of the given field
   */
  def getFieldType(field: String): String

}

