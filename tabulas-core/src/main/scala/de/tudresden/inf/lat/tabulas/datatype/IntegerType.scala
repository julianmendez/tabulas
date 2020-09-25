
package de.tudresden.inf.lat.tabulas.datatype

/** This models the primitive data type Integer.
 *
 */
case class IntegerType() extends PrimitiveType {

  final val TypeName: String = "Integer"

  override val isList: Boolean = false

  override val getTypeName: String = TypeName

  override val toString: String = getTypeName

  def castInstance(value: PrimitiveTypeValue): IntegerValue = parse(value.render)

  override def parse(str: String): IntegerValue = IntegerValue(str)

}

object IntegerType {}
