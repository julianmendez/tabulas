
package de.tudresden.inf.lat.tabulas.datatype

import java.util.Objects

import scala.collection.mutable

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

  override def getFields: mutable.Buffer[String] = {
    return this.dataType.getFields
  }

  override def getFieldType(field: String): Option[String] = {
    return this.dataType.getFieldType(field)
  }

  override def hashCode(): Int = {
    return this.dataType.hashCode()
  }

  override def equals(obj: Any): Boolean = {
    if (Objects.isNull(obj)) {
      return false
    } else if (obj.isInstanceOf[SimplifiedCompositeType]) {
      val other: SimplifiedCompositeType = obj.asInstanceOf[SimplifiedCompositeType]
      return this.dataType.equals(other.dataType)
    } else {
      return false
    }
  }

  override def toString: String = {
    return this.dataType.toString
  }

}

