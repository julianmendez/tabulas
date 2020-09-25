
package de.tudresden.inf.lat.tabulas.table

import de.tudresden.inf.lat.tabulas.datatype.{PrimitiveTypeValue, Record}

/** This is the default implementation of a record.
 *
 */
case class RecordImpl(map: Map[String, PrimitiveTypeValue]) extends Record {

  override val getProperties: Seq[String] = map.keys.toSeq

  override val toString: String = map.toString

  override def get(key: String): Option[PrimitiveTypeValue] = {
    map.get(key)
  }

  override def getMap: Map[String, PrimitiveTypeValue] = map

  def set(key: String, value: PrimitiveTypeValue): RecordImpl = {
    RecordImpl(map ++ Seq((key, value)))
  }


}

object RecordImpl {

  def apply(): RecordImpl = {
    RecordImpl(Map[String, PrimitiveTypeValue]())
  }

  /** Constructs a new record using another one.
   *
   * @param otherRecord
   * other record
   */
  def apply(otherRecord: Record): RecordImpl = {
    RecordImpl(otherRecord.getMap)
  }

}
