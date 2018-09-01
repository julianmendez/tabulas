
package de.tudresden.inf.lat.tabulas.datatype

/** This models the primitive data type Integer.
  *
  */
class IntegerType extends PrimitiveType {

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

  override def hashCode(): Int = {
    getTypeName.hashCode()
  }

  override def equals(obj: Any): Boolean = {
    val result = obj match {
      case other: IntegerType => true
      case _ => false
    }
    result
  }

  override def toString: String = {
    getTypeName
  }

}

object IntegerType {

  def apply(): IntegerType = new IntegerType

}
