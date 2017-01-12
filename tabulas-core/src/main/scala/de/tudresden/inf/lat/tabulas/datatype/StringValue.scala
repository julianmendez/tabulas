
package de.tudresden.inf.lat.tabulas.datatype

import java.util.ArrayList
import java.util.Collections
import java.util.List
import java.util.Objects

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
   *            string
   */
  def this(str: String) = {
    this()
    this.str = if (Objects.isNull(str)) { "" } else { str }
  }

  /**
   * Constructs a new string value using another string value.
   *
   * @param other
   *            a string value
   */
  def this(other: StringValue) = {
    this()
    this.str = other.str
  }

  override def getType(): PrimitiveType = {
    return new StringType()
  }

  override def isEmpty(): Boolean = {
    return str.trim().isEmpty()
  }

  override def render(): String = {
    return str
  }

  override def renderAsList(): List[String] = {
    val ret: List[String] = new ArrayList[String]()
    ret.add(render())
    return Collections.unmodifiableList(ret)

  }

  override def compareTo(other: PrimitiveTypeValue): Int = {
    return toString().compareTo(other.toString())
  }

  override def hashCode(): Int = {
    return this.str.hashCode()
  }

  override def equals(obj: Any): Boolean = {
    if (obj.isInstanceOf[StringValue]) {
      val other: StringValue = obj.asInstanceOf[StringValue]
      return this.str.equals(other.str)
    } else {
      return false
    }
  }

  override def toString(): String = {
    return this.str
  }

}

