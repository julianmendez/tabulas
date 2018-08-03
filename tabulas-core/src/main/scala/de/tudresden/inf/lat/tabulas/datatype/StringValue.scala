
package de.tudresden.inf.lat.tabulas.datatype

import java.util.Objects

import scala.collection.mutable

/** This models a string value.
  *
  */
class StringValue extends PrimitiveTypeValue {

  private var _str: String = ""

  /** Constructs a new string value using a string.
    *
    * @param str
    * string
    */
  def this(str: String) = {
    this()
    this._str = if (Objects.isNull(str)) {
      ""
    } else {
      str
    }
  }

  /** Constructs a new string value using another string value.
    *
    * @param other
    * a string value
    */
  def this(other: StringValue) = {
    this()
    this._str = other._str
  }

  override def getType: PrimitiveType = { new StringType() }

  override def isEmpty: Boolean = { this._str.trim().isEmpty }

  override def render(): String = { this._str }

  override def renderAsList(): Seq[String] = {
    val list = new mutable.ArrayBuffer[String]()
    list += render()
    val result: Seq[String] = list.toList
    result
  }

  override def compareTo(other: PrimitiveTypeValue): Int = { toString.compareTo(other.toString) }

  override def hashCode(): Int = { this._str.hashCode() }

  override def equals(obj: Any): Boolean = {
    var result: Boolean  = false
    obj match {
      case other: StringValue => result = this._str.equals(other._str)
      case _ => result = false
    }
    result
  }

  override def toString: String = { this._str }

}

object StringValue {

  def apply(): StringValue = new StringValue

}
