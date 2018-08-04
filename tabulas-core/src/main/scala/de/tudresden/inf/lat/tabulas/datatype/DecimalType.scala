
package de.tudresden.inf.lat.tabulas.datatype

/** This models the primitive data type Decimal.
  *
  */
class DecimalType extends PrimitiveType {

  val TypeName: String = "Decimal"

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

  override def hashCode(): Int = {
    getTypeName.hashCode()
  }

  override def equals(obj: Any): Boolean = {
    val result: Boolean = obj match {
      case other: DecimalType => true
      case _ => false
    }
    result
  }

  override def toString: String = {
    getTypeName
  }

}

object DecimalType {

  def apply(): DecimalType = new DecimalType

}
