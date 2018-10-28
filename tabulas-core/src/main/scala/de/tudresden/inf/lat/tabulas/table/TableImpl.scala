
package de.tudresden.inf.lat.tabulas.table

import java.util.Objects

import de.tudresden.inf.lat.tabulas.datatype.{CompositeType, CompositeTypeImpl, Record}

import scala.collection.mutable

/** This is the default implementation of a sorted table.
  */
class TableImpl extends Table {

  private var _tableType: CompositeType = CompositeTypeImpl()
  private val _list = new mutable.ArrayBuffer[Record]
  private val _prefixMap: PrefixMap = new PrefixMapImpl()
  private val _sortingOrder = new mutable.ArrayBuffer[String]
  private val _fieldsWithReverseOrder = new mutable.TreeSet[String]()

  def this(newType: CompositeType) = {
    this()
    this._tableType = newType
  }

  def this(other: Table) = {
    this()
    this._tableType = other.getType
    this._list ++= other.getRecords
    other match {
      case otherTable: Table =>
        val otherMap: PrefixMap = otherTable.getPrefixMap
        otherMap.getKeysAsStream.foreach(key => this._prefixMap.put(key, otherMap.get(key).get))
        this._sortingOrder ++= otherTable.getSortingOrder
        this._fieldsWithReverseOrder ++= otherTable.getFieldsWithReverseOrder
    }
  }

  override def getType: CompositeType = {
    this._tableType
  }

  override def setType(newType: CompositeType): Unit = {
    this._tableType = newType
  }

  override def getPrefixMap: PrefixMap = {
    this._prefixMap
  }

  override def setPrefixMap(newPrefixMap: PrefixMap): Unit = {
    this._prefixMap.clear()
    newPrefixMap.getKeysAsStream.foreach(key => this._prefixMap.put(key, newPrefixMap.get(key).get))
  }

  override def add(record: Record): Boolean = {
    val result = if (Objects.isNull(record)) {
      false
    } else {
      this._list += record
      true
    }
    result
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

  override def clear(): Unit = {
    this._list.clear()
  }

  override def hashCode(): Int = {
    val result = this._tableType.hashCode() + 0x1F * (this._prefixMap.hashCode() + 0x1F * (this._sortingOrder.hashCode() +
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

  override def toString: String = {
    val result = this._tableType.toString + " " + this._prefixMap.toString + " " + this._sortingOrder.toString + " " +
      this._fieldsWithReverseOrder.toString + " " + this._list.toString
    result
  }

}

object TableImpl {

  def apply(): TableImpl = new TableImpl

}
