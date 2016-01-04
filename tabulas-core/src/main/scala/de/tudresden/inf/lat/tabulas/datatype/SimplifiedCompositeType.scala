
package de.tudresden.inf.lat.tabulas.datatype

import java.util.List

/**
 * This models a simplified composite type where the fields have the same type.
 *
 */
class SimplifiedCompositeType extends CompositeType {

  val DefaultFieldType: String = "String"

  private val dataType: CompositeTypeImpl = new CompositeTypeImpl()

  def this(knownFields: Array[String]) {
    this()
    knownFields.foreach(field => this.dataType.declareField(field, DefaultFieldType))
  }

  override def getFields(): List[String] = {
    this.dataType.getFields()
  }

  override def getFieldType(field: String): String = {
    this.dataType.getFieldType(field)
  }

  override def hashCode(): Int = {
    this.dataType.hashCode()
  }

  override def equals(obj: Any): Boolean = {
    if (this == obj) {
      true
    } else if (obj == null) {
      false
    } else if (obj.isInstanceOf[SimplifiedCompositeType]) {
      val other: SimplifiedCompositeType = obj.asInstanceOf[SimplifiedCompositeType]
      (this.dataType.equals(other.dataType))
    } else {
      false
    }
  }

  override def toString(): String = {
    this.dataType.toString()
  }

}

