
package de.tudresden.inf.lat.tabulas.datatype

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
    if (this == obj) {
      true
    } else if (obj == null) {
      false
    } else {
       (obj.isInstanceOf[StringType]) 
    }
  }

  override def toString(): String = {
    getTypeName()
  }

}

