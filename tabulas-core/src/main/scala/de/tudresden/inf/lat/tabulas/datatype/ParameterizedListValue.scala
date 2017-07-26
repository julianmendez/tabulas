
package de.tudresden.inf.lat.tabulas.datatype

import java.util.Objects

import scala.collection.mutable
import scala.collection.mutable.ArrayBuffer

/**
  * This models a list of elements with a parameterized type.
  *
  */
class ParameterizedListValue extends ArrayBuffer[PrimitiveTypeValue] with PrimitiveTypeValue {

  val serialVersionUID: Long = -8983139857000842808L

  val Separator: String = " "

  private var _parameter: PrimitiveType = _

  /**
    * Constructs a new parameterized list value.
    *
    * @param parameter
    * primitive type
    */
  def this(parameter: PrimitiveType) = {
    this()
    Objects.requireNonNull(parameter)
    this._parameter = parameter
  }

  /**
    * Constructs a new parameterized list value using another parameterized
    * list value.
    *
    * @param other
    * parameterized list value
    */
  def this(other: ParameterizedListValue) = {
    this()
    Objects.requireNonNull(other)
    this._parameter = other.getParameter
  }

  override def getType: PrimitiveType = {
    return new ParameterizedListType(this._parameter)
  }

  def add(str: String): Unit = {
    this += this._parameter.parse(str)
  }

  override def render(): String = {
    val sbuf: StringBuffer = new StringBuffer()
    val list: mutable.Buffer[String] = renderAsList()
    var first: Boolean = true
    for (str: String <- list) {
      if (first) {
        first = false
      } else {
        sbuf.append(Separator)
      }
      sbuf.append(str)
    }
    val result: String = sbuf.toString

    return result
  }

  override def renderAsList(): mutable.Buffer[String] = {
    val list: mutable.Buffer[String] = new ArrayBuffer[String]()
    this.foreach(elem => list += elem.render())
    val result: mutable.Buffer[String] = list // @FIXME this should be immutable

    return result
  }

  override def compareTo(obj: PrimitiveTypeValue): Int = {
    var result: Int = 0
    if (obj.isInstanceOf[ParameterizedListValue]) {
      val other: ParameterizedListValue = obj.asInstanceOf[ParameterizedListValue]
      result = size - other.size
      if (result == 0) {
        result = toString.compareTo(other.toString)
      }
    } else {
      result = toString.compareTo(obj.toString)
    }

    return result
  }

  def getParameter: PrimitiveType = {
    return this._parameter
  }

}

