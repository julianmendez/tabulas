
package de.tudresden.inf.lat.tabulas.datatype

import java.math.BigDecimal
import java.util.Objects

/** This models a decimal value.
 *
 */
case class DecimalValue(number: BigDecimal) extends PrimitiveTypeValue {

  override val getType: PrimitiveType = DecimalType()

  override val isEmpty: Boolean = false

  override val render: String = number.toString

  override val renderAsList: Seq[String] = List(render)

  override def compareTo(other: PrimitiveTypeValue): Int = {
    val result = other match {
      case otherValue: DecimalValue =>
        number.compareTo(otherValue.getValue)
      case _ =>
        render.compareTo(other.render)
    }
    result
  }

  val getValue: BigDecimal = number

  override val toString: String = number.toString

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

}
