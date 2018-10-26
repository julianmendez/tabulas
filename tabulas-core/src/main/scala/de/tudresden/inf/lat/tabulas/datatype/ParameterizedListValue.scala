
package de.tudresden.inf.lat.tabulas.datatype

import java.util.Objects

import scala.collection.mutable

/** This models a list of elements with a parameterized type.
  *
  */
class ParameterizedListValue(parameter: PrimitiveType)
  extends mutable.ArrayBuffer[PrimitiveTypeValue] with PrimitiveTypeValue {

  val serialVersionUID: Long = -8983139857000842808L

  val Separator: String = " "

  override def getType: PrimitiveType = {
    new ParameterizedListType(this.parameter)
  }

  def add(str: String): Unit = {
    this += this.parameter.parse(str)
  }

  override def render(): String = {
    val sbuf= new StringBuffer()
    val list = renderAsList()
    var first= true
    list.foreach(str => {
      if (first) {
        first = false
      } else {
        sbuf.append(Separator)
      }
      sbuf.append(str)
    })
    val result = sbuf.toString
    result
  }

  override def renderAsList(): Seq[String] = {
    val list = new mutable.ArrayBuffer[String]()
    this.foreach(elem => list += elem.render())
    val result = list.toList
    result
  }

  override def compareTo(obj: PrimitiveTypeValue): Int = {
    val result = obj match {
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

  def getParameter: PrimitiveType = {
    this.parameter
  }

}

object ParameterizedListValue {

  /** Constructs a new parameterized list value.
    *
    * @param parameter primitive type
    */
  def apply(parameter: PrimitiveType): ParameterizedListValue = {
    Objects.requireNonNull(parameter)
    new ParameterizedListValue(parameter)
  }

  /** Constructs a new parameterized list value using another parameterized
    * list value.
    *
    * @param other parameterized list value
    */
  def apply(other: ParameterizedListValue): ParameterizedListValue = {
    Objects.requireNonNull(other)
    new ParameterizedListValue(other.getParameter)
  }

}
