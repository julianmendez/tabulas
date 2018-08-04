
package de.tudresden.inf.lat.tabulas.datatype

/** This models a primitive type.
  *
  */
trait PrimitiveType extends DataType {

  /** Returns the name of this type.
    *
    * @return the name of this type
    */
  def getTypeName: String

  /** Tells whether this type is a list.
    *
    * @return <code>true</code> if and only if this type is a list
    */
  def isList: Boolean

  /** Returns a value based on the given string.
    *
    * @param str string
    * @return a value based on the given string
    */
  def parse(str: String): PrimitiveTypeValue

}
