
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

  override def getTypeName: String = { TypePrefix + this._parameter.getTypeName }

  override def isList: Boolean = { true }

  override def parse(str: String): ParameterizedListValue = {
    val result: ParameterizedListValue = new ParameterizedListValue(this._parameter)
    val stok: StringTokenizer = new StringTokenizer(str)
    while (stok.hasMoreTokens) {
      result += this._parameter.parse(stok.nextToken())
    }
    result
  }

  def getParameter: PrimitiveType = { this._parameter }

  def castInstance(value: PrimitiveTypeValue): ParameterizedListValue = { parse(value.render()) }

  override def hashCode(): Int = { this._parameter.hashCode() }

  override def equals(obj: Any): Boolean = {
    var result: Boolean = false
    obj match {
      case other: ParameterizedListType =>
        result = this._parameter.equals(other._parameter)
      case _ => result = false
    }
    result
  }

  override def toString: String = { getTypeName }

}

