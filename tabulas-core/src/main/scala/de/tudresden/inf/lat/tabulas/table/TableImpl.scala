
package de.tudresden.inf.lat.tabulas.table

import de.tudresden.inf.lat.tabulas.datatype.{CompositeType, Record}

import scala.collection.mutable


case class EmptyCompositeType() extends CompositeType {

  override val getFields: Seq[String] = Seq()

  override def getFieldType(field: String): Option[String] = None

}

object EmptyCompositeType {}


/** This is the default implementation of a sorted table.
 */
case class TableImpl(
                      tableType: CompositeType,
                      prefixMap: PrefixMap,
                      sortingOrder: Seq[String],
                      fieldsWithReverseOrder: Set[String],
                      records: Seq[Record]
                    ) extends Table {

  override val getType: CompositeType = tableType

  override val getPrefixMap: PrefixMap = prefixMap

  override val getSortingOrder: Seq[String] = sortingOrder

  override val getFieldsWithReverseOrder: Set[String] = fieldsWithReverseOrder

  override val toString: String = {
    "\ndef = " + tableType.toString + "\n\nprefix = " + prefixMap.toString +
      "\n\norder = " + sortingOrder.toString + " " +
      "\n\nreverseorder = " + fieldsWithReverseOrder.toString + "\n\nlist = " + records.toString
  }

  def add(record: Record): TableImpl = {
    copy(tableType, prefixMap, sortingOrder, fieldsWithReverseOrder, records ++ Seq(record))
  }

  override def getRecords: Seq[Record] = {
    val comparator = new RecordComparator(sortingOrder, fieldsWithReverseOrder)
    val ret = new mutable.ArrayBuffer[Record]
    ret ++= records
    ret.sortWith((record0, record1) => comparator.compare(record0, record1) < 0).toSeq
  }

}

object TableImpl {

  def apply(): TableImpl = {
    TableImpl(EmptyCompositeType())
  }

  def apply(newType: CompositeType): TableImpl = {
    TableImpl(newType, PrefixMapImpl(), Seq(), Set(), Seq())
  }

  def apply(newType: CompositeType, other: Table): TableImpl = {
    TableImpl(newType, other.getPrefixMap, other.getSortingOrder, other.getFieldsWithReverseOrder, other.getRecords)
  }

  def apply(other: Table): TableImpl = {
    TableImpl(other.getType, other.getPrefixMap, other.getSortingOrder, other.getFieldsWithReverseOrder, other.getRecords)
  }

}
