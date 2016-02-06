
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
    new StringType();
  }

  override def render(): String = {
    str
  }

  override def isEmpty(): Boolean = {
    str.trim().isEmpty()
  }

  override def renderAsList(): List[String] = {
    val ret: List[String] = new ArrayList[String]()
    ret.add(render())
    Collections.unmodifiableList(ret)

  }

  override def compareTo(other: PrimitiveTypeValue): Int = {
    toString().compareTo(other.toString())
  }

  override def hashCode(): Int = {
    this.str.hashCode()
  }

  override def equals(obj: Any): Boolean = {
    if (obj.isInstanceOf[StringValue]) {
      val other: StringValue = obj.asInstanceOf[StringValue]
      this.str.equals(other.str)
    } else {
      false
    }
  }

  override def toString(): String = {
    this.str
  }

}

