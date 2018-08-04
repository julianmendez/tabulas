
package de.tudresden.inf.lat.tabulas.datatype

import scala.collection.mutable

/** This models a empty value.
  *
  */
class EmptyValue extends PrimitiveTypeValue {

  private val Value: String = ""

  override def getType: PrimitiveType = {
    new EmptyType()
  }

  override def isEmpty: Boolean = {
    true
  }

  override def render(): String = {
    Value
  }

  override def renderAsList(): Seq[String] = {
    new mutable.ArrayBuffer[String]().toList
  }

  override def compareTo(other: PrimitiveTypeValue): Int = {
    toString.compareTo(other.toString)
  }

  override def hashCode(): Int = {
    Value.hashCode()
  }

  override def equals(obj: Any): Boolean = {
    var result: Boolean = false
    obj match {
      case other: EmptyValue => result = true
      case _ => result = false
    }
    result
  }

  override def toString: String = {
    Value
  }

}

object EmptyValue {

  def apply(): EmptyValue = new EmptyValue

}
