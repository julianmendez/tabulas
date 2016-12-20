
package de.tudresden.inf.lat.tabulas.datatype

import java.util.Objects

/**
 * This models the primitive data type String.
 *
 */
class StringType extends PrimitiveType {

  val TypeName: String = "String"

  override def getTypeName(): String = {
    return TypeName
  }

  override def isList(): Boolean = {
    return false
  }

  override def parse(str: String): StringValue = {
    return new StringValue(str)
  }

  def castInstance(value: PrimitiveTypeValue): StringValue = {
    return parse(value.render())
  }

  override def hashCode(): Int = {
    return getTypeName().hashCode()
  }

  override def equals(obj: Any): Boolean = {
    if (Objects.isNull(obj)) {
      return false
    } else {
      return (obj.isInstanceOf[StringType])
    }
  }

  override def toString(): String = {
    return getTypeName()
  }

}

