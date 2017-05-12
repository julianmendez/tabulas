
package de.tudresden.inf.lat.tabulas.table

import scala.collection.mutable.ArrayBuffer
import scala.collection.mutable
import java.util.Objects
import scala.collection.mutable.Set
import scala.collection.mutable.TreeSet

import de.tudresden.inf.lat.tabulas.datatype.CompositeType
import de.tudresden.inf.lat.tabulas.datatype.CompositeTypeImpl
import de.tudresden.inf.lat.tabulas.datatype.Record

/**
  * This is the default implementation of a sorted table.
  */
class TableImpl extends Table {

  private var tableType: CompositeType = new CompositeTypeImpl()
  private val list: mutable.Buffer[Record] = new ArrayBuffer[Record]
  private val sortingOrder: mutable.Buffer[String] = new ArrayBuffer[String]
  private val fieldsWithReverseOrder: Set[String] = new TreeSet[String]()

  def this(newType: CompositeType) = {
    this()
    this.tableType = newType
  }

  def this(other: Table) = {
    this()
    this.tableType = other.getType()
    this.list ++= other.getRecords()
    if (other.isInstanceOf[Table]) {
      val otherTable: Table = other.asInstanceOf[Table]
      this.sortingOrder ++= otherTable.getSortingOrder()
      this.fieldsWithReverseOrder ++= otherTable.getFieldsWithReverseOrder()
    }
  }

  override def getType(): CompositeType = {
    return this.tableType
  }

  override def setType(newType: CompositeType): Unit = {
    this.tableType = newType
  }

  override def add(record: Record): Boolean = {
    if (Objects.isNull(record)) {
      return false
    } else {
      this.list += record
      return true
    }
  }

  override def getSortingOrder(): mutable.Buffer[String] = {
    return this.sortingOrder
  }

  override def setSortingOrder(sortingOrder: mutable.Buffer[String]): Unit = {
    this.sortingOrder.clear()
    if (Objects.nonNull(sortingOrder)) {
      this.sortingOrder ++= sortingOrder
    }
  }

  override def getFieldsWithReverseOrder(): Set[String] = {
    return this.fieldsWithReverseOrder
  }

  override def setFieldsWithReverseOrder(fieldsWithReverseOrder: Set[String]): Unit = {
    this.fieldsWithReverseOrder.clear()
    if (Objects.nonNull(fieldsWithReverseOrder)) {
      this.fieldsWithReverseOrder ++= fieldsWithReverseOrder
    }
  }

  override def getRecords(): mutable.Buffer[Record] = {
    val comparator = new RecordComparator(this.sortingOrder, this.fieldsWithReverseOrder)
    val ret: mutable.Buffer[Record] = new ArrayBuffer[Record]
    ret ++= this.list
    return ret.sortWith((record0, record1) => comparator.compare(record0, record1) < 0)
  }

  override def clear(): Unit = {
    return this.list.clear()
  }

  override def hashCode(): Int = {
    return this.sortingOrder.hashCode() + 0x1F * (this.fieldsWithReverseOrder.hashCode() + 0x1F * (this.list.hashCode() + 0x1F * this.tableType.hashCode()))
  }

  override def equals(obj: Any): Boolean = {
    if (obj.isInstanceOf[Table]) {
      val other: Table = obj.asInstanceOf[Table]
      return getSortingOrder().equals(other.getSortingOrder()) &&
        getFieldsWithReverseOrder().equals(other.getFieldsWithReverseOrder()) &&
        getType().equals(other.getType()) &&
        getRecords().equals(other.getRecords())
    } else {
      return false
    }
  }

  override def toString(): String = {
    return this.tableType.toString() + " " + this.sortingOrder + " " + this.fieldsWithReverseOrder.toString() + " " + this.list.toString()
  }

}

