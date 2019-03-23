
package de.tudresden.inf.lat.tabulas.datatype

/** This models the primitive data type Integer.
  *
  */
case class IntegerType() extends PrimitiveType {

  val TypeName: String = "Integer"

  override def getTypeName: String = {
    TypeName
  }

  override def isList: Boolean = {
    false
  }

  override def parse(str: String): IntegerValue = {
    IntegerValue(str)
  }

  def castInstance(value: PrimitiveTypeValue): IntegerValue = {
    parse(value.render())
  }

  override def toString: String = {
    getTypeName
  }

}
