
package de.tudresden.inf.lat.tabulas.datatype

import scala.collection.mutable.ArrayBuffer
import java.util.Collections
import scala.collection.mutable.Buffer
import java.util.Objects

import scala.collection.JavaConverters.asScalaBufferConverter

/**
 * This models a list of elements with a parameterized type.
 *
 */
class ParameterizedListValue extends ArrayBuffer[PrimitiveTypeValue] with PrimitiveTypeValue {

  val serialVersionUID: Long = -8983139857000842808L

  val Separator: String = " "

  var parameter: PrimitiveType = null

  /**
   * Constructs a new parameterized list value.
   *
   * @param parameter
   *            primitive type
   */
  def this(parameter: PrimitiveType) = {
    this()
    Objects.requireNonNull(parameter)
    this.parameter = parameter
  }

  /**
   * Constructs a new parameterized list value using another parameterized
   * list value.
   *
   * @param other
   *            parameterized list value
   */
  def this(other: ParameterizedListValue) = {
    this()
    Objects.requireNonNull(other)
    this.parameter = other.getParameter()
  }

  override def getType(): PrimitiveType = {
    return new ParameterizedListType(this.parameter)
  }

  def add(str: String): Unit = {
    this += this.parameter.parse(str)
  }

  override def render(): String = {
    val sbuf: StringBuffer = new StringBuffer()
    val list: Buffer[String] = renderAsList()
    var first: Boolean = true
    for (str: String <- list) {
      if (first) {
        first = false
      } else {
        sbuf.append(Separator)
      }
      sbuf.append(str)
    }
    return sbuf.toString()
  }

  override def renderAsList(): Buffer[String] = {
    val ret: Buffer[String] = new ArrayBuffer[String]()
    this.foreach(elem => ret += elem.render())
    return ret // @FIXME this should be immutable
  }

  override def compareTo(obj: PrimitiveTypeValue): Int = {
    if (obj.isInstanceOf[ParameterizedListValue]) {
      val other: ParameterizedListValue = obj.asInstanceOf[ParameterizedListValue]
      var ret: Int = size - other.size
      if (ret == 0) {
        ret = toString().compareTo(other.toString())
      }
      return ret
    } else {
      return toString().compareTo(obj.toString())
    }
  }

  def getParameter(): PrimitiveType = {
    return this.parameter
  }

}

