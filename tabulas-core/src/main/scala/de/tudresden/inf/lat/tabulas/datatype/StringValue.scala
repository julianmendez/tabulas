
package de.tudresden.inf.lat.tabulas.datatype

/** This models a string value.
 *
 */
case class StringValue(str: String) extends PrimitiveTypeValue {

  override val getType: PrimitiveType = StringType()

  override val isEmpty: Boolean = str.trim().isEmpty

  override val render: String = str

  override val renderAsList: Seq[String] = List(render)

  override val toString: String = str

  val getValue: String = str

  override def compareTo(other: PrimitiveTypeValue): Int = {
    toString.compareTo(other.toString)
  }

}

object StringValue {

  def apply(): StringValue = {
    new StringValue("")
  }

  /** Constructs a new string value using another string value.
   *
   * @param other a string value
   */
  def apply(other: StringValue): StringValue = {
    new StringValue(other.getValue)
  }

}
