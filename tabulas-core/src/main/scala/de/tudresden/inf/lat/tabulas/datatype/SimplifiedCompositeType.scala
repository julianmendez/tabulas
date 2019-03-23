
package de.tudresden.inf.lat.tabulas.datatype

/** This models a simplified composite type where the fields have the same type.
  *
  */
case class SimplifiedCompositeType(dataType: CompositeTypeImpl) extends CompositeType {

  override def getFields: Seq[String] = {
    dataType.getFields
  }

  def getDataType: CompositeTypeImpl = {
    dataType
  }

  override def getFieldType(field: String): Option[String] = {
    dataType.getFieldType(field)
  }

  override def toString: String = {
    dataType.toString
  }

}

object SimplifiedCompositeType {

  final val DefaultFieldType: String = "String"

  def apply(): SimplifiedCompositeType = new SimplifiedCompositeType(CompositeTypeImpl())

  def apply(knownFields: Array[String]): SimplifiedCompositeType = {
    val compositeType = knownFields
      .foldLeft(CompositeTypeImpl())((compType, field) => compType.declareField(field, DefaultFieldType).get)
    new SimplifiedCompositeType(compositeType)
  }

}
