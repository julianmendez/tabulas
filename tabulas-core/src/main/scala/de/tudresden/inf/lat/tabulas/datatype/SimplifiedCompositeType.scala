
package de.tudresden.inf.lat.tabulas.datatype

/** This models a simplified composite type where the fields have the same type.
  *
  */
class SimplifiedCompositeType(dataType: CompositeTypeImpl) extends CompositeType {

  override def getFields: Seq[String] = {
    this.dataType.getFields
  }

  def getDataType: CompositeTypeImpl = {
    dataType
  }

  override def getFieldType(field: String): Option[String] = {
    this.dataType.getFieldType(field)
  }

  override def hashCode(): Int = {
    this.dataType.hashCode()
  }

  override def equals(obj: Any): Boolean = {
    val result: Boolean = obj match {
      case other: SimplifiedCompositeType =>
        this.dataType.equals(other.getDataType)
      case _ =>
        false
    }
    result
  }

  override def toString: String = {
    this.dataType.toString
  }

}

object SimplifiedCompositeType {

  val DefaultFieldType: String = "String"

  def apply(): SimplifiedCompositeType = new SimplifiedCompositeType(new CompositeTypeImpl())

  def apply(knownFields: Array[String]): SimplifiedCompositeType = {
    val compType = new CompositeTypeImpl()
    knownFields.foreach(field => compType.declareField(field, DefaultFieldType))
    new SimplifiedCompositeType(compType)
  }

}
