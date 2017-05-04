
package de.tudresden.inf.lat.tabulas.datatype

import java.util.Collections
import scala.collection.mutable.ArrayBuffer
import scala.collection.mutable.Buffer

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

  override def renderAsList(): Buffer[String] = {
    return new ArrayBuffer[String]() // @FIXME this should be immutable
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

