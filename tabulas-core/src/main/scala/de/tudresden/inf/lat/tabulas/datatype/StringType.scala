
package de.tudresden.inf.lat.tabulas.datatype

import java.util.Objects

/**
 * This models the primitive data type String.
 *
 */
class StringType extends PrimitiveType {

  val TypeName: String = "String"

  override def getTypeName(): String = {
    TypeName
  }

  override def isList(): Boolean = {
    false
  }

  override def parse(str: String): StringValue = {
    new StringValue(str)
  }

  def castInstance(value: PrimitiveTypeValue): StringValue = {
    parse(value.render())
  }

  override def hashCode(): Int = {
    getTypeName().hashCode()
  }

  override def equals(obj: Any): Boolean = {
    if (Objects.isNull(obj)) {
      false
    } else {
      (obj.isInstanceOf[StringType])
    }
  }

  override def toString(): String = {
    getTypeName()
  }

}

