
package de.tudresden.inf.lat.tabulas.datatype

import java.math.BigDecimal
import java.util.Objects

import scala.collection.mutable

/** This models a decimal value.
  *
  */
class DecimalValue extends PrimitiveTypeValue {

  private var _number: BigDecimal = BigDecimal.ZERO

  /** Constructs a new decimal value using a string.
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
      case e: NumberFormatException => throw new ParseException(e.getMessage, e)
    }
  }

  /** Constructs a new decimal value using another decimal value.
    *
    * @param other
    * a decimal value
    */
  def this(other: DecimalValue) = {
    this()
    this._number = other._number
  }

  override def getType: PrimitiveType = { new DecimalType() }

  override def isEmpty: Boolean = { false }

  override def render(): String = { this._number.toString }

  override def renderAsList(): Seq[String] = {
    val list = new mutable.ArrayBuffer[String]()
    list += render()
    val result = list.toList
    result
  }

  override def compareTo(other: PrimitiveTypeValue): Int = {
    val result: Int = other match {
      case otherValue: DecimalValue =>
        this._number.compareTo(otherValue._number)
      case _ =>
        render().compareTo(other.render())
    }
    result
  }

  override def hashCode(): Int = { this._number.hashCode() }

  override def equals(obj: Any): Boolean = {
    var result: Boolean = false
    obj match {
      case other: DecimalValue => result = this._number.equals(other._number)
      case _ => result = false
    }
    result
  }

  override def toString: String = { this._number.toString }

}

