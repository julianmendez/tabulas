
package de.tudresden.inf.lat.tabulas.datatype

import scala.collection.mutable

/** This models a simplified composite type where the fields have the same type.
  *
  */
class SimplifiedCompositeType extends CompositeType {

  val DefaultFieldType: String = "String"

  private val _dataType: CompositeTypeImpl = new CompositeTypeImpl()

  def this(knownFields: Array[String]) {
    this()
    knownFields.foreach(field => this._dataType.declareField(field, DefaultFieldType))
  }

  override def getFields: mutable.Buffer[String] = {
    return this._dataType.getFields
  }

  override def getFieldType(field: String): Option[String] = {
    return this._dataType.getFieldType(field)
  }

  override def hashCode(): Int = {
    return this._dataType.hashCode()
  }

  override def equals(obj: Any): Boolean = {
    obj match {
      case other: SimplifiedCompositeType => return this._dataType.equals(other._dataType)
      case _ => return false
    }
  }

  override def toString: String = {
    return this._dataType.toString
  }

}

