
package de.tudresden.inf.lat.tabulas.datatype

/**
  * This models the primitive data type Integer.
  *
  */
class IntegerType extends PrimitiveType {

  val TypeName: String = "Integer"

  override def getTypeName: String = {
    return TypeName
  }

  override def isList: Boolean = {
    return false
  }

  override def parse(str: String): IntegerValue = {
    return new IntegerValue(str)
  }

  def castInstance(value: PrimitiveTypeValue): IntegerValue = {
    return parse(value.render())
  }

  override def hashCode(): Int = {
    return getTypeName.hashCode()
  }

  override def equals(obj: Any): Boolean = {
    obj match {
      case other: IntegerType => return true
      case _ => return false
    }
  }

  override def toString: String = {
    return getTypeName
  }

}

