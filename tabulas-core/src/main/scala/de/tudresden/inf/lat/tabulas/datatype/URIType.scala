
package de.tudresden.inf.lat.tabulas.datatype;

import java.util.Objects

/**
 * This models a link.
 *
 */
class URIType extends PrimitiveType {

  val TypeName: String = "URI"

  override def getTypeName(): String = {
    TypeName
  }

  override def isList(): Boolean = {
    false
  }

  override def parse(str: String): URIValue = {
    new URIValue(str)
  }

  def castInstance(value: PrimitiveTypeValue): URIValue = {
    Objects.requireNonNull(value)
    parse(value.render())
  }

  override def hashCode(): Int = {
    getTypeName().hashCode()
  }

  override def equals(obj: Any): Boolean = {
    if (Objects.isNull(obj)) {
      false
    } else {
      (obj.isInstanceOf[URIType])
    }
  }

  override def toString(): String = {
    getTypeName()
  }

}

