
package de.tudresden.inf.lat.tabulas.datatype

import java.util.Objects

import scala.collection.mutable

/** Default implementation of a composite type.
  *
  */
class CompositeTypeImpl extends CompositeType {

  private val _fields = new mutable.ArrayBuffer[String]
  private val _fieldType = new mutable.TreeMap[String, String]

  /** Constructs a new composite type using another one.
    *
    * @param otherType
    * other type
    */
  def this(otherType: CompositeType) = {
    this()
    Objects.requireNonNull(otherType)
    otherType.getFields.foreach(field => declareField(field, otherType.getFieldType(field).get))
  }

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

  override def hashCode(): Int = { this._fields.hashCode() + (0x1F * this._fieldType.hashCode()) }

  override def equals(obj: Any): Boolean = {
    var result: Boolean = false
    obj match {
      case other: CompositeType =>
        result = getFields.equals(other.getFields)
        if (result) {
          val fields: Seq[String] = getFields
          result = result && fields.forall(field => getFieldType(field).equals(other.getFieldType(field)))
        }
      case _ => result = false
    }
    result
  }

  override def toString: String = {
    val sbuf: StringBuffer = new StringBuffer()
    this._fields.foreach(field => sbuf.append(field + ":" + this._fieldType.get(field) + " "))
    val result: String = sbuf.toString
    result
  }

}

