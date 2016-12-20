
package de.tudresden.inf.lat.tabulas.table

import java.util.ArrayList
import java.util.Comparator
import java.util.Iterator
import java.util.List
import java.util.Objects
import java.util.Optional
import java.util.Set
import java.util.TreeSet

import de.tudresden.inf.lat.tabulas.datatype.PrimitiveTypeValue
import de.tudresden.inf.lat.tabulas.datatype.Record

/**
 * Comparator for records.
 *
 */
class RecordComparator extends Comparator[Record] {

  private val sortingOrder: List[String] = new ArrayList[String]
  private val fieldsWithReverseOrder: Set[String] = new TreeSet[String]()

  def this(sortingOrder: List[String]) = {
    this()
    this.sortingOrder.addAll(sortingOrder)
  }

  def this(sortingOrder: List[String], fieldsWithReverseOrder: Set[String]) = {
    this()
    this.sortingOrder.addAll(sortingOrder)
    this.fieldsWithReverseOrder.addAll(fieldsWithReverseOrder)
  }

  def getSortingOrder(): List[String] = {
    return this.sortingOrder
  }

  def getFieldsWithReverseOrder(): Set[String] = {
    return this.fieldsWithReverseOrder
  }

  override def compare(record0: Record, record1: Record): Int = {
    if (Objects.isNull(record0)) {
      if (Objects.isNull(record1)) { return 0 } else { return -1 }
    } else {
      if (Objects.isNull(record1)) { return 1 } else {
        var ret: Int = 0
        val it: Iterator[String] = this.sortingOrder.iterator()
        while (it.hasNext() && (ret == 0)) {
          val token: String = it.next()
          ret = compareValues(record0.get(token), record1.get(token), this.fieldsWithReverseOrder.contains(token))
        }
        return ret
      }
    }
  }

  def compareValues(optValue0: Optional[PrimitiveTypeValue], optValue1: Optional[PrimitiveTypeValue], hasReverseOrder: Boolean): Int = {
    if (hasReverseOrder) {
      return compareValues(optValue1, optValue0, false)
    } else {
      if (optValue0.isPresent()) {
        if (optValue1.isPresent()) { return optValue0.get().compareTo(optValue1.get()) } else { return 1 }
      } else {
        if (optValue1.isPresent()) { return -1 } else { return 0 }
      }
    }
  }

  override def equals(o: Any): Boolean = {
    if (o.isInstanceOf[RecordComparator]) {
      val other: RecordComparator = o.asInstanceOf[RecordComparator]
      return this.sortingOrder.equals(other.sortingOrder)
    } else {
      return false
    }
  }

  override def hashCode(): Int = {
    return this.sortingOrder.hashCode()
  }

  override def toString(): String = {
    return this.sortingOrder.toString()
  }

}

