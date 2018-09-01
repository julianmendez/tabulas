
package de.tudresden.inf.lat.tabulas.datatype

/** This models a string value.
  *
  */

class StringValue(str: String) extends PrimitiveTypeValue {

  override def getType: PrimitiveType = {
    new StringType()
  }

  def getValue: String = {
    str
  }

  override def isEmpty: Boolean = {
    str.trim().isEmpty
  }

  override def render(): String = {
    str
  }

  override def renderAsList(): Seq[String] = {
    List(render())
  }

  override def compareTo(other: PrimitiveTypeValue): Int = {
    toString.compareTo(other.toString)
  }

  override def hashCode(): Int = {
    str.hashCode()
  }

  override def equals(obj: Any): Boolean = {
    val result = obj match {
      case other: StringValue => str.equals(other.getValue)
      case _ => false
    }
    result
  }

  override def toString: String = {
    str
  }

}

object StringValue {

  def apply(): StringValue = {
    new StringValue("")
  }

  /** Constructs a new string value using a string.
    *
    * @param str string
    */
  def apply(str: String): StringValue = {
    new StringValue(str)
  }

  /** Constructs a new string value using another string value.
    *
    * @param other a string value
    */
  def apply(other: StringValue): StringValue = {
    new StringValue(other.getValue)
  }

}
