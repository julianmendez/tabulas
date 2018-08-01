
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

  override def getFields: Seq[String] = { this._dataType.getFields }

  override def getFieldType(field: String): Option[String] = { this._dataType.getFieldType(field) }

  override def hashCode(): Int = { this._dataType.hashCode() }

  override def equals(obj: Any): Boolean = {
    val result: Boolean = obj match {
      case other: SimplifiedCompositeType =>
        this._dataType.equals(other._dataType)
      case _ =>
        false
    }
    result
  }

  override def toString: String = { this._dataType.toString }

}

