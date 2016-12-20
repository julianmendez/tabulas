
package de.tudresden.inf.lat.tabulas.table

import java.util.ArrayList
import java.util.Collections
import java.util.List
import java.util.Set
import java.util.TreeSet
import de.tudresden.inf.lat.tabulas.datatype.CompositeType
import de.tudresden.inf.lat.tabulas.datatype.CompositeTypeImpl
import de.tudresden.inf.lat.tabulas.datatype.Record
import java.util.Objects

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
    return this.tableType
  }

  def setType(newType: CompositeType): Unit = {
    this.tableType = newType
  }

  def add(record: Record): Boolean = {
    if (Objects.isNull(record)) {
      return false
    } else {
      return this.list.add(record)
    }
  }

  def addId(id: String): Boolean = {
    return this.identifiers.add(id)
  }

  override def getSortingOrder(): List[String] = {
    return this.sortingOrder
  }

  def setSortingOrder(sortingOrder: List[String]): Unit = {
    this.sortingOrder.clear()
    if (Objects.nonNull(sortingOrder)) {
      this.sortingOrder.addAll(sortingOrder)
    }
  }

  override def getFieldsWithReverseOrder(): Set[String] = {
    return this.fieldsWithReverseOrder
  }

  def setFieldsWithReverseOrder(fieldsWithReverseOrder: Set[String]): Unit = {
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

  override def getIdentifiers(): Set[String] = {
    return this.identifiers
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
        getRecords().equals(other.getRecords()) &&
        getIdentifiers().equals(other.getIdentifiers())
    } else {
      return false
    }
  }

  override def toString(): String = {
    return this.tableType.toString() + " " + this.sortingOrder + " " + this.fieldsWithReverseOrder.toString() + " " + this.list.toString()
  }

}

