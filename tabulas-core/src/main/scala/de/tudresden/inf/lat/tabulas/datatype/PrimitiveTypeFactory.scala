package de.tudresden.inf.lat.tabulas.datatype

import scala.collection.mutable

/** This models a factory of primitive types.
  *
  */
class PrimitiveTypeFactory(map: Map[String, PrimitiveType]) {

  /** Tells whether this factory contains the given primitive type.
    *
    * @param primType primitive type
    * @return <code>true</code> if and only if this factory contains the given
    *         primitive type
    */
  def contains(primType: String): Boolean = {
    this.map.get(primType).isDefined
  }

  /** Returns a new value of the specified type.
    *
    * @param typeName type name
    * @param value    value
    * @return a new value of the specified type
    */
  def newInstance(typeName: String, value: String): PrimitiveTypeValue = {
    val optPrimType: Option[PrimitiveType] = this.map.get(typeName)
    val result: PrimitiveTypeValue = if (optPrimType.isEmpty) {
      throw ParseException("Type '" + typeName + "' is undefined.")
    } else {
      optPrimType.get.parse(value)
    }
    result
  }

}

object PrimitiveTypeFactory {

  def apply(): PrimitiveTypeFactory = {
    val map = Seq(
      new EmptyType(),
      new StringType(),
      new ParameterizedListType(new StringType()),
      new URIType(),
      new ParameterizedListType(new URIType()),
      new IntegerType(),
      new ParameterizedListType(new IntegerType()),
      new DecimalType(),
      new ParameterizedListType(new DecimalType())
    )
      .map(primType => (primType.getTypeName, primType))
      .toMap

    new PrimitiveTypeFactory(map)
  }

}
