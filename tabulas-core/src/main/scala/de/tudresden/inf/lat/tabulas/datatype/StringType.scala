
package de.tudresden.inf.lat.tabulas.datatype

/** This models the primitive data type String.
 *
 */
case class StringType() extends PrimitiveType {

  final val TypeName: String = "String"

  override val isList: Boolean = false

  override val getTypeName: String = TypeName

  override val toString: String = getTypeName

  def castInstance(value: PrimitiveTypeValue): StringValue = parse(value.render)

  override def parse(str: String): StringValue = new StringValue(str)

}

object StringType {}
