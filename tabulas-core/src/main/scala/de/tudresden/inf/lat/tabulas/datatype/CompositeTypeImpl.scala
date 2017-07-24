
package de.tudresden.inf.lat.tabulas.datatype

import java.util.Objects

import scala.collection.mutable
import scala.collection.mutable.ArrayBuffer

/**
  * Default implementation of a composite type.
  *
  */
class CompositeTypeImpl extends CompositeType {

  private val _fields: mutable.Buffer[String] = new ArrayBuffer[String]
  private val _fieldType: mutable.Map[String, String] = new mutable.TreeMap[String, String]

  /**
    * Constructs a new composite type using another one.
    *
    * @param otherType
    * other type
    */
  def this(otherType: CompositeType) = {
    this()
    Objects.requireNonNull(otherType)
    otherType.getFields.foreach(field => declareField(field, otherType.getFieldType(field).get))
  }

  override def getFields: mutable.Buffer[String] = {
    return this._fields // @FIXME this should be immutable
  }

  override def getFieldType(field: String): Option[String] = {
    Objects.requireNonNull(field)
    this._fieldType.get(field)
  }

  /**
    * Declares a field.
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
    return this._fields.hashCode() + (0x1F * this._fieldType.hashCode())
  }

  override def equals(obj: Any): Boolean = {
    obj match {
      case other: CompositeType => {
        var ret: Boolean = getFields.equals(other.getFields)
        if (ret) {
          val fields: mutable.Buffer[String] = getFields
          ret = ret && fields.forall(field => getFieldType(field).equals(other.getFieldType(field)))
        }
        return ret
      }
      case _ => return false
    }
  }

  override def toString: String = {
    val sbuf: StringBuffer = new StringBuffer()
    this._fields.foreach(field => sbuf.append(field + ":" + this._fieldType.get(field) + " "))
    return sbuf.toString
  }

}

