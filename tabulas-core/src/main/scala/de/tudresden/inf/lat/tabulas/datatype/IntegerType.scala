
package de.tudresden.inf.lat.tabulas.datatype

/** This models the primitive data type Integer.
 *
 */
case class IntegerType() extends PrimitiveType {

  final val TypeName: String = "Integer"

  override def isList: Boolean = {
    false
  }

  def castInstance(value: PrimitiveTypeValue): IntegerValue = {
    parse(value.render())
  }

  override def parse(str: String): IntegerValue = {
    IntegerValue(str)
  }

  override def toString: String = {
    getTypeName
  }

  override def getTypeName: String = {
    TypeName
  }

}

object IntegerType {}
