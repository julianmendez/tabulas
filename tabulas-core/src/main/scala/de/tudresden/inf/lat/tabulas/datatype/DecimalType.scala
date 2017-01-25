
package de.tudresden.inf.lat.tabulas.datatype

import java.util.Objects

/**
 * This models the primitive data type Decimal.
 *
 */
class DecimalType extends PrimitiveType {

  val TypeName: String = "Decimal"

  override def getTypeName(): String = {
    return TypeName
  }

  override def isList(): Boolean = {
    return false
  }

  override def parse(str: String): DecimalValue = {
    return new DecimalValue(str)
  }

  def castInstance(value: PrimitiveTypeValue): DecimalValue = {
    return parse(value.render())
  }

  override def hashCode(): Int = {
    return getTypeName().hashCode()
  }

  override def equals(obj: Any): Boolean = {
    if (Objects.isNull(obj)) {
      return false
    } else {
      return (obj.isInstanceOf[DecimalType])
    }
  }

  override def toString(): String = {
    return getTypeName()
  }

}

