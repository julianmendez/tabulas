
package de.tudresden.inf.lat.tabulas.datatype

import scala.collection.mutable.ArrayBuffer
import java.util.Collections
import scala.collection.mutable.Buffer
import java.util.Map
import java.util.Objects
import java.util.Optional
import java.util.TreeMap

import scala.collection.JavaConverters.asScalaBufferConverter

/**
 * Default implementation of a composite type.
 *
 */
class CompositeTypeImpl extends CompositeType {

  private val fields: Buffer[String] = new ArrayBuffer[String]
  private val fieldType: Map[String, String] = new TreeMap[String, String]

  /**
   * Constructs a new composite type using another one.
   *
   * @param otherType
   *            other type
   */
  def this(otherType: CompositeType) = {
    this()
    Objects.requireNonNull(otherType)
    otherType.getFields().foreach(field => declareField(field, otherType.getFieldType(field).get))
  }

  override def getFields(): Buffer[String] = {
    return this.fields // @FIXME this should be immutable
  }

  override def getFieldType(field: String): Optional[String] = {
    Objects.requireNonNull(field)
    val value: String = this.fieldType.get(field)
    if (Objects.isNull(value)) {
      return Optional.empty()
    } else {
      return Optional.of(value)
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
      this.fields += field
      this.fieldType.put(field, typeStr)
    }
  }

  override def hashCode(): Int = {
    return this.fields.hashCode() + (0x1F * this.fieldType.hashCode())
  }

  override def equals(obj: Any): Boolean = {
    if (obj.isInstanceOf[CompositeType]) {
      val other: CompositeType = obj.asInstanceOf[CompositeType]
      var ret: Boolean = getFields().equals(other.getFields())
      if (ret) {
        val fields: Buffer[String] = getFields()
        ret = ret && fields.forall(field => getFieldType(field).equals(other.getFieldType(field)))
      }
      return ret
    }
    return false
  }

  override def toString(): String = {
    val sbuf: StringBuffer = new StringBuffer()
    this.fields.foreach(field => sbuf.append(field + ":" + this.fieldType.get(field) + " "))
    return sbuf.toString()
  }

}

