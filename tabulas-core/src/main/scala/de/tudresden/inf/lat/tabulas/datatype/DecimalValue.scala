
package de.tudresden.inf.lat.tabulas.datatype

import java.math.BigDecimal
import java.util.Objects

import scala.collection.mutable
import scala.collection.mutable.ArrayBuffer

/**
  * This models a decimal value.
  *
  */
class DecimalValue extends PrimitiveTypeValue {

  private var _number: BigDecimal = BigDecimal.ZERO

  /**
    * Constructs a new decimal value using a string.
    *
    * @param str
    * string
    * @throws ParseException
    * <code>str</code> is not a valid representation of a decimal
    * value.
    */
  def this(str: String) = {
    this()
    Objects.requireNonNull(str)
    try {
      this._number = new BigDecimal(str)
    } catch {
      case e: NumberFormatException => {
        throw new ParseException(e.getMessage, e)
      }
    }
  }

  /**
    * Constructs a new decimal value using another decimal value.
    *
    * @param other
    * a decimal value
    */
  def this(other: DecimalValue) = {
    this()
    this._number = other._number
  }

  override def getType: PrimitiveType = {
    return new DecimalType()
  }

  override def isEmpty: Boolean = {
    return false
  }

  override def render(): String = {
    return this._number.toString
  }

  override def renderAsList(): mutable.Buffer[String] = {
    val list: mutable.Buffer[String] = new ArrayBuffer[String]()
    list += render()
    val result = list // @FIXME this should be immutable

    return result
  }

  override def compareTo(other: PrimitiveTypeValue): Int = {
    var result: Int = 0
    if (other.isInstanceOf[DecimalValue]) {
      val otherValue: DecimalValue = other.asInstanceOf[DecimalValue]
      result = this._number.compareTo(otherValue._number)
    } else {
      result = render().compareTo(other.render())
    }

    return result
  }

  override def hashCode(): Int = {
    return this._number.hashCode()
  }

  override def equals(obj: Any): Boolean = {
    var result: Boolean = false
    obj match {
      case other: DecimalValue => result = this._number.equals(other._number)
      case _ => result = false
    }

    return result
  }

  override def toString: String = {
    return this._number.toString
  }

}

