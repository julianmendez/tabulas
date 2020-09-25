
package de.tudresden.inf.lat.tabulas.datatype

/** This models a empty value.
 *
 */
case class EmptyValue() extends PrimitiveTypeValue {

  override val getType: PrimitiveType = EmptyType()

  override val isEmpty: Boolean = true

  override val render: String = ""

  override val renderAsList: Seq[String] = List()

  override val toString: String = render

  override def compareTo(other: PrimitiveTypeValue): Int = {
    toString.compareTo(other.toString)
  }

}

object EmptyValue {

  def apply(): EmptyValue = new EmptyValue

}
