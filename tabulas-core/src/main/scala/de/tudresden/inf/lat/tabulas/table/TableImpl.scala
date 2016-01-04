
package de.tudresden.inf.lat.tabulas.table

import java.util.ArrayList
import java.util.Collections
import java.util.List
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
  private val identifiers: Set[String] = new TreeSet[String]()
  private val sortingOrder: List[String] = new ArrayList[String]
  private val fieldsWithReverseOrder: Set[String] = new TreeSet[String]()

  def this(other: Table) = {
    this()
    this.tableType = other.getType()
    this.list.addAll(other.getRecords())
  }

  def this(newType: CompositeType) = {
    this()
    this.tableType = newType
  }

  override def getType(): CompositeType = {
    this.tableType
  }

  def setType(newType: CompositeType): Unit = {
    this.tableType = newType
  }

  def add(record: Record): Boolean = {
    if (record == null) {
      false
    } else {
      this.list.add(record)
    }
  }

  def addId(id: String): Boolean = {
    this.identifiers.add(id)
  }

  override def getSortingOrder(): List[String] = {
    this.sortingOrder
  }

  def setSortingOrder(sortingOrder0: List[String]): Unit = {
    this.sortingOrder.clear()
    if (sortingOrder0 != null) {
      this.sortingOrder.addAll(sortingOrder0)
    }
  }

  override def getFieldsWithReverseOrder(): Set[String] = {
    this.fieldsWithReverseOrder
  }

  def setFieldsWithReverseOrder(fieldsWithReverseOrder0: Set[String]): Unit = {
    this.fieldsWithReverseOrder.clear()
    if (fieldsWithReverseOrder0 != null) {
      this.fieldsWithReverseOrder.addAll(fieldsWithReverseOrder0)
    }
  }

  override def getRecords(): List[Record] = {
    val ret: List[Record] = new ArrayList[Record]
    ret.addAll(this.list)
    Collections.sort(ret, new RecordComparator(this.sortingOrder))
    ret
  }

  override def getIdentifiers(): Set[String] = {
    this.identifiers
  }

  override def hashCode(): Int = {
    this.sortingOrder.hashCode() + 0x1F * (this.fieldsWithReverseOrder.hashCode() + 0x1F * (this.list.hashCode() + 0x1F * this.tableType.hashCode()))
  }

  override def equals(obj: Any): Boolean = {
    if (this == obj) {
      true
    } else if (obj.isInstanceOf[Table]) {
      val other: Table = obj.asInstanceOf[Table]
      getSortingOrder().equals(other.getSortingOrder()) &&
        getFieldsWithReverseOrder().equals(other.getFieldsWithReverseOrder()) &&
        getType().equals(other.getType()) &&
        getRecords().equals(other.getRecords()) &&
        getIdentifiers().equals(other.getIdentifiers())
    } else {
      false
    }
  }

  override def toString(): String = {
    this.tableType.toString() + " " + this.sortingOrder + " " + this.fieldsWithReverseOrder.toString() + " " + this.list.toString()
  }

}

