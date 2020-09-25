
package de.tudresden.inf.lat.tabulas.datatype

/** This models the primitive data type Decimal.
 *
 */
case class DecimalType() extends PrimitiveType {

  final val TypeName: String = "Decimal"

  override val isList: Boolean = false

  override val getTypeName: String = TypeName

  override val toString: String = getTypeName

  def castInstance(value: PrimitiveTypeValue): DecimalValue = parse(value.render)

  override def parse(str: String): DecimalValue = DecimalValue(str)

}

object DecimalType {}

