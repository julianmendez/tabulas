
package de.tudresden.inf.lat.tabulas.datatype

/** This models the primitive data type Decimal.
  *
  */
case class DecimalType() extends PrimitiveType {

  final val TypeName: String = "Decimal"

  override def getTypeName: String = {
    TypeName
  }

  override def isList: Boolean = {
    false
  }

  override def parse(str: String): DecimalValue = {
    DecimalValue(str)
  }

  def castInstance(value: PrimitiveTypeValue): DecimalValue = {
    parse(value.render())
  }

  override def toString: String = {
    getTypeName
  }

}
