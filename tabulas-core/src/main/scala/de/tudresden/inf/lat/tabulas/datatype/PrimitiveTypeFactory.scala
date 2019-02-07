package de.tudresden.inf.lat.tabulas.datatype

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
    map.get(primType).isDefined
  }

  /** Returns the type for the given name
    *
    * @param typeName type name
    * @return
    */
  def getType(typeName: String): Option[PrimitiveType] = {
    map.get(typeName)
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
