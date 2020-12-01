package de.tudresden.inf.lat.tabulas.datatype

/** This models a factory of primitive types.
 *
 */
case class PrimitiveTypeFactory(map: Map[String, PrimitiveType]) {

  /** Tells whether this factory contains the given primitive type.
   *
   * @param primType primitive type
   * @return <code>true</code> if and only if this factory contains the given
   *         primitive type
   */
  def contains(primType: String): Boolean = map.contains(primType)


  /** Returns the type for the given name
   *
   * @param typeName type name
   * @return the type
   */
  def getType(typeName: String): Option[PrimitiveType] = map.get(typeName)


}

object PrimitiveTypeFactory {

  def apply(): PrimitiveTypeFactory = {
    val map = Seq(
      EmptyType(),
      StringType(),
      ParameterizedListType(StringType()),
      URIType(),
      ParameterizedListType(URIType()),
      IntegerType(),
      ParameterizedListType(IntegerType()),
      DecimalType(),
      ParameterizedListType(DecimalType())
    )
      .map(primType => (primType.getTypeName, primType))
      .toMap

    new PrimitiveTypeFactory(map)
  }

}
