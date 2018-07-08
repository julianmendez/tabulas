
package de.tudresden.inf.lat.tabulas.datatype

import java.math.BigInteger
import java.util.Objects

import scala.collection.mutable
import scala.collection.mutable.ArrayBuffer

/** This models a integer value.
  *
  */
class IntegerValue extends PrimitiveTypeValue {

  private var _number: BigInteger = BigInteger.ZERO

  /** Constructs a new integer value using a string.
    *
    * @param str
    * string
    * @throws ParseException
    * <code>str</code> is not a valid representation of an integer
    * value.
    */
  def this(str: String) = {
    this()
    Objects.requireNonNull(str)
    try {
      this._number = new BigInteger(str)
    } catch {
      case e: NumberFormatException => throw new ParseException(e.getMessage, e)
    }
  }

  /** Constructs a new integer value using another integer value.
    *
    * @param other
    * an integer value
    */
  def this(other: IntegerValue) = {
    this()
    this._number = other._number
  }

  override def getType: PrimitiveType = { new IntegerType() }

  override def isEmpty: Boolean = { false }

  override def render(): String = { this._number.toString }

  override def renderAsList(): mutable.Buffer[String] = {
    val list: mutable.Buffer[String] = new ArrayBuffer[String]()
    list += render()
    val result: mutable.Buffer[String] = list // @FIXME this should be immutable
    result
  }

  override def compareTo(other: PrimitiveTypeValue): Int = {
    val result: Int = other match {
      case otherValue: IntegerValue =>
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
      case other: IntegerValue => result = this._number.equals(other._number)
      case _ => result = false
    }
    result
  }

  override def toString: String = { this._number.toString }

}

