
package de.tudresden.inf.lat.tabulas.datatype

import java.util.{Objects, StringTokenizer}

/** This models the type of a list of elements with a parameterized type.
  *
  */
class ParameterizedListType extends PrimitiveType {

  val TypePrefix: String = "List_"

  private var _parameter: PrimitiveType = _

  def this(parameter: PrimitiveType) = {
    this()
    Objects.requireNonNull(parameter)
    this._parameter = parameter
  }

  override def getTypeName: String = {
    return TypePrefix + this._parameter.getTypeName
  }

  override def isList: Boolean = {
    return true
  }

  override def parse(str: String): ParameterizedListValue = {
    val result: ParameterizedListValue = new ParameterizedListValue(this._parameter)
    val stok: StringTokenizer = new StringTokenizer(str)
    while (stok.hasMoreTokens) {
      result += this._parameter.parse(stok.nextToken())
    }

    return result
  }

  def getParameter: PrimitiveType = {
    return this._parameter
  }

  def castInstance(value: PrimitiveTypeValue): ParameterizedListValue = {
    return parse(value.render())
  }

  override def hashCode(): Int = {
    return this._parameter.hashCode()
  }

  override def equals(obj: Any): Boolean = {
    var result: Boolean = false
    obj match {
      case other: ParameterizedListType =>
        result = this._parameter.equals(other._parameter)
      case _ => result = false
    }

    return result
  }

  override def toString: String = {
    return getTypeName
  }

}

