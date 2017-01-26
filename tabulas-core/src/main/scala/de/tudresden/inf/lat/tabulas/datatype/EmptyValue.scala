
package de.tudresden.inf.lat.tabulas.datatype

import java.util.Collections
import java.util.List

/**
 * This models a empty value.
 *
 */
class EmptyValue extends PrimitiveTypeValue {

  private val Value: String = ""

  override def getType(): PrimitiveType = {
    return new EmptyType()
  }

  override def isEmpty(): Boolean = {
    return true
  }

  override def render(): String = {
    return Value
  }

  override def renderAsList(): List[String] = {
    return Collections.emptyList()
  }

  override def compareTo(other: PrimitiveTypeValue): Int = {
    return toString().compareTo(other.toString())
  }

  override def hashCode(): Int = {
    return Value.hashCode()
  }

  override def equals(obj: Any): Boolean = {
    if (obj.isInstanceOf[EmptyValue]) {
      return true;
    } else {
      return false
    }
  }

  override def toString(): String = {
    return Value
  }

}

