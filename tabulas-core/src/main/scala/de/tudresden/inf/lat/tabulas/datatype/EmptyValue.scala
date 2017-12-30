
package de.tudresden.inf.lat.tabulas.datatype

import scala.collection.mutable
import scala.collection.mutable.ArrayBuffer

/** This models a empty value.
  *
  */
class EmptyValue extends PrimitiveTypeValue {

  private val Value: String = ""

  override def getType: PrimitiveType = {
    return new EmptyType()
  }

  override def isEmpty: Boolean = {
    return true
  }

  override def render(): String = {
    return Value
  }

  override def renderAsList(): mutable.Buffer[String] = {
    return new ArrayBuffer[String]() // @FIXME this should be immutable
  }

  override def compareTo(other: PrimitiveTypeValue): Int = {
    return toString.compareTo(other.toString)
  }

  override def hashCode(): Int = {
    return Value.hashCode()
  }

  override def equals(obj: Any): Boolean = {
    var result: Boolean = false
    obj match {
      case other: EmptyValue => result = true
      case _ => result = false
    }

    return result
  }

  override def toString: String = {
    return Value
  }

}

