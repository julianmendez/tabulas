
package de.tudresden.inf.lat.tabulas.datatype

import java.util.Objects

/** This models a link.
  *
  */
class URIType extends PrimitiveType {

  val TypeName: String = "URI"

  override def getTypeName: String = {
    return TypeName
  }

  override def isList: Boolean = {
    return false
  }

  override def parse(str: String): URIValue = {
    return new URIValue(str)
  }

  def castInstance(value: PrimitiveTypeValue): URIValue = {
    Objects.requireNonNull(value)
    return parse(value.render())
  }

  override def hashCode(): Int = {
    return getTypeName.hashCode()
  }

  override def equals(obj: Any): Boolean = {
    obj match {
      case other: URIType => return true
      case _ => return false
    }
  }

  override def toString: String = {
    return getTypeName
  }

}

