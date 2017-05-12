
package de.tudresden.inf.lat.tabulas.datatype

import java.math.BigInteger
import scala.collection.mutable.ArrayBuffer
import scala.collection.mutable
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

  override def renderAsList(): mutable.Buffer[String] = {
    val ret: mutable.Buffer[String] = new ArrayBuffer[String]()
    ret += render()
    return ret // @FIXME this should be immutable

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

