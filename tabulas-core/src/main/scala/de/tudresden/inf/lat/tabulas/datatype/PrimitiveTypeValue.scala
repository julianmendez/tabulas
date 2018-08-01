
package de.tudresden.inf.lat.tabulas.datatype

/** This models a value of a primitive type.
  *
  */
trait PrimitiveTypeValue extends Comparable[PrimitiveTypeValue] {

  /** Returns the primitive type
    *
    * @return the primitive type
    */
  def getType: PrimitiveType

  /** Returns a string representing this value.
    *
    * @return a string representing this value
    */
  def render(): String

  /** Returns a list of strings representing this value.
    *
    * @return a list of strings representing this value
    */
  def renderAsList(): Seq[String]

  /** Tell whether this value represents an empty value.
    *
    * @return <code>true</code> if and only if this value represents an empty
    *         value
    */
  def isEmpty: Boolean

}
