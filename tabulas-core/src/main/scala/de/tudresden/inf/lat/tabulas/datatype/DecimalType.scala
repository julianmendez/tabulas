
package de.tudresden.inf.lat.tabulas.datatype

/** This models the primitive data type Decimal.
 *
 */
case class DecimalType() extends PrimitiveType {

  final val TypeName: String = "Decimal"

  override def isList: Boolean = {
    false
  }

  def castInstance(value: PrimitiveTypeValue): DecimalValue = {
    parse(value.render())
  }

  override def parse(str: String): DecimalValue = {
    DecimalValue(str)
  }

  override def toString: String = {
    getTypeName
  }

  override def getTypeName: String = {
    TypeName
  }

}

object DecimalType {}
