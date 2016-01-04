
package de.tudresden.inf.lat.tabulas.datatype

import java.util.ArrayList
import java.util.Collections
import java.util.List
import java.util.Map
import java.util.TreeMap

import scala.collection.JavaConversions.asScalaBuffer

/**
 * Default implementation of a composite type.
 *
 */
class CompositeTypeImpl extends CompositeType {

  private val fields: List[String] = new ArrayList[String]
  private val fieldType: Map[String, String] = new TreeMap[String, String]

  override def getFields(): List[String] = {
    Collections.unmodifiableList(this.fields)
  }

  override def getFieldType(field: String): String = {
    if (field == null) {
      null
    } else {
      this.fieldType.get(field)
    }
  }

  /**
   * Declares a field.
   *
   * @param field
   *            field name
   * @param typeStr
   *            type of the field
   */
  def declareField(field: String, typeStr: String): Unit = {
    if (this.fields.contains(field)) {
      throw new ParseException("Field '" + field + "' has been already defined.")
    } else {
      this.fields.add(field)
      this.fieldType.put(field, typeStr)
    }
  }

  override def hashCode(): Int = {
    this.fields.hashCode() + (0x1F * this.fieldType.hashCode())
  }

  override def equals(obj: Any): Boolean = {
    if (this == obj) {
      true
    }
    if (obj.isInstanceOf[CompositeType]) {
      val other: CompositeType = obj.asInstanceOf[CompositeType]
      var ret: Boolean = getFields().equals(other.getFields())
      if (ret) {
        val fields: List[String] = getFields()
        ret = ret && fields.forall(field => getFieldType(field).equals(other.getFieldType(field)))
      }
      ret
    }
    false
  }

  override def toString(): String = {
    val sbuf: StringBuffer = new StringBuffer()
    this.fields.foreach(field => sbuf.append(field + ":" + this.fieldType.get(field) + " "))
    sbuf.toString()
  }

}


