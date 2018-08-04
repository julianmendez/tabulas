
package de.tudresden.inf.lat.tabulas.datatype

import scala.collection.mutable

/** This models a string value.
  *
  */

class StringValue(str: String) extends PrimitiveTypeValue {

  override def getType: PrimitiveType = {
    new StringType()
  }

  def getValue: String = str

  override def isEmpty: Boolean = {
    this.str.trim().isEmpty
  }

  override def render(): String = {
    this.str
  }

  override def renderAsList(): Seq[String] = {
    val list = new mutable.ArrayBuffer[String]()
    list += render()
    val result: Seq[String] = list.toList
    result
  }

  override def compareTo(other: PrimitiveTypeValue): Int = {
    toString.compareTo(other.toString)
  }

  override def hashCode(): Int = {
    this.str.hashCode()
  }

  override def equals(obj: Any): Boolean = {
    var result: Boolean = false
    obj match {
      case other: StringValue => result = this.str.equals(other.getValue)
      case _ => result = false
    }
    result
  }

  override def toString: String = {
    this.str
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
