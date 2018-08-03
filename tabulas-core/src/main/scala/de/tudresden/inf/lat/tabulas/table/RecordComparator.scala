
package de.tudresden.inf.lat.tabulas.table

import java.util.{Comparator, Objects}

import de.tudresden.inf.lat.tabulas.datatype.{PrimitiveTypeValue, Record}

import scala.collection.mutable

/** Comparator for records.
  *
  */
class RecordComparator extends Comparator[Record] {

  private val _sortingOrder = new mutable.ArrayBuffer[String]
  private val _fieldsWithReverseOrder = new mutable.TreeSet[String]()

  def this(sortingOrder: Seq[String]) = {
    this()
    this._sortingOrder ++= sortingOrder
  }

  def this(sortingOrder: Seq[String], fieldsWithReverseOrder: Set[String]) = {
    this()
    this._sortingOrder ++= sortingOrder
    this._fieldsWithReverseOrder ++= fieldsWithReverseOrder
  }

  def getSortingOrder: Seq[String] = { this._sortingOrder }

  def getFieldsWithReverseOrder: Set[String] = { this._fieldsWithReverseOrder.toSet }

  override def compare(record0: Record, record1: Record): Int = {
    var result: Int = 0
    if (Objects.isNull(record0)) {
      if (Objects.isNull(record1)) {
        result = 0
      } else {
        result = -1
      }
    } else {
      if (Objects.isNull(record1)) {
        result = 1
      } else {
        result = 0
        val it: Iterator[String] = this._sortingOrder.iterator
        while (it.hasNext && (result == 0)) {
          val token: String = it.next()
          result = compareValues(record0.get(token), record1.get(token), this._fieldsWithReverseOrder.contains(token))
        }
      }
    }
    result
  }

  def compareValues(optValue0: Option[PrimitiveTypeValue], optValue1: Option[PrimitiveTypeValue], hasReverseOrder: Boolean): Int = {
    var result: Int = 0
    if (hasReverseOrder) {
      result = compareValues(optValue1, optValue0, false)
    } else {
      if (optValue0.isDefined) {
        if (optValue1.isDefined) {
          result = optValue0.get.compareTo(optValue1.get)
        } else {
          result = 1
        }
      } else {
        if (optValue1.isDefined) {
          result = -1
        } else {
          result = 0
        }
      }
    }
    result
  }

  override def equals(obj: Any): Boolean = {
    var result: Boolean = false
    obj match {
      case other: RecordComparator => result = this._sortingOrder.equals(other._sortingOrder)
      case _ => result = false
    }
    result
  }

  override def hashCode(): Int = { this._sortingOrder.hashCode() }

  override def toString: String = { this._sortingOrder.toString }

}

object RecordComparator {

  def apply(): RecordComparator = new RecordComparator

}
