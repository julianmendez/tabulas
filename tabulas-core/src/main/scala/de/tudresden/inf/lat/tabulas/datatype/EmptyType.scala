
package de.tudresden.inf.lat.tabulas.datatype

/** This models the primitive data type Empty.
  *
  */
case class EmptyType() extends PrimitiveType {

  final val TypeName: String = "Empty"

  override def isList: Boolean = {
    false
  }

  def castInstance(value: PrimitiveTypeValue): EmptyValue = {
    parse(value.render())
  }

  override def parse(str: String): EmptyValue = {
    EmptyValue()
  }

  override def toString: String = {
    getTypeName
  }

  override def getTypeName: String = {
    TypeName
  }

}

object EmptyType {}
