
package de.tudresden.inf.lat.tabulas.datatype;

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
		parse(value.render())
	}

  override def hashCode(): Int = {
    getTypeName().hashCode()
  }

  override def equals(obj: Any): Boolean = {
    if (this == obj) {
      true
    } else if (obj == null) {
      false
    } else {
       (obj.isInstanceOf[URIType]) 
    }
  }

  override def toString(): String = {
    getTypeName()
  }

}

