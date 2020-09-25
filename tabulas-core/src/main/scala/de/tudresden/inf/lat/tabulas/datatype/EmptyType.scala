
package de.tudresden.inf.lat.tabulas.datatype

/** This models the primitive data type Empty.
 *
 */
case class EmptyType() extends PrimitiveType {

  final val TypeName: String = "Empty"

  override val getTypeName: String = TypeName

  override val toString: String = getTypeName

  override val isList: Boolean = false

  def castInstance(value: PrimitiveTypeValue): EmptyValue = parse(value.render)

  override def parse(str: String): EmptyValue = EmptyValue()

}

object EmptyType {}
