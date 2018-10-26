
package de.tudresden.inf.lat.tabulas.datatype

import java.math.BigInteger
import java.util.Objects

import scala.collection.mutable

/** This models a integer value.
  *
  */
class IntegerValue(number: BigInteger) extends PrimitiveTypeValue {

  override def getType: PrimitiveType = {
    new IntegerType()
  }

  def getValue: BigInteger = number

  override def isEmpty: Boolean = {
    false
  }

  override def render(): String = {
    this.number.toString
  }

  override def renderAsList(): Seq[String] = {
    List(render())
  }

  override def compareTo(other: PrimitiveTypeValue): Int = {
    val result = other match {
      case otherValue: IntegerValue =>
        this.number.compareTo(otherValue.getValue)
      case _ =>
        render().compareTo(other.render())
    }
    result
  }

  override def hashCode(): Int = {
    this.number.hashCode()
  }

  override def equals(obj: Any): Boolean = {
    val result = obj match {
      case other: IntegerValue => this.number.equals(other.getValue)
      case _ => false
    }
    result
  }

  override def toString: String = {
    this.number.toString
  }

}

object IntegerValue {

  def apply(): IntegerValue = new IntegerValue(BigInteger.ZERO)

  /** Constructs a new integer value using a string.
    *
    * @param str string
    * @throws ParseException
    * <code>str</code> is not a valid representation of an integer
    * value.
    */
  def apply(str: String): IntegerValue = {
    Objects.requireNonNull(str)
    try {
      new IntegerValue(new BigInteger(str))
    } catch {
      case e: NumberFormatException => throw new ParseException(e.getMessage, e)
    }
  }

  /** Constructs a new integer value using another integer value.
    *
    * @param other an integer value
    */
  def apply(other: IntegerValue): IntegerValue = {
    new IntegerValue(other.getValue)
  }

}
