
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

  private val _list = new mutable.ArrayBuffer[Record]
  private val _prefixMap: PrefixMap = new PrefixMapImpl()
  private val _sortingOrder = new mutable.ArrayBuffer[String]
  private val _fieldsWithReverseOrder = new mutable.TreeSet[String]()

  override def add(record: Record): Boolean = {
    val result = if (Objects.isNull(record)) {
      false
    } else {
      this._list += record
      true
    }
    result
  }

  override def clear(): Unit = {
    this._list.clear()
  }

  override def hashCode(): Int = {
    val result = tableType.hashCode() + 0x1F * (this._prefixMap.hashCode() + 0x1F * (this._sortingOrder.hashCode() +
      0x1F * (this._fieldsWithReverseOrder.hashCode() + 0x1F * this._list.hashCode())))
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

  override def setPrefixMap(newPrefixMap: PrefixMap): Unit = {
    this._prefixMap.clear()
    newPrefixMap.getKeysAsStream.foreach(key => this._prefixMap.put(key, newPrefixMap.get(key).get))
  }

  override def getSortingOrder: Seq[String] = {
    this._sortingOrder
  }

  override def setSortingOrder(sortingOrder: Seq[String]): Unit = {
    this._sortingOrder.clear()
    if (Objects.nonNull(sortingOrder)) {
      this._sortingOrder ++= sortingOrder
    }
  }

  override def getFieldsWithReverseOrder: Set[String] = {
    this._fieldsWithReverseOrder.toSet
  }

  override def setFieldsWithReverseOrder(fieldsWithReverseOrder: Set[String]): Unit = {
    this._fieldsWithReverseOrder.clear()
    if (Objects.nonNull(fieldsWithReverseOrder)) {
      this._fieldsWithReverseOrder ++= fieldsWithReverseOrder
    }
  }

  override def getRecords: Seq[Record] = {
    val comparator = new RecordComparator(this._sortingOrder, this._fieldsWithReverseOrder.toSet)
    val ret = new mutable.ArrayBuffer[Record]
    ret ++= this._list
    val result = ret.sortWith((record0, record1) => comparator.compare(record0, record1) < 0)
    result
  }

  override def toString: String = {
    val result = "\ndef = " + tableType.toString + "\n\nprefix = " + this._prefixMap.toString +
      "\n\norder = " + this._sortingOrder.toString + " " +
      "\n\nreverseorder = " + this._fieldsWithReverseOrder.toString + "\n\nlist = " + this._list.toString
    result
  }

}

object TableImpl {

  def apply(): TableImpl = new TableImpl(EmptyCompositeType())

  def apply(newType: CompositeType): TableImpl = new TableImpl(newType)

  def apply(other: Table): TableImpl = TableImpl(other.getType, other)

  def apply(newType: CompositeType, other: Table): TableImpl = {
    val result = new TableImpl(newType)
    result._list ++= other.getRecords
    other match {
      case otherTable: Table =>
        val otherMap: PrefixMap = otherTable.getPrefixMap
        otherMap.getKeysAsStream.foreach(key => result._prefixMap.put(key, otherMap.get(key).get))
        result._sortingOrder ++= otherTable.getSortingOrder
        result._fieldsWithReverseOrder ++= otherTable.getFieldsWithReverseOrder
    }
    result
  }

}
