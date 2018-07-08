
package de.tudresden.inf.lat.tabulas.datatype

import java.util.Objects

import scala.collection.mutable
import scala.collection.mutable.ArrayBuffer

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

  override def renderAsList(): mutable.Buffer[String] = {
    val list: mutable.Buffer[String] = new ArrayBuffer[String]()
    list += render()
    val result: mutable.Buffer[String] = list // @FIXME this should be immutable
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

