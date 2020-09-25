
package de.tudresden.inf.lat.tabulas.datatype

import java.util.Objects

/** This models a link.
 *
 */
case class URIType() extends PrimitiveType {

  final val TypeName: String = "URI"

  override def isList: Boolean = {
    false
  }

  def castInstance(value: PrimitiveTypeValue): URIValue = {
    Objects.requireNonNull(value)
    parse(value.render())
  }

  override def parse(str: String): URIValue = {
    URIValue(str)
  }

  override def toString: String = {
    getTypeName
  }

  override def getTypeName: String = {
    TypeName
  }

}

object URIType {}
