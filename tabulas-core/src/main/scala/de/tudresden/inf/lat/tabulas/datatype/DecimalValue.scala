
package de.tudresden.inf.lat.tabulas.datatype

import java.math.BigDecimal
import java.util.Objects

import scala.collection.mutable

/** This models a decimal value.
  *
  */
class DecimalValue(number: BigDecimal) extends PrimitiveTypeValue {

  override def getType: PrimitiveType = {
    new DecimalType()
  }

  def getValue: BigDecimal = number

  override def isEmpty: Boolean = {
    false
  }

  override def render(): String = {
    this.number.toString
  }

  override def renderAsList(): Seq[String] = {
    val list = new mutable.ArrayBuffer[String]()
    list += render()
    val result = list.toList
    result
  }

  override def compareTo(other: PrimitiveTypeValue): Int = {
    val result: Int = other match {
      case otherValue: DecimalValue =>
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
    var result: Boolean = false
    obj match {
      case other: DecimalValue => result = this.number.equals(other.getValue)
      case _ => result = false
    }
    result
  }

  override def toString: String = {
    this.number.toString
  }

}

object DecimalValue {

  def apply(): DecimalValue = new DecimalValue(BigDecimal.ZERO)

  /** Constructs a new decimal value using a string.
    *
    * @param str string
    * @throws ParseException
    * <code>str</code> is not a valid representation of a decimal
    * value.
    */
  def apply(str: String): DecimalValue = {
    Objects.requireNonNull(str)
    try {
      new DecimalValue(new BigDecimal(str))
    } catch {
      case e: NumberFormatException => throw new ParseException(e.getMessage, e)
    }
  }

  /** Constructs a new decimal value using another decimal value.
    *
    * @param other a decimal value
    */
  def apply(other: DecimalValue): DecimalValue = {
    new DecimalValue(other.getValue)
  }

}
