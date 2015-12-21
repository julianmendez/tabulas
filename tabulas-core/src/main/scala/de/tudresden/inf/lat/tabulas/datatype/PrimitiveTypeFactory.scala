package de.tudresden.inf.lat.tabulas.datatype;

import java.util.TreeMap

/**
 * This models a factory of primitive types.
 *
 */
class PrimitiveTypeFactory {

  private val map: TreeMap[String, PrimitiveType] = new TreeMap[String, PrimitiveType]()

  private def add(primType: PrimitiveType): Unit = {
    this.map.put(primType.getTypeName(), primType);
  }

  /**
   * Constructs a new primitive type factory.
   */
  {
    add(new StringType())
    add(new ParameterizedListType(new StringType()))
    add(new URIType())
    add(new ParameterizedListType(new URIType()))
  }

  /**
   * Tells whether this factory contains the given primitive type.
   *
   * @param primType
   *            primitive type
   * @return <code>true</code> if and only if this factory contains the given
   *         primitive type
   */
  def contains(primType: String): Boolean = {
    this.map.containsKey(primType)
  }

  /**
   * Returns a new value of the specified type.
   *
   * @param typeName
   *            type name
   * @param value
   *            value
   * @return a new value of the specified type
   */
  def newInstance(typeName: String, value: String): PrimitiveTypeValue = {
    val primType: PrimitiveType = this.map.get(typeName)
    if (primType == null) {
      throw new ParseException("Type '" + typeName + "' is undefined.")
    } else {
      primType.parse(value)
    }
  }

}