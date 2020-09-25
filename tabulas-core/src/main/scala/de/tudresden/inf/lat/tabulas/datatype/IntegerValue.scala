
package de.tudresden.inf.lat.tabulas.datatype

import java.math.BigInteger
import java.util.Objects

/** This models a integer value.
 *
 */
case class IntegerValue(number: BigInteger) extends PrimitiveTypeValue {

  override val getType: PrimitiveType = IntegerType()

  override val isEmpty: Boolean = false

  override val render: String = number.toString

  override val renderAsList: Seq[String] = List(render)

  override val toString: String = number.toString

  val getValue: BigInteger = number

  override def compareTo(other: PrimitiveTypeValue): Int = {
    val result = other match {
      case otherValue: IntegerValue =>
        number.compareTo(otherValue.getValue)
      case _ =>
        render.compareTo(other.render)
    }
    result
  }

}

object IntegerValue {

  def apply(): IntegerValue = IntegerValue(BigInteger.ZERO)

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
      IntegerValue(new BigInteger(str))
    } catch {
      case e: NumberFormatException => throw new ParseException(e.getMessage, e)
    }
  }

}
