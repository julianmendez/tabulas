
package de.tudresden.inf.lat.tabulas.datatype

import java.util.Objects

/** This models a link.
  *
  */
class URIType extends PrimitiveType {

  val TypeName: String = "URI"

  override def getTypeName: String = {
    TypeName
  }

  override def isList: Boolean = {
    false
  }

  override def parse(str: String): URIValue = {
    new URIValue(str)
  }

  def castInstance(value: PrimitiveTypeValue): URIValue = {
    Objects.requireNonNull(value)
    val result = parse(value.render())
    result
  }

  override def hashCode(): Int = {
    getTypeName.hashCode()
  }

  override def equals(obj: Any): Boolean = {
    val result: Boolean = obj match {
      case other: URIType => true
      case _ => false
    }
    result
  }

  override def toString: String = {
    getTypeName
  }

}

object URIType {

  def apply(): URIType = new URIType

}
