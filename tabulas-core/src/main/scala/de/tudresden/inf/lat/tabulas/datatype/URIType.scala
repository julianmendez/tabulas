
package de.tudresden.inf.lat.tabulas.datatype

import java.util.Objects

/** This models a link.
 *
 */
case class URIType() extends PrimitiveType {

  final val TypeName: String = "URI"

  override val isList: Boolean = false

  override val getTypeName: String = TypeName

  override val toString: String = getTypeName

  def castInstance(value: PrimitiveTypeValue): URIValue = {
    Objects.requireNonNull(value)
    parse(value.render)
  }

  override def parse(str: String): URIValue = URIValue(str)

}

object URIType {}

