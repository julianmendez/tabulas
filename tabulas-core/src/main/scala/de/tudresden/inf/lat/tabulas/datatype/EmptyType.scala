
package de.tudresden.inf.lat.tabulas.datatype

/** This models the primitive data type Empty.
  *
  */
case class EmptyType() extends PrimitiveType {

  final val TypeName: String = "Empty"

  override def getTypeName: String = {
    TypeName
  }

  override def isList: Boolean = {
    false
  }

  override def parse(str: String): EmptyValue = {
    EmptyValue()
  }

  def castInstance(value: PrimitiveTypeValue): EmptyValue = {
    parse(value.render())
  }

  override def toString: String = {
    getTypeName
  }

}
