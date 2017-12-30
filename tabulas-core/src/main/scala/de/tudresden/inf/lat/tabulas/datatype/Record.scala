
package de.tudresden.inf.lat.tabulas.datatype

import scala.collection.mutable

/** This models a record.
  *
  */
trait Record {

  /** Returns an optional containing the value of a given property, if this
    * value is present, or an empty optional otherwise.
    *
    * @param key
    * property name
    * @return an optional containing the value of a given property, if this
    *         value is present, or an empty optional otherwise
    */
  def get(key: String): Option[PrimitiveTypeValue]

  /** Sets the value of a given property.
    *
    * @param key
    * property name
    * @param value
    * value
    */
  def set(key: String, value: PrimitiveTypeValue): Unit

  /** Returns the property names.
    *
    * @return the property names
    */
  def getProperties: mutable.Buffer[String]

}

