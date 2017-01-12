
package de.tudresden.inf.lat.tabulas.table

import java.util.ArrayList
import java.util.Collections
import java.util.List
import java.util.Objects
import java.util.Set
import java.util.TreeSet

import de.tudresden.inf.lat.tabulas.datatype.CompositeType
import de.tudresden.inf.lat.tabulas.datatype.CompositeTypeImpl
import de.tudresden.inf.lat.tabulas.datatype.Record

/**
 * This is the default implementation of a sorted table.
 */
class TableImpl extends Table {

  private var tableType: CompositeType = new CompositeTypeImpl()
  private val list: List[Record] = new ArrayList[Record]
  private val sortingOrder: List[String] = new ArrayList[String]
  private val fieldsWithReverseOrder: Set[String] = new TreeSet[String]()

  def this(newType: CompositeType) = {
    this()
    this.tableType = newType
  }

  def this(other: Table) = {
    this()
    this.tableType = other.getType()
    this.list.addAll(other.getRecords())
    if (other.isInstanceOf[Table]) {
      val otherTable: Table = other.asInstanceOf[Table]
      this.sortingOrder.addAll(otherTable.getSortingOrder())
      this.fieldsWithReverseOrder.addAll(otherTable.getFieldsWithReverseOrder())
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
      return this.list.add(record)
    }
  }

  override def getSortingOrder(): List[String] = {
    return this.sortingOrder
  }

  override def setSortingOrder(sortingOrder: List[String]): Unit = {
    this.sortingOrder.clear()
    if (Objects.nonNull(sortingOrder)) {
      this.sortingOrder.addAll(sortingOrder)
    }
  }

  override def getFieldsWithReverseOrder(): Set[String] = {
    return this.fieldsWithReverseOrder
  }

  override def setFieldsWithReverseOrder(fieldsWithReverseOrder: Set[String]): Unit = {
    this.fieldsWithReverseOrder.clear()
    if (Objects.nonNull(fieldsWithReverseOrder)) {
      this.fieldsWithReverseOrder.addAll(fieldsWithReverseOrder)
    }
  }

  override def getRecords(): List[Record] = {
    val ret: List[Record] = new ArrayList[Record]
    ret.addAll(this.list)
    Collections.sort(ret, new RecordComparator(this.sortingOrder))
    return ret
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

