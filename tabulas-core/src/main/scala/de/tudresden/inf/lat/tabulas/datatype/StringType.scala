
package de.tudresden.inf.lat.tabulas.datatype

/** This models the primitive data type String.
  *
  */
class StringType extends PrimitiveType {

  val TypeName: String = "String"

  override def getTypeName: String = { TypeName }

  override def isList: Boolean = { false }

  override def parse(str: String): StringValue = { new StringValue(str) }

  def castInstance(value: PrimitiveTypeValue): StringValue = { parse(value.render()) }

  override def hashCode(): Int = { getTypeName.hashCode() }

  override def equals(obj: Any): Boolean = {
    val result: Boolean = obj match {
      case other: StringType => true
      case _ => false
    }
    result
  }

  override def toString: String = { getTypeName }

}

