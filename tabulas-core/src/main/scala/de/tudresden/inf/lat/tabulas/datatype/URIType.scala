
package de.tudresden.inf.lat.tabulas.datatype

import java.util.Objects

/** This models a link.
  *
  */
case class URIType() extends PrimitiveType {

  final val TypeName: String = "URI"

  override def getTypeName: String = {
    TypeName
  }

  override def isList: Boolean = {
    false
  }

  override def parse(str: String): URIValue = {
    URIValue(str)
  }

  def castInstance(value: PrimitiveTypeValue): URIValue = {
    Objects.requireNonNull(value)
    val result = parse(value.render())
    result
  }

  override def toString: String = {
    getTypeName
  }

}
