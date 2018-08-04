package de.tudresden.inf.lat.tabulas.datatype

import scala.collection.mutable

/** This models a factory of primitive types.
  *
  */
class PrimitiveTypeFactory {

  private val _map = new mutable.TreeMap[String, PrimitiveType]()

  private def add(primType: PrimitiveType): Unit = {
    this._map.put(primType.getTypeName, primType)
  }

  /** Constructs a new primitive type factory.
    */
  {
    add(new EmptyType())
    add(new StringType())
    add(new ParameterizedListType(new StringType()))
    add(new URIType())
    add(new ParameterizedListType(new URIType()))
    add(new IntegerType())
    add(new ParameterizedListType(new IntegerType()))
    add(new DecimalType())
    add(new ParameterizedListType(new DecimalType()))
  }

  /** Tells whether this factory contains the given primitive type.
    *
    * @param primType primitive type
    * @return <code>true</code> if and only if this factory contains the given
    *         primitive type
    */
  def contains(primType: String): Boolean = {
    this._map.get(primType).isDefined
  }

  /** Returns a new value of the specified type.
    *
    * @param typeName type name
    * @param value    value
    * @return a new value of the specified type
    */
  def newInstance(typeName: String, value: String): PrimitiveTypeValue = {
    val optPrimType: Option[PrimitiveType] = this._map.get(typeName)
    val result: PrimitiveTypeValue = if (optPrimType.isEmpty) {
      throw new ParseException("Type '" + typeName + "' is undefined.")
    } else {
      optPrimType.get.parse(value)
    }
    result
  }

}

object PrimitiveTypeFactory {

  def apply(): PrimitiveTypeFactory = new PrimitiveTypeFactory

}
