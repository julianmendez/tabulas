
package de.tudresden.inf.lat.tabulas.datatype

import java.util.ArrayList
import java.util.Collections
import java.util.List

import scala.collection.JavaConversions.asScalaBuffer

/**
 * This models a list of elements with a parameterized type.
 *
 */
class ParameterizedListValue extends ArrayList[PrimitiveTypeValue] with PrimitiveTypeValue {

  val serialVersionUID: Long = -8983139857000842808L

  val Separator: String = " "

  var parameter: PrimitiveType = null

  def this(parameter0: PrimitiveType) = {
    this()
    if (parameter0 == null) {
      throw new IllegalArgumentException("Null argument.")
    }
    this.parameter = parameter0
  }

  override def getType(): PrimitiveType = {
    new ParameterizedListType(this.parameter)
  }

  def add(str: String): Unit = {
    super.add(this.parameter.parse(str))
  }

  override def render(): String = {
    val sbuf: StringBuffer = new StringBuffer()
    val list: List[String] = renderAsList()
    var first: Boolean = true
    for (str: String <- list) {
      if (first) {
        first = false
      } else {
        sbuf.append(Separator)
      }
      sbuf.append(str)
    }
    sbuf.toString()
  }

  override def renderAsList(): List[String] = {
    val ret: ArrayList[String] = new ArrayList[String]()
    for (elem: PrimitiveTypeValue <- this) {
      ret.add(elem.render())
    }
    Collections.unmodifiableList(ret)
  }

  override def compareTo(obj: PrimitiveTypeValue): Int = {
    if (obj.isInstanceOf[ParameterizedListValue]) {
      val other: ParameterizedListValue = obj.asInstanceOf[ParameterizedListValue]
      var ret: Int = size() - other.size()
      if (ret == 0) {
        ret = toString().compareTo(other.toString())
      }
      ret
    } else {
      toString().compareTo(obj.toString())
    }
  }

  def getParameter(): PrimitiveType = {
    this.parameter
  }

}

