
package de.tudresden.inf.lat.tabulas.datatype

import java.util.Objects

import scala.collection.mutable
import scala.collection.mutable.ArrayBuffer

/** This models a list of elements with a parameterized type.
  *
  */
class ParameterizedListValue extends ArrayBuffer[PrimitiveTypeValue] with PrimitiveTypeValue {

  val serialVersionUID: Long = -8983139857000842808L

  val Separator: String = " "

  private var _parameter: PrimitiveType = _

  /** Constructs a new parameterized list value.
    *
    * @param parameter
    * primitive type
    */
  def this(parameter: PrimitiveType) = {
    this()
    Objects.requireNonNull(parameter)
    this._parameter = parameter
  }

  /** Constructs a new parameterized list value using another parameterized
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

  override def getType: PrimitiveType = { new ParameterizedListType(this._parameter) }

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
    result
  }

  override def renderAsList(): mutable.Buffer[String] = {
    val list: mutable.Buffer[String] = new ArrayBuffer[String]()
    this.foreach(elem => list += elem.render())
    val result: mutable.Buffer[String] = list // @FIXME this should be immutable
    result
  }

  override def compareTo(obj: PrimitiveTypeValue): Int = {
    var result: Int = obj match {
      case other: ParameterizedListValue =>
        val diff: Int = size - other.size
        if (diff == 0) {
          toString.compareTo(other.toString)
        } else {
          diff
        }
      case _ =>
        toString.compareTo(obj.toString)
    }
    result
  }

  def getParameter: PrimitiveType = { this._parameter }

}

