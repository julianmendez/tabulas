
package de.tudresden.inf.lat.tabulas.datatype

import java.math.BigInteger
import java.util.ArrayList
import java.util.Collections
import java.util.List
import java.util.Objects

/**
 * This models a integer value.
 *
 */
class IntegerValue extends PrimitiveTypeValue {

  private var number: BigInteger = BigInteger.ZERO

  /**
   * Constructs a new integer value using a string.
   *
   * @param str
   *            string
   * @throws ParseException
   *              <code>str</code> is not a valid representation of an integer
   *              value.
   */
  def this(str: String) = {
    this()
    Objects.requireNonNull(str)
    try {
      this.number = new BigInteger(str)
    } catch {
      case e: NumberFormatException => {
        throw new ParseException(e.getMessage(), e)
      }
    }
  }

  /**
   * Constructs a new integer value using another integer value.
   *
   * @param other
   *            an integer value
   */
  def this(other: IntegerValue) = {
    this()
    this.number = other.number
  }

  override def getType(): PrimitiveType = {
    return new IntegerType()
  }

  override def isEmpty(): Boolean = {
    return false
  }

  override def render(): String = {
    return this.number.toString()
  }

  override def renderAsList(): List[String] = {
    val ret: List[String] = new ArrayList[String]()
    ret.add(render())
    return Collections.unmodifiableList(ret)

  }

  override def compareTo(other: PrimitiveTypeValue): Int = {
    if (other.isInstanceOf[IntegerValue]) {
      val otherValue: IntegerValue = other.asInstanceOf[IntegerValue]
      return this.number.compareTo(otherValue.number)
    } else {
      return render().compareTo(other.render())
    }
  }

  override def hashCode(): Int = {
    return this.number.hashCode()
  }

  override def equals(obj: Any): Boolean = {
    if (obj.isInstanceOf[IntegerValue]) {
      val other: IntegerValue = obj.asInstanceOf[IntegerValue]
      return this.number.equals(other.number)
    } else {
      return false
    }
  }

  override def toString(): String = {
    return this.number.toString()
  }

}

