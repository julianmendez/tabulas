
package de.tudresden.inf.lat.tabulas.datatype

import scala.collection.mutable

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
  def getFields: mutable.Buffer[String]

  /**
    * Returns an optional containing the type of the given field, if the field
    * is present, or an empty optional otherwise.
    *
    * @param field
    * field
    * @return an optional containing the type of the given field, if the field
    *         is present, or an empty optional otherwise
    */
  def getFieldType(field: String): Option[String]

}

