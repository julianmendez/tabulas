
package de.tudresden.inf.lat.tabulas.datatype

import java.util.Objects

import scala.util.Try

/** Default implementation of a composite type.
  *
  */
case class CompositeTypeImpl(fields: Seq[String], fieldType: Map[String, String]) extends CompositeType {

  /** Declares a field.
    *
    * @param field   field name
    * @param typeStr type of the field
    */
  def declareField(field: String, typeStr: String): Try[CompositeTypeImpl] = Try {
    if (getFields.contains(field)) {
      throw ParseException("Field '" + field + "' has been already defined.")
    } else {
      val list = fields ++ Seq(field)
      val map = fieldType + (field -> typeStr)
      new CompositeTypeImpl(list, map)
    }
  }

  override def getFields: Seq[String] = fields

  override def toString: String = {
    val sbuf = new StringBuffer()
    getFields.foreach(field => sbuf.append(field + ":" + getFieldType(field) + " "))
    val result: String = sbuf.toString
    result
  }

  override def getFieldType(field: String): Option[String] = fieldType.get(field)

}

object CompositeTypeImpl {

  def apply(): CompositeTypeImpl = new CompositeTypeImpl(Seq(), Map())

  /** Constructs a new composite type using another one.
    *
    * @param otherType other type
    */
  def apply(otherType: CompositeType): CompositeTypeImpl = {
    Objects.requireNonNull(otherType)
    val map = otherType.getFields
      .map(field => (field, otherType.getFieldType(field).get))
      .toMap
    new CompositeTypeImpl(otherType.getFields, map)
  }

}
