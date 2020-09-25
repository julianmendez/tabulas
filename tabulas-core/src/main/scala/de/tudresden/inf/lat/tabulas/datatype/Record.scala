
package de.tudresden.inf.lat.tabulas.datatype

/** This models a record.
 *
 */
trait Record {

  /** Returns an optional containing the value of a given property, if this
   * value is present, or an empty optional otherwise.
   *
   * @param key property name
   * @return an optional containing the value of a given property, if this
   *         value is present, or an empty optional otherwise
   */
  def get(key: String): Option[PrimitiveTypeValue]

  /** Returns a map representing this record.
   *
   * @return a map
   */
  def getMap: Map[String, PrimitiveTypeValue]

  /** Returns the property names.
   *
   * @return the property names
   */
  def getProperties: Seq[String]

}

