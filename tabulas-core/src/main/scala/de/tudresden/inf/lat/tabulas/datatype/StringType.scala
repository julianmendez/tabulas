
package de.tudresden.inf.lat.tabulas.datatype

/** This models the primitive data type String.
 *
 */
case class StringType() extends PrimitiveType {

  final val TypeName: String = "String"

  override def isList: Boolean = {
    false
  }

  def castInstance(value: PrimitiveTypeValue): StringValue = {
    parse(value.render())
  }

  override def parse(str: String): StringValue = {
    new StringValue(str)
  }

  override def toString: String = {
    getTypeName
  }

  override def getTypeName: String = {
    TypeName
  }

}

object StringType {}
