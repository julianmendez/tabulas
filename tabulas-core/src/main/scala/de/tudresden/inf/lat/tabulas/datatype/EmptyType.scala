
package de.tudresden.inf.lat.tabulas.datatype

/** This models the primitive data type Empty.
  *
  */
class EmptyType extends PrimitiveType {

  val TypeName: String = "Empty"

  override def getTypeName: String = { TypeName }

  override def isList: Boolean = { false }

  override def parse(str: String): EmptyValue = { new EmptyValue() }

  def castInstance(value: PrimitiveTypeValue): EmptyValue = { parse(value.render()) }

  override def hashCode(): Int = { getTypeName.hashCode() }

  override def equals(obj: Any): Boolean = {
    val result: Boolean = obj match {
      case other: EmptyType => true
      case _ => false
    }
    result
  }

  override def toString: String = { getTypeName }

}

