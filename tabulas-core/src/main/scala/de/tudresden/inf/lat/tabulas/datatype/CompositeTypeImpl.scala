
package de.tudresden.inf.lat.tabulas.datatype

import java.util.Objects

import scala.collection.mutable

/** Default implementation of a composite type.
  *
  */
class CompositeTypeImpl extends CompositeType {

  private val _fields = new mutable.ArrayBuffer[String]
  private val _fieldType = new mutable.TreeMap[String, String]

  override def getFields: Seq[String] = {
    this._fields.toList
  }

  override def getFieldType(field: String): Option[String] = {
    Objects.requireNonNull(field)
    this._fieldType.get(field)
  }

  /** Declares a field.
    *
    * @param field
    * field name
    * @param typeStr
    * type of the field
    */
  def declareField(field: String, typeStr: String): Unit = {
    if (this._fields.contains(field)) {
      throw new ParseException("Field '" + field + "' has been already defined.")
    } else {
      this._fields += field
      this._fieldType.put(field, typeStr)
    }
  }

  override def hashCode(): Int = {
    this._fields.hashCode() + (0x1F * this._fieldType.hashCode())
  }

  override def equals(obj: Any): Boolean = {
    val result = obj match {
      case other: CompositeType =>
        getFields.equals(other.getFields) &&
          getFields.forall(field => getFieldType(field).equals(other.getFieldType(field)))
      case _ => false
    }
    result
  }

  override def toString: String = {
    val sbuf = new StringBuffer()
    this._fields.foreach(field => sbuf.append(field + ":" + this._fieldType.get(field) + " "))
    val result: String = sbuf.toString
    result
  }

}

object CompositeTypeImpl {

  def apply(): CompositeTypeImpl = new CompositeTypeImpl

  /** Constructs a new composite type using another one.
    *
    * @param otherType
    * other type
    */
  def apply(otherType: CompositeType): CompositeTypeImpl = {
    Objects.requireNonNull(otherType)
    val result = new CompositeTypeImpl
    otherType.getFields
      .foreach(field => result.declareField(field, otherType.getFieldType(field).get))
    result
  }

}
