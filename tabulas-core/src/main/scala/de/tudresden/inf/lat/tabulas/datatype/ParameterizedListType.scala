
package de.tudresden.inf.lat.tabulas.datatype

import java.util.StringTokenizer;
import java.util.Objects

/**
 * This models the type of a list of elements with a parameterized type.
 *
 */
class ParameterizedListType extends PrimitiveType {

  val TypePrefix: String = "List_";

  var parameter: PrimitiveType = null

  def this(parameter0: PrimitiveType) = {
    this()
    if (parameter0 == null) {
      throw new IllegalArgumentException("Null argument.")
    }
    this.parameter = parameter0
  }

  override def getTypeName(): String = {
    TypePrefix + this.parameter.getTypeName()
  }

  override def isList(): Boolean = {
    true
  }

  override def parse(str: String): ParameterizedListValue = {
    val ret: ParameterizedListValue = new ParameterizedListValue(this.parameter);
    val stok: StringTokenizer = new StringTokenizer(str)
    while (stok.hasMoreTokens()) {
      ret.add(this.parameter.parse(stok.nextToken()))
    }
    ret
  }

  def getParameter(): PrimitiveType = {
    this.parameter
  }

  def castInstance(value: PrimitiveTypeValue): ParameterizedListValue = {
    parse(value.render())
  }

  override def hashCode(): Int = {
    this.parameter.hashCode()
  }

  override def equals(obj: Any): Boolean = {
    if (Objects.isNull(obj)) {
      false
    } else if (obj.isInstanceOf[ParameterizedListType]) {
      val other: ParameterizedListType = obj.asInstanceOf[ParameterizedListType]
      (this.parameter.equals(other.parameter))
    } else {
      false
    }
  }

  override def toString(): String = {
    getTypeName()
  }

}

