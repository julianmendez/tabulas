
package de.tudresden.inf.lat.tabulas.datatype

/** This models a composite type value.
 *
 */
trait CompositeTypeValue {

  /** Returns the type of this composite type value.
   *
   * @return the type of this composite type value
   */
  def getType: CompositeType

  /** Returns all the records.
   *
   * @return all the records
   */
  def getRecords: Seq[Record]

}

