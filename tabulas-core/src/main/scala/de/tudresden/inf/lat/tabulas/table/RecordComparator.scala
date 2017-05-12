
package de.tudresden.inf.lat.tabulas.table

import java.util.{Comparator, Objects}

import de.tudresden.inf.lat.tabulas.datatype.{PrimitiveTypeValue, Record}

import scala.collection.mutable
import scala.collection.mutable.{ArrayBuffer, Set, TreeSet}

/**
  * Comparator for records.
  *
  */
class RecordComparator extends Comparator[Record] {

  private val sortingOrder: mutable.Buffer[String] = new ArrayBuffer[String]
  private val fieldsWithReverseOrder: Set[String] = new TreeSet[String]()

  def this(sortingOrder: mutable.Buffer[String]) = {
    this()
    this.sortingOrder ++= sortingOrder
  }

  def this(sortingOrder: mutable.Buffer[String], fieldsWithReverseOrder: Set[String]) = {
    this()
    this.sortingOrder ++= sortingOrder
    this.fieldsWithReverseOrder ++= fieldsWithReverseOrder
  }

  def getSortingOrder(): mutable.Buffer[String] = {
    return this.sortingOrder
  }

  def getFieldsWithReverseOrder(): Set[String] = {
    return this.fieldsWithReverseOrder
  }

  override def compare(record0: Record, record1: Record): Int = {
    if (Objects.isNull(record0)) {
      if (Objects.isNull(record1)) {
        return 0
      } else {
        return -1
      }
    } else {
      if (Objects.isNull(record1)) {
        return 1
      } else {
        var ret: Int = 0
        val it: Iterator[String] = this.sortingOrder.iterator
        while (it.hasNext && (ret == 0)) {
          val token: String = it.next()
          ret = compareValues(record0.get(token), record1.get(token), this.fieldsWithReverseOrder.contains(token))
        }
        return ret
      }
    }
  }

  def compareValues(optValue0: Option[PrimitiveTypeValue], optValue1: Option[PrimitiveTypeValue], hasReverseOrder: Boolean): Int = {
    if (hasReverseOrder) {
      return compareValues(optValue1, optValue0, false)
    } else {
      if (optValue0.isDefined) {
        if (optValue1.isDefined) {
          return optValue0.get.compareTo(optValue1.get)
        } else {
          return 1
        }
      } else {
        if (optValue1.isDefined) {
          return -1
        } else {
          return 0
        }
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

