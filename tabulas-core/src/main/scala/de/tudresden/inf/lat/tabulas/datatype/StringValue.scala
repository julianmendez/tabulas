
package de.tudresden.inf.lat.tabulas.datatype

import java.util.Objects

import scala.collection.mutable
import scala.collection.mutable.ArrayBuffer

/**
  * This models a string value.
  *
  */
class StringValue extends PrimitiveTypeValue {

  private var str: String = ""

  /**
    * Constructs a new string value using a string.
    *
    * @param str
    * string
    */
  def this(str: String) = {
    this()
    this.str = if (Objects.isNull(str)) {
      ""
    } else {
      str
    }
  }

  /**
    * Constructs a new string value using another string value.
    *
    * @param other
    * a string value
    */
  def this(other: StringValue) = {
    this()
    this.str = other.str
  }

  override def getType: PrimitiveType = {
    return new StringType()
  }

  override def isEmpty: Boolean = {
    return str.trim().isEmpty
  }

  override def render(): String = {
    return str
  }

  override def renderAsList(): mutable.Buffer[String] = {
    val ret: mutable.Buffer[String] = new ArrayBuffer[String]()
    ret += render()
    return ret // @FIXME this should be immutable

  }

  override def compareTo(other: PrimitiveTypeValue): Int = {
    return toString.compareTo(other.toString)
  }

  override def hashCode(): Int = {
    return this.str.hashCode()
  }

  override def equals(obj: Any): Boolean = {
    obj match {
      case other: StringValue => return this.str.equals(other.str)
      case _ => return false
    }
  }

  override def toString: String = {
    return this.str
  }

}

