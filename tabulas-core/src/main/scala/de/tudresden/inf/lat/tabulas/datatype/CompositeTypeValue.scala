
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

  /** Sets the type of this composite type value.
    *
    * @param newType
    * type
    */
  def setType(newType: CompositeType): Unit

  /** Returns all the records.
    *
    * @return all the records
    */
  def getRecords: Seq[Record]

  /** Adds a record. Returns <code>true</code> if and only if this composite
    * type value changed as a result of the call.
    *
    * @param record
    * record
    * @return <code>true</code> if and only if this composite type value
    *         changed as a result of the call
    */
  def add(record: Record): Boolean

  /** Removes all of the records from this composite type value.
    */
  def clear(): Unit

}

