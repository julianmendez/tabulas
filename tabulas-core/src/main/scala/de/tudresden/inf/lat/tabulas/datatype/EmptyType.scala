
package de.tudresden.inf.lat.tabulas.datatype

/** This models the primitive data type Empty.
  *
  */
class EmptyType extends PrimitiveType {

  val TypeName: String = "Empty"

  override def getTypeName: String = {
    return TypeName
  }

  override def isList: Boolean = {
    return false
  }

  override def parse(str: String): EmptyValue = {
    return new EmptyValue()
  }

  def castInstance(value: PrimitiveTypeValue): EmptyValue = {
    return parse(value.render())
  }

  override def hashCode(): Int = {
    return getTypeName.hashCode()
  }

  override def equals(obj: Any): Boolean = {
    val result: Boolean = obj match {
      case other: EmptyType => true
      case _ => false
    }

    return result
  }

  override def toString: String = {
    return getTypeName
  }

}

