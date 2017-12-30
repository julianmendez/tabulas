
package de.tudresden.inf.lat.tabulas.datatype

/** This models the primitive data type Decimal.
  *
  */
class DecimalType extends PrimitiveType {

  val TypeName: String = "Decimal"

  override def getTypeName: String = {
    return TypeName
  }

  override def isList: Boolean = {
    return false
  }

  override def parse(str: String): DecimalValue = {
    return new DecimalValue(str)
  }

  def castInstance(value: PrimitiveTypeValue): DecimalValue = {
    return parse(value.render())
  }

  override def hashCode(): Int = {
    return getTypeName.hashCode()
  }

  override def equals(obj: Any): Boolean = {
    obj match {
      case other: DecimalType => return true
      case _ => return false
    }
  }

  override def toString: String = {
    return getTypeName
  }

}

