
package de.tudresden.inf.lat.tabulas.datatype

/** This models a empty value.
 *
 */
case class EmptyValue() extends PrimitiveTypeValue {

  private val Value: String = ""

  override def getType: PrimitiveType = {
    EmptyType()
  }

  override def isEmpty: Boolean = {
    true
  }

  override def render(): String = {
    Value
  }

  override def renderAsList(): Seq[String] = {
    List()
  }

  override def compareTo(other: PrimitiveTypeValue): Int = {
    toString.compareTo(other.toString)
  }

  override def toString: String = {
    Value
  }

}

object EmptyValue {

  def apply(): EmptyValue = new EmptyValue

}
