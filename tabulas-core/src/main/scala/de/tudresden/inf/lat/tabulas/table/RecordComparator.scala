
package de.tudresden.inf.lat.tabulas.table

import java.util.ArrayList
import java.util.Comparator
import java.util.Iterator
import java.util.List
import java.util.Set
import java.util.TreeSet

import de.tudresden.inf.lat.tabulas.datatype.PrimitiveTypeValue
import de.tudresden.inf.lat.tabulas.datatype.Record

/**
 * Comparator for records.
 *
 */
class RecordComparator extends Comparator[Record] {

  private val sortingOrder: ArrayList[String] = new ArrayList[String]
  private val fieldsWithReverseOrder: TreeSet[String] = new TreeSet[String]()

  def this(sortingOrder0: List[String]) = {
    this()
    this.sortingOrder.addAll(sortingOrder0)
  }

  def this(sortingOrder0: List[String], fieldsWithReverseOrder0: TreeSet[String]) = {
    this()
    this.sortingOrder.addAll(sortingOrder0)
    this.fieldsWithReverseOrder.addAll(fieldsWithReverseOrder0)
  }

  def getSortingOrder(): List[String] = {
    this.sortingOrder
  }

  def getFieldsWithReverseOrder(): Set[String] = {
    this.fieldsWithReverseOrder
  }

  override def compare(record0: Record, record1: Record): Int = {
    if (record0 == null) {
      if (record1 == null) { 0 } else { -1 }
    } else {
      if (record1 == null) { 1 } else {
        var ret: Int = 0
        val it: Iterator[String] = this.sortingOrder.iterator()
        while (it.hasNext() && (ret == 0)) {
          val token: String = it.next()
          ret = compareValues(record0.get(token), record1.get(token), this.fieldsWithReverseOrder.contains(token))
        }
        ret
      }
    }
  }

  def compareValues(value0: PrimitiveTypeValue, value1: PrimitiveTypeValue, hasReverseOrder: Boolean): Int = {
    if (hasReverseOrder) {
      compareValues(value1, value0, false)
    } else {
      if (value0 == null) {
        if (value1 == null) { 0 } else { -1 }
      } else {
        if (value1 == null) { 1 } else { value0.compareTo(value1) }
      }
    }
  }

  override def equals(o: Any): Boolean = {
    if (this == o) {
      true
    } else if (o.isInstanceOf[RecordComparator]) {
      val other: RecordComparator = o.asInstanceOf[RecordComparator]
      this.sortingOrder.equals(other.sortingOrder)
    } else {
      false
    }
  }

  override def hashCode(): Int = {
    this.sortingOrder.hashCode()
  }

  override def toString(): String = {
    this.sortingOrder.toString()
  }

}


