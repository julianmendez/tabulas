
package de.tudresden.inf.lat.tabulas.datatype

import java.util.{Objects, StringTokenizer}

/**
  * This models the type of a list of elements with a parameterized type.
  *
  */
class ParameterizedListType extends PrimitiveType {

  val TypePrefix: String = "List_"

  var parameter: PrimitiveType = _

  def this(parameter: PrimitiveType) = {
    this()
    Objects.requireNonNull(parameter)
    this.parameter = parameter
  }

  override def getTypeName: String = {
    return TypePrefix + this.parameter.getTypeName
  }

  override def isList: Boolean = {
    return true
  }

  override def parse(str: String): ParameterizedListValue = {
    val ret: ParameterizedListValue = new ParameterizedListValue(this.parameter)
    val stok: StringTokenizer = new StringTokenizer(str)
    while (stok.hasMoreTokens) {
      ret += this.parameter.parse(stok.nextToken())
    }
    return ret
  }

  def getParameter: PrimitiveType = {
    return this.parameter
  }

  def castInstance(value: PrimitiveTypeValue): ParameterizedListValue = {
    return parse(value.render())
  }

  override def hashCode(): Int = {
    return this.parameter.hashCode()
  }

  override def equals(obj: Any): Boolean = {
    if (Objects.isNull(obj)) {
      return false
    } else if (obj.isInstanceOf[ParameterizedListType]) {
      val other: ParameterizedListType = obj.asInstanceOf[ParameterizedListType]
      return this.parameter.equals(other.parameter)
    } else {
      return false
    }
  }

  override def toString: String = {
    return getTypeName
  }

}

