
package de.tudresden.inf.lat.tabulas.table

import java.util.Objects

import de.tudresden.inf.lat.tabulas.datatype.{CompositeType, Record}

import scala.collection.mutable

case class EmptyCompositeType() extends CompositeType {

  override def getFields: Seq[String] = Seq()

  override def getFieldType(field: String): Option[String] = None

}

/** This is the default implementation of a sorted table.
  */
class TableImpl(tableType: CompositeType) extends Table {

  private val _sortingOrder = new mutable.ArrayBuffer[String]
  private val _fieldsWithReverseOrder = new mutable.TreeSet[String]()
  private var _records = new mutable.ArrayBuffer[Record]
  private var _prefixMap: PrefixMap = PrefixMapImpl(Map(), Seq())

  def add(record: Record): TableImpl = {
    this._records += record
    this
  }

  override def hashCode(): Int = {
    val result = tableType.hashCode() + 0x1F * (this._prefixMap.hashCode() + 0x1F * (this._sortingOrder.hashCode() +
      0x1F * (this._fieldsWithReverseOrder.hashCode() + 0x1F * this._records.hashCode())))
    result
  }

  override def equals(obj: Any): Boolean = {
    val result = obj match {
      case other: Table => getType.equals(other.getType) &&
        getPrefixMap.equals(other.getPrefixMap) &&
        getSortingOrder.equals(other.getSortingOrder) &&
        getFieldsWithReverseOrder.equals(other.getFieldsWithReverseOrder) &&
        getRecords.equals(other.getRecords)
      case _ => false
    }
    result
  }

  override def getType: CompositeType = {
    tableType
  }

  override def getPrefixMap: PrefixMap = {
    this._prefixMap
  }

  override def getSortingOrder: Seq[String] = {
    this._sortingOrder
  }

  override def getFieldsWithReverseOrder: Set[String] = {
    this._fieldsWithReverseOrder.toSet
  }

  override def getRecords: Seq[Record] = {
    val comparator = new RecordComparator(this._sortingOrder, this._fieldsWithReverseOrder.toSet)
    val ret = new mutable.ArrayBuffer[Record]
    ret ++= this._records
    val result = ret.sortWith((record0, record1) => comparator.compare(record0, record1) < 0)
    result
  }

  def setPrefixMap(newPrefixMap: PrefixMap): TableImpl = {
    this._prefixMap = newPrefixMap
    this
  }

  def setSortingOrder(sortingOrder: Seq[String]): TableImpl = {
    this._sortingOrder.clear()
    if (Objects.nonNull(sortingOrder)) {
      this._sortingOrder ++= sortingOrder
    }
    this
  }

  def setFieldsWithReverseOrder(fieldsWithReverseOrder: Set[String]): TableImpl = {
    this._fieldsWithReverseOrder.clear()
    if (Objects.nonNull(fieldsWithReverseOrder)) {
      this._fieldsWithReverseOrder ++= fieldsWithReverseOrder
    }
    this
  }

  def setRecords(newRecords: Seq[Record]): TableImpl = {
    _records = new mutable.ArrayBuffer[Record] ++ newRecords
    this
  }

  override def toString: String = {
    val result = "\ndef = " + tableType.toString + "\n\nprefix = " + this._prefixMap.toString +
      "\n\norder = " + this._sortingOrder.toString + " " +
      "\n\nreverseorder = " + this._fieldsWithReverseOrder.toString + "\n\nlist = " + this._records.toString
    result
  }

}

object TableImpl {

  def apply(): TableImpl = new TableImpl(EmptyCompositeType())

  def apply(newType: CompositeType): TableImpl = new TableImpl(newType)

  def apply(other: Table): TableImpl = TableImpl(other.getType, other)

  def apply(newType: CompositeType, other: Table): TableImpl = {
    val result = new TableImpl(newType)
    result._records ++= other.getRecords
    other match {
      case otherTable: Table =>
        result._prefixMap = otherTable.getPrefixMap
        result._sortingOrder ++= otherTable.getSortingOrder
        result._fieldsWithReverseOrder ++= otherTable.getFieldsWithReverseOrder
    }
    result
  }

}
