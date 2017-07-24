
package de.tudresden.inf.lat.tabulas.table

import java.util.{Comparator, Objects}

import de.tudresden.inf.lat.tabulas.datatype.{PrimitiveTypeValue, Record}

import scala.collection.mutable
import scala.collection.mutable.ArrayBuffer

/**
  * Comparator for records.
  *
  */
class RecordComparator extends Comparator[Record] {

  private val _sortingOrder: mutable.Buffer[String] = new ArrayBuffer[String]
  private val _fieldsWithReverseOrder: mutable.Set[String] = new mutable.TreeSet[String]()

  def this(sortingOrder: mutable.Buffer[String]) = {
    this()
    this._sortingOrder ++= sortingOrder
  }

  def this(sortingOrder: mutable.Buffer[String], fieldsWithReverseOrder: mutable.Set[String]) = {
    this()
    this._sortingOrder ++= sortingOrder
    this._fieldsWithReverseOrder ++= fieldsWithReverseOrder
  }

  def getSortingOrder: mutable.Buffer[String] = {
    return this._sortingOrder
  }

  def getFieldsWithReverseOrder: mutable.Set[String] = {
    return this._fieldsWithReverseOrder
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
        val it: Iterator[String] = this._sortingOrder.iterator
        while (it.hasNext && (ret == 0)) {
          val token: String = it.next()
          ret = compareValues(record0.get(token), record1.get(token), this._fieldsWithReverseOrder.contains(token))
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

  override def equals(obj: Any): Boolean = {
    obj match {
      case other: RecordComparator => return this._sortingOrder.equals(other._sortingOrder)
      case _ => return false
    }
  }

  override def hashCode(): Int = {
    return this._sortingOrder.hashCode()
  }

  override def toString: String = {
    return this._sortingOrder.toString
  }

}

