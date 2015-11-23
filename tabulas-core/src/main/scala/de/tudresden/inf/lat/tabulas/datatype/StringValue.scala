
package de.tudresden.inf.lat.tabulas.datatype

import java.util.ArrayList
import java.util.Collections
import java.util.List

class StringValue extends PrimitiveTypeValue {

  private var str: String = ""

  def this(str0: String) = {
    this()
    str = if (str0 == null) { "" } else { str0 }
  }

  override def getType(): PrimitiveType = {
    new StringType();
  }

  override def render(): String = {
    str
  }

  override def isEmpty(): Boolean = {
    str.trim().isEmpty()
  }

  override def renderAsList(): List[String] = {
    val ret: ArrayList[String] = new ArrayList[String]()
    ret.add(render())
    Collections.unmodifiableList(ret)

  }

  override def compareTo(other: PrimitiveTypeValue): Int = {
    toString().compareTo(other.toString())
  }

  override def hashCode(): Int = {
    this.str.hashCode()
  }

  override def equals(obj: Any): Boolean = {
    if (this == obj) {
      true
    } else if (obj.isInstanceOf[StringValue]) {
      val other: StringValue = obj.asInstanceOf[StringValue]
      this.str.equals(other.str)
    } else {
      false
    }
  }

  override def toString(): String = {
    this.str
  }

}

